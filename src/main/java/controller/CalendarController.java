package controller;

import controller.command.Command;
import controller.command.CreateEventCommand;
import controller.command.ParseCommandException;
import exception.EventConflictException;
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
  private final DateTimeFormatter dateTimeFormatter;
  private final DateTimeFormatter dateFormatter;

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


  private void executeCommand(Scanner lineScanner) {
    Command command = null;
    try {
      switch (lineScanner.next()) {
        case "create":
          command = new CreateEventCommand(model, lineScanner, dateTimeFormatter, dateFormatter);
          break;
        case "edit":
          break;
        case "print":
          break;
        case "export":
          break;
        case "show":
          break;
        case "exit":
          exitProgram();
          break;
        default:
          promptError("Unknown command");
          return;
      }
    } catch (NoSuchElementException e) { // thrown if scanner cannot find nextToken
      promptError("Invalid command format");
      return;
    } catch (ParseCommandException e) {
      promptError(e.getMessage());
      return;
    }

    try {
      command.execute();
    } catch (EventConflictException e) {
      promptError(e.getMessage());
    }
  }

  private void exitProgram() {
    System.exit(0);
  }

}
