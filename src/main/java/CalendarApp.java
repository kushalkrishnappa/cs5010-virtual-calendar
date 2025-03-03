import controller.CalendarController;
import controller.IController;
import java.io.FileNotFoundException;
import model.Calendar;
import model.ICalendar;
import view.CommanLineInteractiveView;
import view.HeadlessView;
import view.IView;

/**
 * CalendarApp is the main class for the Calendar application.
 */
public class CalendarApp {

  private static final String INTERACTIVE_MODE = "interactive";
  private static final String HEADLESS_MODE = "headless";

  /**
   * The main method for the Calendar application.
   *
   * @param args The command line arguments
   */
  public static void main(String[] args) {
    // create the model
    IView view;
    IController controller;
    ICalendar model = new Calendar();

    if (args.length < 2) {
      System.err.println("Usage: java CalendarApp.java --mode [interactive | headless filepath]");
      return;
    }

    // parse the command line arguments for --mode flag
    String modeFlag = args[0];
    if (!modeFlag.equals("--mode")) {
      System.err.println("Usage: java CalendarApp.java --mode [interactive | headless filepath]");
      return;
    }

    // parse the command line arguments for mode type
    String mode = args[1];
    switch (mode) {
      case INTERACTIVE_MODE:
        controller = new CalendarController(model, System.in);
        view = new CommanLineInteractiveView();
        controller.addView(view);
        break;
      case HEADLESS_MODE:
        if (args.length < 3) {
          System.err.println("Usage: java CalendarApp.java --mode headless filepath");
          return;
        }
        try {
          controller = new CalendarController(model, args[2]);
        } catch (FileNotFoundException e) {
          System.err.println("File not found: " + args[2]);
          return;
        }
        view = new HeadlessView();
        controller.addView(view);
        break;
      default:
        System.err.println("Usage: java CalendarApp.java --mode [interactive | headless filepath]");
        return;
    }

    controller.startProgram();

  }
}
