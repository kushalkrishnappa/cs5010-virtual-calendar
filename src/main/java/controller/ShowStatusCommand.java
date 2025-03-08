package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import model.IModel;

class ShowStatusCommand extends Command {

  private LocalDateTime dateTime;
  private Boolean isBusy;

  @Override
  void parseCommand(Scanner commandScanner) throws ParseCommandException {
    try {
      if (!commandScanner.next().equals("status")) {
        throw new ParseCommandException("Invalid command format: show status ...");
      }
      if (!commandScanner.next().equals("on")) {
        throw new ParseCommandException("Invalid command format: show status on ...");
      }

      try {
        dateTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid dateTime format: " + CalendarController.dateFormatter);
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException("Invalid command format: show status on <dateTime>");
    }
  }

  @Override
  void executeCommand(IModel model) throws CalendarExportException, EventConflictException {
    isBusy = model.isBusy(dateTime);
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (isBusy) {
      controllerUtility.promptOutput("Busy at " + dateTime);
    } else {
      controllerUtility.promptOutput("Available at " + dateTime);
    }
  }
}
