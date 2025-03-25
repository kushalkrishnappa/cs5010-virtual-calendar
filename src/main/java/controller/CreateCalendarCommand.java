package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.CreateCalendarException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.util.Objects;
import java.util.Scanner;

class CreateCalendarCommand extends Command {

  private String calendarName;
  private String zoneIdString;

  CreateCalendarCommand() {
    calendarName = null;
    zoneIdString = null;
  }

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

  private void parseTimeZone(Scanner commandScanner) throws ParseCommandException {
    zoneIdString = commandScanner.next();
  }


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

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Created calendar " + calendarName);
  }
}
