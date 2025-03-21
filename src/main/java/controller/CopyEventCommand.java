package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import exception.CalendarExportException;
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

public class CopyEventCommand extends Command {

  private LocalDateTime sourceStartDateTime;

  private LocalDate sourceStartDate;

  private LocalDate sourceEndDate;

  private LocalDate targetStartDate;

  private LocalDateTime targetStartDateTime;

  private String targetCalendarName;

  private String eventName;

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

  /**
   * Execute the copy command on the model. It copies the event(s) from the source calendar to the
   * target calendar.
   *
   * <p>The recurring details of the recurring event(s) are reset and the event(s) are copied as
   * non-recurring events.
   *
   * <p>It does not check for conflicts in the target calendar. It will create the event(s) in the
   * target calendar even if there is a conflict.
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

  private int copySingleEvent(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {
    // get event in source calendar for the given dateTime
    List<EventDTO> events = sourceCalendarEntry
        .model.getEventsInRange(sourceStartDateTime, sourceStartDateTime);

    // filter the event to copy
    EventDTO eventToCopy = null;
    for (EventDTO event : events) {
      if (event.getSubject().equals(eventName)) {
        eventToCopy = event;
        break;
      }
    }

    // if no event found, return 0
    if (Objects.isNull(eventToCopy)) {
      return 0;
    }

    // calculate event duration i.e. endTime - startTime in minutes
    Duration durationOfEvent = null;
    if (!eventToCopy.getIsAllDay() && eventToCopy.getEndTime() != null) {
      durationOfEvent = Duration.between(eventToCopy.getStartTime(), eventToCopy.getEndTime());
    }

    // Create a new event in target calendar
    EventDTO.EventDTOBuilder builder = EventDTO.getBuilder()
        .setSubject(eventToCopy.getSubject())
        .setDescription(eventToCopy.getDescription())
        .setLocation(eventToCopy.getLocation())
        .setIsAllDay(eventToCopy.getIsAllDay())
        .setIsPublic(eventToCopy.getIsPublic())
        .setIsRecurring(false)
        .setStartTime(targetStartDateTime);

    // Set end time if applicable
    if (!eventToCopy.getIsAllDay() && durationOfEvent != null) {
      builder.setEndTime(targetStartDateTime.plus(durationOfEvent));
    }

    // Create the event in target calendar
    targetCalendarEntry.model.createEvent(builder.build(), false);
    return 1;
  }

  private int copyEventsOnDate(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {

    // get all the events on the source date
    List<EventDTO> eventsOnDate = sourceCalendarEntry.model.getEventsOnDate(sourceStartDate);

    if (eventsOnDate.isEmpty()) {
      return 0;
    }

    int copiedEvents = 0;

    for (EventDTO event : eventsOnDate) {
      // calculate event duration between startTime and endTime
      Duration durationOfEvent = Duration.between(event.getStartTime(), event.getEndTime());

      // get the startDateTime for event to be copied (time will be same as source)
      LocalDateTime newStartDateTime = targetStartDate.atTime(event.getStartTime().toLocalTime());

      // get the endDateTime for event to be copied (time will be same as source)
      LocalDateTime newEndDateTime = null;
      if (!event.getIsAllDay() && event.getEndTime() != null) {
        newEndDateTime = newStartDateTime.plus(durationOfEvent);
      }

      // create a new event in target calendar
      EventDTO eventToCopy = EventDTO.getBuilder()
          .setSubject(event.getSubject())
          .setDescription(event.getDescription())
          .setLocation(event.getLocation())
          .setIsAllDay(event.getIsAllDay())
          .setIsPublic(event.getIsPublic())
          .setIsRecurring(false)
          .setStartTime(newStartDateTime)
          .setEndTime(newEndDateTime)
          .build();

      // create the event in target calendar
      targetCalendarEntry.model.createEvent(eventToCopy, false);
      copiedEvents++;
    }
    return copiedEvents;
  }

  private int copyEventsBetweenDates(CalendarEntry sourceCalendarEntry,
      CalendarEntry targetCalendarEntry) {
    //TODO: Implement logic to copy events between two dates from sourceCalendarEntry to targetCalendarEntry
    return 0;
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
