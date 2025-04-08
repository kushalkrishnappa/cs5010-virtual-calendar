package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.CreateCalendarException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class represents an implementation of abstract Command class to create a new calendar.
 *
 * <p>This class is responsible for parsing the command to create a new calendar, executing the
 * command to create the calendar, and prompting the result of the command.
 */
class CreateCalendarCommand extends Command {

  private String calendarName;

  private String zoneIdString;

  /**
   * The constructor for CreateCalendarCommand class initializes the calendarName and zoneIdString
   * to null.
   *
   * <p>It will be set upon parsing the command.
   */
  CreateCalendarCommand() {
    calendarName = null;
    zoneIdString = null;
  }

  CreateCalendarCommand(String calendarName, String zoneIdString) {
    this.calendarName = calendarName;
    this.zoneIdString = zoneIdString;
  }

  /**
   * This method starts parsing the create calendar command from the Scanner object. It checks if
   * the create calendar command contains `--name` or `--timezone` keyword before calling the
   * appropriate method to continue the parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input).
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   * @throws InvalidTimeZoneException if the time zone provided is invalid
   */
  @Override
  Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    while (commandScanner.hasNext()) {
      switch (commandScanner.next()) {
        case "--name":
          parseCalendarName(commandScanner);
          break;
        case "--timezone":
          parseTimeZone(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: create calendar --name <calName> --timezone area/location");
      }
    }
    if (Objects.isNull(calendarName) || Objects.isNull(zoneIdString)) {
      throw new ParseCommandException("Please Specify calendarName and zoneId");
    }
    return this;
  }

  /**
   * This method parses the time zone from the command scanner. It reads the next token from the
   * scanner and sets the zoneIdString to the value read.
   *
   * @param commandScanner the scanner object that reads the command
   */
  private void parseTimeZone(Scanner commandScanner) {
    zoneIdString = commandScanner.next();
  }

  /**
   * This method parses the calendar name from the command scanner. It reads the next token from
   * the scanner and sets the calendarName to the value read. If the calendar name is not found,
   * it throws a ParseCommandException.
   *
   * @param commandScanner the scanner object that reads the command
   * @throws ParseCommandException if the command provided is invalid
   */
  private void parseCalendarName(Scanner commandScanner) throws ParseCommandException {
    calendarName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (Objects.isNull(calendarName)) {
      throw new ParseCommandException("Invalid command format: create calendar --name <name>");
    }
    calendarName =
        calendarName.startsWith("\"")
            ? calendarName.substring(1, calendarName.length() - 1)
            : calendarName;
  }

  /**
   * Execute the `create calendar` command. This method doesn't make a call to model instead uses
   * the controller utility to add the calendar entry to the model.
   *
   * <p>The context of multiple calendars is handled by the controller.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar
   * @throws EventConflictException if there is a conflict with the event
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    if (Objects.nonNull(controllerUtility.getCalendarEntry(calendarName))) {
      throw new CreateCalendarException("Calendar with the provided name already exists");
    }
    controllerUtility.addCalendarEntry(calendarName,
        CalendarEntry.getBuilder()
            .setModel(controllerUtility.getModelFactory().get())
            .setZoneId(zoneIdString)
            .build());
  }

  /**
   * Prompt the result of the `create calendar` command to the user.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Created calendar " + calendarName);
  }
}
