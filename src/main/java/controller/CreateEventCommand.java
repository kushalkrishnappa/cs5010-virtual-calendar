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
import java.util.NoSuchElementException;
import java.util.Objects;
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
    try {
      if (!commandScanner.next().equals("event")) {
        throw new ParseCommandException("Invalid command format: create event ...");
      }

      String next = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
      if (Objects.isNull(next)) {
        throw new ParseCommandException("Invalid command format: create event <eventName> ...");
      }
      if (next.equals("--autoDecline")) {
        autoDecline = true;
        eventName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
        if (Objects.isNull(eventName)) {
          throw new ParseCommandException(
              "Invalid command format: create event [--autoDecline] <eventName> ...");
        }
      } else {
        eventName = next;
      }
      if (eventName.startsWith("\"") && eventName.endsWith("\"")) {
        eventName = eventName.substring(1, eventName.length() - 1);
      }
      switch (commandScanner.next()) {
        case "from":
          handleCreateSpannedEvent(commandScanner);
          break;
        case "on":
          handleCreateAllDayEvent(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: create event [--autoDecline] <eventName> (from|on) ...]");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: create event [--autoDecline] <eventName> "
              + "(on <dateTime>| from <startDateTime> to <endDateTime>) "
              + "[repeats <weekdays> (for <N> times| until <untilDateTime>)]");
    }
  }

  private void handleCreateAllDayEvent(Scanner commandScanner) throws ParseCommandException {
    String startTime = commandScanner.next();

    if (parseRepeatDays(commandScanner)) {
      try {
        this.startTime = LocalDateTime.parse(startTime, CalendarController.dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid Start Date format: expecting " + CalendarController.dateTimeFormatter);
      }
      return;
    }

    try {
      this.startTime = LocalDate.parse(startTime, CalendarController.dateFormatter)
          .atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid date format: " + CalendarController.dateFormatter);
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateAllDayNTimesEvent(commandScanner);
        break;
      case "until":
        handleCreateAllDayUntilEvent(commandScanner);
        break;
      default:
        throw new ParseCommandException(
            "Invalid command format: create event <eventName> on <dateTime> "
                + "repeats <weekdays> (for|until) ...");
    }
  }

  private boolean parseRepeatDays(Scanner commandScanner) throws ParseCommandException {
    if (!commandScanner.hasNext()) {
      return true;
    }

    if (!commandScanner.next().equals("repeats")) {
      throw new ParseCommandException(
          "Invalid command format: create event <eventName> "
              + "(on <dateTime> |from <startDateTime> to <endDateTime>) repeats ...");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(commandScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Invalid week days specification: Expected combination of "
          + "MTWRFSU");
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
      throw new ParseCommandException(
          "Invalid command format: create event <eventName> on <dateString> "
              + "repeats <weekdays> for <occurrences> times");
    }
    isRecurring = true;
  }

  private void handleCreateSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateTimeFormatter);
    }
    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException(
          "Invalid command format: create event [--autoDecline] <eventName> from "
              + "<startDateTime> to ...");
    }
    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateTimeFormatter);
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
      default:
        throw new ParseCommandException(
            "Invalid command format: create event [--autoDecline] <eventName> from "
                + "<startDateTime> to <dateDateTime> ...");
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
      throw new ParseCommandException(
          "Invalid command format: create event [--autoDecline] <eventName> from "
              + "<startDateTime> to <dateDateTime> repeats <weekdays> for <N> times");
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
                isRecurring ?
                    RecurringDetailsDTO.getBuilder()
                        .setRepeatDays(repeatDays)
                        .setUntilDate(untilDate)
                        .setOccurrences(occurrences)
                        .build()
                    : null
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
