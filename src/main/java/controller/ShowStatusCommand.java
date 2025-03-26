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
 * The class represents an implementation of the abstract Command class to show the status of a
 * specific date and time.
 */
class ShowStatusCommand extends Command {

  private LocalDateTime dateTime;

  private Boolean isBusy;

  /**
   * The constructor for ShowStatusCommand class initializes the dateTime and isBusy to null.
   */
  ShowStatusCommand() {
    dateTime = null;
    isBusy = null;
  }

  /**
   * This method starts parsing the show status command from the Scanner object. It checks if the
   * command contains `status` keyword and the `on` keyword before formatting the dateTime.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * The method makes a call to model to check if the dateTime is busy or not. It uses the
   * controllerUtility object to access the current calendar and its model.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if the calendar export fails
   * @throws EventConflictException if there is an event conflict
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    isBusy = controllerUtility.getCurrentCalendar().model.isBusy(dateTime);
  }

  /**
   * Prompt the result of show status command to the user with a message indicating busy or
   * available.
   *
   * @param controllerUtility the controller utility object
   */
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
