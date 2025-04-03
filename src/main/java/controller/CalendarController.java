package controller;

import exception.CalendarExportException;
import exception.CalendarNotPresentException;
import exception.CalendarNotSelectedException;
import exception.CreateCalendarException;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import model.IModel;
import view.IView;

/**
 * This class represents the controller of the calendar application. Commands from the CLI and
 * Headless interactions are gathered and processed before dispatching the request to model to
 * perform operations on the calendar events.
 *
 * <p>This also handles the context of creating and editing the calendar. This provides the context
 * of multiple calendars to the view.
 */
public class CalendarController implements IController {

  ControllerMode mode;

  final IView view;

  final Supplier<IModel> modelFactory;

  private final Map<String, CalendarEntry> calendars;

  private String currentCalendar;

  static final String dateFormat = "yyyy-MM-dd";

  static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);

  static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm";

  static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);

  final ControllerUtility controllerUtility;

  private boolean exitFlag;

  /**
   * Utility class for the controller.
   */
  class ControllerUtility {

    void promptOutput(String message) {
      if (mode != ControllerMode.HEADLESS) {
        view.displayMessage(message + "\n");
      }
    }

    Supplier<IModel> getModelFactory() {
      return modelFactory;
    }

    CalendarEntry getCurrentCalendar() {
      if (Objects.isNull(currentCalendar)) {
        throw new CalendarNotSelectedException(
            "No calendar is set. Use \"use calendar --name <calName>\" to select a calendar");
      }
      return calendars.get(currentCalendar);
    }

    void setCurrentCalendar(String currentCalendarName) {
      currentCalendar = currentCalendarName;
    }

    CalendarEntry getCalendarEntry(String calendar) {
      return calendars.get(calendar);
    }

    String[] getAllCalendarNames() {
      return calendars.keySet().toArray(new String[0]);
    }

    void addCalendarEntry(String calendar, CalendarEntry calendarEntry) {
      calendars.put(calendar, calendarEntry);
    }

    CalendarEntry removeCalendarEntry(String calendar) {
      return calendars.remove(calendar);
    }

    void exitProgram() {
      exitFlag = true;
    }
  }

  /**
   * Constructor for initializing the class attributes for the controller.
   *
   * @param modelFactory The model factory for the calendar application
   * @param view         The view for the calendar application
   * @param mode         The mode of the controller
   */
  public CalendarController(Supplier<IModel> modelFactory, IView view, ControllerMode mode) {
    this.modelFactory = modelFactory;
    this.view = view;
    this.mode = mode;
    this.calendars = new HashMap<>();
    this.currentCalendar = null;
    controllerUtility = new ControllerUtility();
    exitFlag = false;
    Objects.requireNonNull(mode, "mode cannot be null");
    Objects.requireNonNull(view, "view cannot be null");
    Objects.requireNonNull(modelFactory, "model cannot be null");
  }

  /**
   * This method will start the controller and will run the command line interface in interactive
   * mode or headless mode. This will also handle the command line arguments and will dispatch the
   * request to the model to perform operations on the calendar events.
   */
  @Override
  public void run() {

    Scanner scanner = new Scanner(view.getInputStream());
    promptUserInput();
    while (scanner.hasNextLine()) {
      Scanner lineScanner = new Scanner(scanner.nextLine());
      if (!lineScanner.hasNext()) {
        promptUserInput();
        continue;
      }
      executeCommand(lineScanner);
      if (exitFlag) {
        exitProgram();
        return;
      }
      promptUserInput();
    }
    // headless mode should have *exit* as last command
    if (mode == ControllerMode.HEADLESS) {
      promptError("exit command was not specified in the passed file\n");
    }
  }

  /**
   * Prompt the user for input.
   */
  private void promptUserInput() {
    if (mode == ControllerMode.INTERACTIVE) {
      view.displayMessage("calApp ["
          + (Objects.nonNull(currentCalendar) ? currentCalendar : "No calendar in use")
          + "]> ");
    }
  }

  /**
   * Prompt the user with an error message.
   *
   * @param message The error message to be displayed.
   */
  private void promptError(String message) {
    view.displayError(message);
    if (mode == ControllerMode.HEADLESS) {
      exitFlag = true;
    }
  }

  /**
   * On execute, the command will be created and parsed before executing it.
   *
   * @param lineScanner Scanner object that contains the command
   */
  private void executeCommand(Scanner lineScanner) {
    controller.Command command;
    String firstToken = lineScanner.next();

    command = CommandFactory.createCommand(firstToken);
    if (command == null) {
      promptError("Unknown command\n");
      return;
    }

    try {
      command = command.parseCommand(lineScanner);
    } catch (ParseCommandException | InvalidTimeZoneException e) {
      promptError(e.getMessage() + "\n");
      return;
    }

    try {
      command.executeCommand(controllerUtility);
    } catch (CalendarNotSelectedException | CalendarNotPresentException | EventConflictException
             | CalendarExportException | InvalidDateTimeRangeException | InvalidTimeZoneException
             | IllegalArgumentException | CreateCalendarException e) {
      promptError(e.getMessage() + "\n");
      return;
    }

    command.promptResult(controllerUtility);
  }

  /**
   * Factory class for creating commands.
   */
  private static class CommandFactory {

    private static final Map<String, Supplier<Command>> commandMap = new HashMap<>();

    static {
      commandMap.put("use", UseCommand::new);
      commandMap.put("exit", ExitCommand::new);
      commandMap.put("create", CreateEventCommand::new);
      commandMap.put("copy", CopyEventCommand::new);
      commandMap.put("edit", EditEventCommand::new);
      commandMap.put("print", PrintEventsCommand::new);
      commandMap.put("export", ExportCalendarCommand::new);
      commandMap.put("show", ShowStatusCommand::new);
    }

    /**
     * Create a Command object based on the first token of the command.
     *
     * @param firstToken The first token of the command
     * @return The Command object corresponding to the first token
     */
    static Command createCommand(String firstToken) {
      Supplier<Command> commandSupplier = commandMap.get(firstToken);
      if (commandSupplier != null) {
        return commandSupplier.get();
      }
      return null;
    }
  }

  /**
   * Exit the program.
   */
  private void exitProgram() {
    if (mode == ControllerMode.INTERACTIVE) {
      view.displayMessage("Bye...\n");
    }
  }
}
