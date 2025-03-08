package controller;


import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;
import model.DayOfWeek;
import model.IModel;

class CreateEventCommand extends Command {

  private String eventName;
  private boolean autoDecline;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private boolean isRecurring;
  private Set<DayOfWeek> repeatDays;
  private LocalDateTime untilDate;
  private int occurrences;

  @Override
  void parseCommand(Scanner commandScanner) throws ParseCommandException {
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
        handleCreateSpannedEvent(commandScanner);
        break;
      case "on":
        handleCreateAllDayEvent(commandScanner);
        break;
      default:
        throw new ParseCommandException("Invalid format");
    }
  }

  private void handleCreateAllDayEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDate.parse(commandScanner.next(), CalendarController.dateFormatter)
          .atStartOfDay();
    } catch (DateTimeParseException e) {
      System.out.println(e);
      throw new ParseCommandException("Invalid date format: " + CalendarController.dateFormatter);
    }

    if (parseRepeatDays(commandScanner)) {
      return;
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateAllDayNTimesEvent(commandScanner);
        break;
      case "until":
        handleCreateAllDayUntilEvent(commandScanner);
        break;
    }
  }

  private boolean parseRepeatDays(Scanner commandScanner) throws ParseCommandException {
    if (!commandScanner.hasNext()) {
      return true;
    }

    if (!commandScanner.next().equals("repeats")) {
      throw new ParseCommandException("Invalid format (not repeats)");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(commandScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Repeat days is not specified correctly");
    }
    return false;
  }

  private void handleCreateAllDayUntilEvent(Scanner commandScanner)
      throws ParseCommandException {
    try {
      untilDate = LocalDateTime.parse(commandScanner.next(), CalendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid untilTime format: " + CalendarController.dateFormatter);
    }
    isRecurring = true;
  }

  private void handleCreateAllDayNTimesEvent(Scanner commandScanner)
      throws ParseCommandException {
    occurrences = Integer.parseInt(commandScanner.next());
    if (!commandScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }
    isRecurring = true;
  }

  private void handleCreateSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
      if (!commandScanner.next().equals("to")) {
        throw new ParseCommandException("Invalid format");
      }
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid dateTime format" + CalendarController.dateTimeFormatter);
    }

    if (parseRepeatDays(commandScanner)) {
      return;
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateSpannedNTimesEvent(commandScanner);
        break;
      case "until":
        handleCreateSpannedUntilEvent(commandScanner);
        break;
    }
  }

  private void handleCreateSpannedUntilEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      untilDate = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid untilTime format: " + CalendarController.dateTimeFormatter);
    }
    isRecurring = true;
  }

  private void handleCreateSpannedNTimesEvent(Scanner commandScanner) throws ParseCommandException {
    occurrences = Integer.parseInt(commandScanner.next());
    if (!commandScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }
    isRecurring = true;
  }

  @Override
  void executeCommand(IModel model) throws CalendarExportException, EventConflictException {
    model.createEvent(
        EventDTO.getBuilder()
            .setSubject(eventName)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setIsRecurring(isRecurring)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setRepeatDays(repeatDays)
                    .setUntilDate(untilDate)
                    .setOccurrences(occurrences)
                    .build()
            )
            .build(),
        autoDecline
    );
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Successfully created event " + eventName + "\n");
  }
}
