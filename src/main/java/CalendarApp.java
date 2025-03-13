import controller.CalendarController;
import controller.ControllerMode;
import controller.IController;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import model.CalendarModel;
import model.IModel;
import view.CommandLineView;
import view.IView;

/**
 * CalendarApp is the main class for the Calendar application.
 */
public class CalendarApp {

  /**
   * The main method for the Calendar application.
   *
   * @param args The command line arguments
   */
  public static void main(String[] args) {
    // create the model
    IView view;
    IController controller;
    IModel model = new CalendarModel();

    if (args.length < 2) {
      promptUsageError();
      return;
    }

    // parse the command line arguments for --mode flag
    String modeFlag = args[0];
    if (!modeFlag.equals("--mode")) {
      promptUsageError();
      return;
    }

    // parse the command line arguments for mode type
    ControllerMode mode;
    try {
      mode = ControllerMode.getControllerMode(args[1]);
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid mode: " + args[1]);
      return;
    }
    switch (mode) {
      case INTERACTIVE:
        view = new CommandLineView(new InputStreamReader(System.in), new PrintStream(System.out));
        break;
      case HEADLESS:
        if (args.length < 3) {
          System.err.println("Usage: java CalendarApp.java --mode headless filepath");
          return;
        }
        try {
          view = new CommandLineView(new BufferedReader(new FileReader(args[2])),
              new PrintStream(System.out));
        } catch (FileNotFoundException e) {
          System.err.println("File not found: " + args[2]);
          return;
        }
        break;
      default:
        // flow will not reach here
        return;
    }

    controller = new CalendarController(model, view, mode);
    System.out.println("Starting calendar in " + mode + " mode...");
    controller.run();
  }

  private static void promptUsageError() {
    System.err.println("Usage: java CalendarApp.java --mode [interactive | headless filepath]");
  }

}
