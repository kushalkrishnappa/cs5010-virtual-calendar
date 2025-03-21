package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CopyEventCommand extends Command {

  private LocalDateTime sourceStartDateTime;

  private LocalDate sourceStartDate;

  private LocalDate sourceEndDate;

  private LocalDate targetStartDate;

  private LocalDateTime targetStartDateTime;

  private String targetCalendarName;

  private Integer copiedEvents;

  CopyEventCommand() {
    sourceStartDate = null;
    sourceEndDate = null;
    targetStartDate = null;
    targetStartDateTime = null;
    targetCalendarName = null;
  }

  @Override
  Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    try {
      switch (commandScanner.next()) {
        case "event":
          copySingleEvent(commandScanner);
          break;
        case "events":
          copyMultipleEvents(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: copy (event|events) ...");
      }
    } catch (NoSuchElementException e) {
      System.out.println("printing no such element");
      throw new ParseCommandException(
          "Invalid command format: copy (event|events) (eventName on|on|between)...");
    }
    return this;
  }

  private void copySingleEvent(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    // parse the <eventName>
    try {
      String eventName = parseOptionalQuoted(commandScanner);
    } catch (ParseCommandException e) {
      throw new ParseCommandException(
          e.getMessage() + "copy event <eventName> ...");
    }

    // parse the "on" keyword
    if (!commandScanner.next().equals("on")) {
      throw new ParseCommandException(
          "Invalid command format: copy event <eventName> on ...");
    }

    // parse the source calendar's start dateTime
    sourceStartDateTime = parseDateTime(commandScanner);

    // parse target calendar details
    parseTargerCalendarDetails(commandScanner);
  }

  private void copyMultipleEvents(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    try {
      switch (commandScanner.next()) {
        case "on":
          copyEventsOnDate(commandScanner);
          break;
        case "between":
          copyEventsBetweenDates(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: copy events (on|between) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: copy events (on|between) ...");
    }
  }

  private void copyEventsOnDate(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    // parse the source calendar's start date
    sourceStartDate = parseDate(commandScanner);

    // parse the target calendar details
    parseTargerCalendarDetails(commandScanner);
  }

  private void copyEventsBetweenDates(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    // parse the source calendar's start date
    sourceStartDate = parseDate(commandScanner);

    // parse the "and" keyword
    if (!commandScanner.next().equals("and")) {
      throw new ParseCommandException(
          "Invalid command format: copy events between <startDateString> and <endDateString> ...");
    }

    // parse the source calendar's end date
    sourceEndDate = parseDate(commandScanner);

    // parse the target calendar details
    parseTargerCalendarDetails(commandScanner);
  }

  private void parseTargerCalendarDetails(Scanner commandScanner)
      throws ParseCommandException {
    // parse the "--target" option keyword
    if (!commandScanner.next().equals("--target")) {
      throw new ParseCommandException(
          "Invalid command format: Missing --target flag");
    }

    // parse the target "<calendarName>"
    targetCalendarName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (targetCalendarName == null) {
      throw new ParseCommandException(
          "Invalid command format: --target <calendarName> ...");
    }

    // Expect "to" keyword
    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException(
          "Invalid command format: --target <calendarName> to ...");
    }

    // if copy single event, parse the dateTime else parse the date
    if (sourceStartDateTime != null) {
      targetStartDateTime = parseDateTime(commandScanner);
    } else if (sourceStartDate != null) {
      targetStartDate = parseDate(commandScanner);
    }
  }

  private String parseOptionalQuoted(Scanner commandScanner) throws ParseCommandException {
    String token = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (token == null) {
      throw new ParseCommandException("Invalid command format: ");
    }
    return token.startsWith("\"") ? token.substring(1, token.length() - 1) : token;
  }

  private LocalDate parseDate(Scanner commandScanner)
      throws ParseCommandException {
    try {
      String dateString = commandScanner.next();
      return LocalDate.parse(dateString, CalendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid date format: " + CalendarController.dateFormat);
    }
  }

  private LocalDateTime parseDateTime(Scanner commandScanner)
      throws ParseCommandException {
    try {
      String dateTimeString = commandScanner.next();
      return LocalDateTime.parse(dateTimeString, CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid datetime format: " + CalendarController.dateTimeFormat);
    }
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    //TODO: Implement logic to copy events

  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (copiedEvents > 0) {
      controllerUtility.promptOutput("Successfully copied event(s) to " + targetCalendarName);
    } else {
      controllerUtility.promptOutput("No events were copied to " + targetCalendarName);
    }
  }
}
