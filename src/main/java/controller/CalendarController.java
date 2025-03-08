package controller;

import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  private final ControllerMode mode;
  private final IView view;
  private final IModel model;
  static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm");
  static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  final ControllerUtility controllerUtility;

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
      promptUserInput();
    }
    // headless mode should have *exit* as last command
    if(mode == ControllerMode.HEADLESS){
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
    }

    command = getCommand(firstToken);
    if (command == null) {
      promptError("Unknown command");
      return;
    }

    try {
      command.parseCommand(lineScanner);
    } catch (ParseCommandException e) {
      promptError(e.getMessage());
      return;
    }

    try {
      command.executeCommand(model);
    } catch (EventConflictException | CalendarExportException e) {
      promptError(e.getMessage());
      return;
    }

    command.promptResult(controllerUtility);
  }

  private Command getCommand(String firstToken) {
    switch (firstToken) {
      case "create":
        return new CreateEventCommand();
      case "edit":
        return new EditEventCommand();
      case "print":
        return new PrintEventsCommand();
      case "export":
        return new ExportCalendarCommand();
      case "show":
        return new ShowStatusCommand();
//      case "help":
//        break;
      default:
        return null;

    }
  }

  private void exitProgram() {
    view.displayMessage("Bye...");
    System.exit(0);
  }

}
