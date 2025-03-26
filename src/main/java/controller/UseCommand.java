package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.CalendarNotPresentException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class represents an implementation of the abstract Command class to switch the current
 * calendar in use. It parses the command, executes the switch operation, and prompts the result.
 */
public class UseCommand extends Command {

  private String calendarName;

  /**
   * This method starts parsing the `use` command from the Scanner object. It checks if the `use`
   * command contains `calendar` keyword and --name tag before calling the appropriate method
   * continuing the parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return the command object
   * @throws ParseCommandException    if the command provided is invalid
   * @throws InvalidTimeZoneException if the time zone provided is invalid
   */
  @Override
  Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    try {
      if (!commandScanner.next().equals("calendar")) {
        throw new ParseCommandException("Invalid command format: use calendar ...");
      }

      if (!commandScanner.next().equals("--name")) {
        throw new ParseCommandException("Invalid command format: use calendar --name ...");
      }

      parseCalendarName(commandScanner);
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: use calendar --name <calendar_name> ...");
    }
    return this;
  }

  /**
   * This method parses the calendar name from the command scanner. It checks if the calendar name
   * is provided in quotes or not. If not, it throws a ParseCommandException.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input)
   * @throws ParseCommandException if the command provided is invalid
   */
  private void parseCalendarName(Scanner commandScanner) throws ParseCommandException {
    calendarName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (Objects.isNull(calendarName)) {
      throw new ParseCommandException(
          "Invalid command format: use calendar --name <calendar_name> ...");
    }
    calendarName =
        calendarName.startsWith("\"")
            ? calendarName.substring(1, calendarName.length() - 1)
            : calendarName;
  }

  /**
   * Execute the `use` command to switch the current calendar in use. It checks if the calendar
   * already exists, if not present it sets the current calendar to the provided name.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar
   * @throws EventConflictException if there is a conflict with the event
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    if (Objects.isNull(controllerUtility.getCalendarEntry(calendarName))) {
      throw new CalendarNotPresentException("Calendar with the provided name doesn't exists");
    }
    controllerUtility.setCurrentCalendar(calendarName);
  }

  /**
   * Prompt the result of `use` command with message.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Switched to " + calendarName);
  }
}
