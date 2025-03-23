package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class represents an implementation of abstract Command class to copy events from one
 * calendar to another. It parses the command, executes the copy operation, and prompts the result.
 */
public class CopyEventCommand extends Command {

  private LocalDateTime sourceStartDateTime;

  private LocalDate sourceStartDate;

  private LocalDate sourceEndDate;

  private LocalDate targetStartDate;

  private LocalDateTime targetStartDateTime;

  private String targetCalendarName;

  private String eventName;

  private Integer copiedEvents;

  private final StringBuilder conflictMessages;

  /**
   * The constructor for the CopyEventCommand class initializes the class variables to null.
   */
  CopyEventCommand() {
    sourceStartDateTime = null;
    sourceStartDate = null;
    sourceEndDate = null;
    targetStartDate = null;
    targetStartDateTime = null;
    targetCalendarName = null;
    eventName = null;
    copiedEvents = null;
    conflictMessages = new StringBuilder();
  }

  /**
   * This method starts parsing the copy command from the Scanner object. It checks if the copy
   * command contains `event` or `events` keyword before calling the appropriate method continuing
   * the parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input).
   * @return the command object of type CopyEventCommand
   * @throws ParseCommandException    if the command provided is invalid.
   * @throws InvalidTimeZoneException if the time zone provided is invalid.
   */
  @Override
  Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    try {
      switch (commandScanner.next()) {
        case "event":
          parseCopySingleEvent(commandScanner);
          break;
        case "events":
          parseCopyMultipleEvents(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: copy (event|events) ...");
      }
    } catch (NoSuchElementException e) {
      System.out.println("printing no such element");
      throw new ParseCommandException(
          "Invalid command format: copy (event|events) (eventName on|on|between) "
              + "(sourceStartDateTime|sourceStartDateTime|<sourceStartDate> and <sourceEndDate>) "
              + "--target <targetCalendarName> to (targetStartDateTime|targetStartDate)"
      );
    }
    return this;
  }

