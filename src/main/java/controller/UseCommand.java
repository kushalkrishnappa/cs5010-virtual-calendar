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

public class UseCommand extends Command {


  private String calendarName;

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

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    if (Objects.isNull(controllerUtility.getCalendarEntry(calendarName))) {
      throw new CalendarNotPresentException("Calendar with the provided name doesn't exists");
    }
    controllerUtility.setCurrentCalendar(calendarName);
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Switched to " + calendarName);
  }
}
