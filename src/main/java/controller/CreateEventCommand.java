package controller;


import dto.EventDTO;
import dto.RecurringEventDTO;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;
import model.DayOfWeek;

class CreateEventCommand extends Command {


  private String eventName;
  private boolean autoDecline;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Set<DayOfWeek> repeatDays;
  private LocalDateTime untilDate;
  private int occurrences;

  private final EventDTO.Builder<?> eventBuilder;
  private final RecurringEventDTO.Builder<?> reccuringEventBuilder;

  CreateEventCommand(CalendarController calendarController, Scanner commandScanner) {
    super(calendarController, commandScanner);
    reccuringEventBuilder = new RecurringEventDTO.Builder();
    eventBuilder = new EventDTO.Builder();
  }

  @Override
  void parseCommand() throws ParseCommandException {
    if (!commandScanner.next().equals("event")) {
      throw new ParseCommandException("Invalid command (not create event)");
    }

    String next = commandScanner.next();
    if (next.equals("--autoDecline")) {
      autoDecline = true;
      eventName = commandScanner.next();
    } else {
      eventName = next;
    }
    switch (commandScanner.next()) {
      case "from":
        handleCreateSpannedEvent();
        break;
      case "on":
        handleCreateAllDayEvent();
        break;
      default:
        throw new ParseCommandException("Invalid format");
    }
  }

  private void handleCreateAllDayEvent() throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid date format");
    }

    if (!commandScanner.hasNext()) {
      command = () -> calendarController.getModel().createEvent(eventBuilder
          .setSubject(eventName)
          .setStartTime(startTime)
          .build(), autoDecline);
      return;
    }

    if (!commandScanner.next().equals("repeats")) {
      throw new ParseCommandException("Invalid format (not repeats)");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(commandScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Repeat days is not specified correctly");
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateAllDayNTimesEvent();
        break;
      case "until":
        handleCreateAllDayUntilEvent();
        break;
    }
  }

  private void handleCreateAllDayUntilEvent()
      throws ParseCommandException {
    try {
      untilDate = LocalDateTime.parse(commandScanner.next(), calendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid untilTime format");
    }

    command = () -> calendarController.getModel().createRecurringEvent(reccuringEventBuilder
        .setSubject(eventName)
        .setStartTime(startTime)
        .setRepeatDays(repeatDays)
        .setUntilDate(untilDate)
        .build(), autoDecline);
  }

  private void handleCreateAllDayNTimesEvent()
      throws ParseCommandException {
    occurrences = Integer.parseInt(commandScanner.next());
    if (!commandScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }

    command = () -> calendarController.getModel().createRecurringEvent(reccuringEventBuilder
        .setSubject(eventName)
        .setStartTime(startTime)
        .setRepeatDays(repeatDays)
        .setOccurrences(occurrences)
        .build(), autoDecline);
  }

  private void handleCreateSpannedEvent() throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
      if (!commandScanner.next().equals("to")) {
        throw new ParseCommandException("Invalid format");
      }
      endTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid time format");
    }

    if (!commandScanner.hasNext()) {
      command = () -> calendarController.getModel().createEvent(eventBuilder
          .setSubject(eventName)
          .setStartTime(startTime)
          .setEndTime(endTime)
          .build(), autoDecline);
      return;
    }

    if (!commandScanner.next().equals("repeats")) {
      throw new ParseCommandException("Invalid format (not repeats)");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(commandScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Repeat days is not specified correctly");
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateSpannedNTimesEvent();
        break;
      case "until":
        handleCreateSpannedUntilEvent();
        break;
    }
  }

  private void handleCreateSpannedUntilEvent() throws ParseCommandException {
    try {
      untilDate = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid untilTime format");
    }

    command = () -> calendarController.getModel().createRecurringEvent(reccuringEventBuilder
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setRepeatDays(repeatDays)
        .setUntilDate(untilDate)
        .build(), autoDecline);
  }

  private void handleCreateSpannedNTimesEvent() throws ParseCommandException {
    occurrences = Integer.parseInt(commandScanner.next());
    if (!commandScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }

    command = () -> calendarController.getModel().createRecurringEvent(reccuringEventBuilder
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .setRepeatDays(repeatDays)
        .setOccurrences(occurrences)
        .build(), autoDecline);
  }

  @Override
  void promptResult() {
    calendarController.promptOutput("Successfully created event " + eventName + "\n");
  }
}
