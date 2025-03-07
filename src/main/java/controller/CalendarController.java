package controller;

import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  private final ControllerMode mode;
  private final IView view;
  private final IModel model;
  final DateTimeFormatter dateTimeFormatter;
  final DateTimeFormatter dateFormatter;

  public CalendarController(IModel model, IView view, ControllerMode mode) {
    this.model = model;
    this.view = view;
    this.mode = mode;
    dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

  IModel getModel() {
    return this.model;
  }

  void promptOutput(String message) {
    if (mode == ControllerMode.INTERACTIVE) {
      view.displayMessage(message);
    }
  }

  private void executeCommand(Scanner lineScanner) {
    controller.Command command = null;
    switch (lineScanner.next()) {
      case "create":
        command = new CreateEventCommand(this, lineScanner);
        break;
      case "edit":
        command = new EditEventCommand(this, lineScanner);
        break;
      case "print":
        command = new PrintEventsCommand(this, lineScanner);
        break;
      case "export":
        command = new ExportCalendarCommand(this, lineScanner);
        break;
      case "show":
        command = new ShowStatusCommand(this, lineScanner);
        break;
      case "exit":
        exitProgram();
        break;
      case "help":
        break;
      default:
        promptError("Unknown command");
        return;
    }

    try {
      command.parseCommand();
    } catch (NoSuchElementException e) { // thrown if scanner cannot find nextToken
      promptError("Invalid command format");
      return;
    } catch (ParseCommandException e) {
      promptError(e.getMessage());
      return;
    }

    try {
      command.executeCommand();
    } catch (EventConflictException | CalendarExportException e) {
      promptError(e.getMessage());
      return;
    }

    command.promptResult();
  }

  private void exitProgram() {
    System.exit(0);
  }

}