  /**
   * Parse the copy command for a single event. It parses the `event name`, `on` keyword, and the
   * source calendar's `start dateTime` before calling the method to parse the target calendar
   * details.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @throws ParseCommandException    if the command provided is invalid.
   * @throws InvalidTimeZoneException if the time zone provided is invalid.
   */
  private void parseCopySingleEvent(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    // parse the <eventName>
    try {
      eventName = parseOptionalQuoted(commandScanner);
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
    parseTargetCalendarDetails(commandScanner);
  }

  /**
   * Parse the copy command for multiple events. It parses the `on` or `between` keyword and the
   * source calendar's `start date` and `end date` before calling the method to parse the target
   * calendar details.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @throws ParseCommandException    if the command provided is invalid.
   * @throws InvalidTimeZoneException if the time zone provided is invalid.
   */
  private void parseCopyMultipleEvents(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    try {
      switch (commandScanner.next()) {
        case "on":
          parseCopyEventsOnDate(commandScanner);
          break;
        case "between":
          parseCopyEventsBetweenDates(commandScanner);
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

  /**
   * Parse the copy command for multiple events on a date. It parses the source calendar's `start
   * date` before calling the method to parse the target calendar.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @throws ParseCommandException    if the command provided is invalid.
   * @throws InvalidTimeZoneException if the time zone provided is invalid.
   */
  private void parseCopyEventsOnDate(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException {
    // parse the source calendar's start date
    sourceStartDate = parseDate(commandScanner);

    // parse the target calendar details
    parseTargetCalendarDetails(commandScanner);
  }

  /**
   * Parse the copy command for multiple events between two dates. It parses the source calendar's
   * `start date`, `end date`, and the target calendar details.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @throws ParseCommandException    if the command provided is invalid.
   * @throws InvalidTimeZoneException if the time zone provided is invalid.
   */
  private void parseCopyEventsBetweenDates(Scanner commandScanner)
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
    parseTargetCalendarDetails(commandScanner);
  }

  /**
   * Parse the target calendar details. It parses the `--target` keyword, the target calendar name,
   * and the `to` keyword before parsing the target calendar's start date or start dateTime.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @throws ParseCommandException if the command provided is invalid.
   */
  private void parseTargetCalendarDetails(Scanner commandScanner)
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

  /**
   * Parse the optional quoted string from the command. If the string is enclosed in double quotes,
   * removes the quotes and returns the string else, returns the string as it is.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @return the token parsed from the command
   * @throws ParseCommandException if the command provided is invalid.
   */
  private String parseOptionalQuoted(Scanner commandScanner) throws ParseCommandException {
    String token = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (token == null) {
      throw new ParseCommandException("Invalid command format: ");
    }
    return token.startsWith("\"") ? token.substring(1, token.length() - 1) : token;
  }

  /**
   * Parse the date from the command. It parses the date in the format `yyyy-MM-dd` and returns the
   * date.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @return the date parsed from the command
   * @throws ParseCommandException if the command provided is invalid.
   */
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

  /**
   * Parse the dateTime from the command. It parses the dateTime in the format `yyyy-MM-dd HH:mm`
   * and returns the dateTime.
   *
   * @param commandScanner the Scanner object that reads the command (File or console input).
   * @return the dateTime parsed from the command
   * @throws ParseCommandException if the command provided is invalid.
   */
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

  /**
   * Execute the copy command on the model. It copies the event(s) from the source calendar to the
   * target calendar.
   *
   * <p>The recurring details of the recurring event(s) are reset and the event(s) are copied as
   * non-recurring events.
   *
   * <p>It will check for the conflicts in the target calendar and will not copy the event(s) if
   * there is a conflict. In case of series of events with conflicts, it will follow the best effort
   * approach to copy events that don't have conflict and skip those that do.
   *
   * @param controllerUtility the controller utility class
   * @throws CalendarExportException if the target calendar is not present in the model
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException {
    // check if target calendar is present
    CalendarEntry targetCalendarEntry = controllerUtility.getCalendarEntry(targetCalendarName);
    if (Objects.isNull(targetCalendarEntry)) {
      throw new CalendarExportException("Target calendar not found: " + targetCalendarName);
    }

    // get current calendar entry
    CalendarEntry currentCalendarEntry = controllerUtility.getCurrentCalendar();

    // copy events from current calendar to target calendar
    if (sourceStartDateTime != null) {
      copiedEvents = copySingleEvent(currentCalendarEntry, targetCalendarEntry);
    } else if (sourceStartDate != null && sourceEndDate != null) {
      // copy events between two dates
      copiedEvents = copyEventsBetweenDates(currentCalendarEntry, targetCalendarEntry);
    } else {
      // copy events on a date
      copiedEvents = copyEventsOnDate(currentCalendarEntry, targetCalendarEntry);
    }
  }

  /**
   * Copy a single event from the source calendar to the target calendar.
   *
   * @param sourceCalendarEntry the current calendar set in the controller
   * @param targetCalendarEntry the target calendar to copy the event to
   * @return the number of events copied (should be always 1 for this method)
   */
  private int copySingleEvent(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {
    // get event in source calendar for the given dateTime
    List<EventDTO> events = sourceCalendarEntry
        .model.getEventsInRange(sourceStartDateTime, sourceStartDateTime);

    // filter the event to copy
    events.removeIf(event -> !event.getSubject().equals(eventName));

    // no event to copy, return 0
    if (events.isEmpty()) {
      return 0;
    }

    return copyEvents(targetCalendarEntry, events);
  }

  /**
   * Copy all the events on a given date from the source calendar to the target calendar.
   *
   * @param sourceCalendarEntry the current calendar set in the controller
   * @param targetCalendarEntry the target calendar to copy the event to
   * @return the number of events copied (all the events on the given date)
   */
  private int copyEventsOnDate(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {
    // get all the events on the source date
    List<EventDTO> eventsOnDate = sourceCalendarEntry.model.getEventsOnDate(sourceStartDate);

    // no events to copy, return 0
    if (eventsOnDate.isEmpty()) {
      return 0;
    }

    return copyEvents(targetCalendarEntry, eventsOnDate);
  }

  /**
   * Copy all the events between two dates from the source calendar to the target calendar.
   *
   * @param sourceCalendarEntry the current calendar set in the controller
   * @param targetCalendarEntry the target calendar to copy the event to
   * @return the number of events copied (all the events between the two dates)
   */
  private int copyEventsBetweenDates(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {
    // get all the events between the two dates
    List<EventDTO> eventsBetweenDates = sourceCalendarEntry.model
        .getEventsInRange(
            sourceStartDate.atStartOfDay(),
            sourceStartDate.atTime(23, 59, 59)
        );

    // no events to copy, return 0
    if (eventsBetweenDates.isEmpty()) {
      return 0;
    }

    return copyEvents(targetCalendarEntry, eventsBetweenDates);
  }

  /**
   * Copy the events to the target calendar. It creates a new event in the target calendar with the
   * same details as the source event. Recurring details are reset.
   *
   * @param targetCalendarEntry the target calendar to copy the event to
   * @param eventsToCopy        the list of events to copy
   * @return the number of events copied to target calendar
   */
  private int copyEvents(CalendarEntry targetCalendarEntry, List<EventDTO> eventsToCopy) {
    int copiedEvents = 0;

    for (EventDTO event : eventsToCopy) {
      // get the startDateTime for event to be copied (time will be same as source)
      LocalDateTime newStartDateTime;
      if (!Objects.isNull(sourceStartDateTime)) {
        newStartDateTime = targetStartDateTime;
      } else {
        newStartDateTime = targetStartDate.atTime(event.getStartTime().toLocalTime());
      }

      // get the endDateTime for event to be copied (time will be same as source)
      LocalDateTime newEndDateTime = null;
      if (!event.getIsAllDay() && event.getEndTime() != null) {
        // calculate event duration between startTime and endTime
        Duration durationOfEvent = Duration.between(event.getStartTime(), event.getEndTime());
        newEndDateTime = newStartDateTime.plus(durationOfEvent);
      }

      // create a new event in target calendar
      EventDTO eventToCopy = EventDTO.getBuilder()
          .setSubject(event.getSubject())
          .setDescription(event.getDescription())
          .setLocation(event.getLocation())
          .setIsAllDay(event.getIsAllDay())
          .setIsPublic(event.getIsPublic())
          .setIsRecurring(false) // recurring details are reset on copy
          .setStartTime(newStartDateTime)
          .setEndTime(newEndDateTime)
          .build();

      // try to create the event in target calendar
      try {
        targetCalendarEntry.model.createEvent(eventToCopy, true);
        copiedEvents++;
      } catch (EventConflictException e) {
        // event conflict, add to conflict messages
        formatConflictMessages();
        conflictMessages.append(event.getSubject())
            .append(" on ")
            .append(newStartDateTime.format(CalendarController.dateTimeFormatter));
      }
    }
    return copiedEvents;
  }

  /**
   * Format the conflict messages to be printed.
   */
  private void formatConflictMessages() {
    if (conflictMessages.length() > 0) {
      conflictMessages.append("\n");
    }
    conflictMessages.append("- Event Conflict: ");
  }

  /**
   * Prompt the result of copy command with a message.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (copiedEvents > 0) {
      controllerUtility.promptOutput(
          "Successfully copied" + copiedEvents + " event(s) to " + targetCalendarName);
    } else {
      controllerUtility.promptOutput("No events were copied to " + targetCalendarName);
    }

    // print conflict messages if any
    if (conflictMessages.length() > 0) {
      controllerUtility.promptOutput(
          "The following events were not copied due to conflicts:\n" + conflictMessages);
    }
  }
}
