package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ShowStatusCommand class implements Command and execute the command to show the status of the
 * calendar at a specific time.
 */
class ShowStatusCommand extends Command {

  private LocalDateTime dateTime;

  private Boolean isBusy;

  /**
   * Constructor for ShowStatusCommand.
   */
  ShowStatusCommand() {
    dateTime = null;
    isBusy = null;
  }

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
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
            "Invalid dateTime format: " + CalendarController.dateTimeFormat);
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException("Invalid command format: show status on <dateTime>");
    }
    return this;
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    isBusy = controllerUtility.getCurrentCalendar().model.isBusy(dateTime);
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    StringBuilder output = new StringBuilder();
    if (isBusy) {
      output.append("Busy at ");
    } else {
      output.append("Available at ");
    }
    controllerUtility.promptOutput(output.append(dateTime).toString());
  }
}
