package controller;

import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import exception.ParseCommandException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  private final ControllerMode mode;
  private final IView view;
  private final IModel model;
  static final String dateFormat = "yyyy-MM-dd";
  static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
  static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm";
  static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
      dateTimeFormat);

  final ControllerUtility controllerUtility;
  private boolean exitFlag;

  class ControllerUtility {

    void promptOutput(String message) {
      if (mode == ControllerMode.INTERACTIVE) {
        view.displayMessage(message);
      }
    }
  }

  public CalendarController(IModel model, IView view, ControllerMode mode) {
    this.model = model;
    this.view = view;
    this.mode = mode;
    controllerUtility = new ControllerUtility();
    exitFlag = false;
  }

  @Override
  public void run() {
    Objects.requireNonNull(mode, "mode cannot be null");
    Objects.requireNonNull(view, "view cannot be null");
    Objects.requireNonNull(model, "model cannot be null");

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
        return;
      }
      promptUserInput();
    }
    // headless mode should have *exit* as last command
    if (mode == ControllerMode.HEADLESS) {
      promptError("exit command was not specified in the passed file");
    }
  }

  private void promptUserInput() {
    if (mode == ControllerMode.INTERACTIVE) {
      view.displayMessage("calApp> ");
    }
  }

  private void promptError(String message) {
    view.displayError(message);
    if (mode == ControllerMode.HEADLESS) {
      exitProgram();
    }
  }


  private void executeCommand(Scanner lineScanner) {
    controller.Command command;

    String firstToken = lineScanner.next();
    if (firstToken.equals("exit")) {
      exitProgram();
      return;
    }

    command = CommandFactory.createCommand(firstToken);
    if (command == null) {
      promptError("Unknown command\n");
      return;
    }

    try {
      command.parseCommand(lineScanner);
    } catch (ParseCommandException e) {
      promptError(e.getMessage()+"\n");
      return;
    }

    try {
      command.executeCommand(model);
    } catch (EventConflictException | CalendarExportException | InvalidDateTimeRangeException |
             IllegalArgumentException e) {
      promptError(e.getMessage());
      return;
    }

    command.promptResult(controllerUtility);
  }


  private static class CommandFactory {

    private static final Map<String, Supplier<Command>> commandMap = new HashMap<>();

    static {
      commandMap.put("create", CreateEventCommand::new);
      commandMap.put("edit", EditEventCommand::new);
      commandMap.put("print", PrintEventsCommand::new);
      commandMap.put("export", ExportCalendarCommand::new);
      commandMap.put("show", ShowStatusCommand::new);
    }

    static Command createCommand(String firstToken) {
      Supplier<Command> commandSupplier = commandMap.get(firstToken);
      if (commandSupplier != null) {
        return commandSupplier.get();
      }
      return null;
    }
  }

  private void exitProgram() {
    view.displayMessage("Bye...\n");
    exitFlag= true;
  }

}
