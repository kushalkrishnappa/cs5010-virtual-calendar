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
import model.CalendarDayOfWeek;

/**
 * CreateEventCommand class implements Command and execute the command to create an event.
 */
class CreateEventCommand extends Command {

  private String eventName;

  private Boolean autoDecline;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private Boolean isRecurring;

  private Boolean isAllDay;

  private Set<CalendarDayOfWeek> repeatDays;

  private LocalDateTime untilDate;

  private Integer occurrences;

  /**
   * Constructor for CreateEventCommand.
   */
  CreateEventCommand() {
    eventName = null;
    autoDecline = false;
    startTime = null;
    endTime = null;
    isRecurring = false;
    isAllDay = false;
    repeatDays = null;
    untilDate = null;
    occurrences = null;
  }

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    try {
      switch (commandScanner.next()) {
        case "calendar":
          Command command;
          command = new CreateCalendarCommand();
          return command.parseCommand(commandScanner);
        case "event":
          break;
        default:
          throw new ParseCommandException("Invalid command: create (calendar|event) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: create (calendar|event) ...");
    }

    try {

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
              "Invalid command format: create event [--autoDecline] <eventName> (from|on) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: create event [--autoDecline] <eventName> "
              + "(on <dateTime>| from <startDateTime> to <endDateTime>) "
              + "[repeats <weekdays> (for <N> times| until <untilDateTime>)]");
    }
    return this;
  }

  /**
   * Parse the command to create an all day event.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void handleCreateAllDayEvent(Scanner commandScanner) throws ParseCommandException {
    isAllDay = true;
    String startTime = commandScanner.next();

    if (notParseRepeatDays(commandScanner)) {
      try {
        this.startTime = LocalDateTime.parse(startTime, CalendarController.dateTimeFormatter)
            .toLocalDate().atStartOfDay();
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid Start Date format: expecting " + CalendarController.dateTimeFormat);
      }
      return;
    }

    try {
      this.startTime = LocalDate.parse(startTime, CalendarController.dateFormatter)
          .atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid date format: " + CalendarController.dateFormat);
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateNTimesEvent(commandScanner);
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

  /**
   * Parse the command to create a recurring event.
   *
   * @param commandScanner Scanner object to parse the command
   * @return true if the command is not in the correct format
   * @throws ParseCommandException If the command is not in the correct format
   */
  private boolean notParseRepeatDays(Scanner commandScanner) throws ParseCommandException {
    if (!commandScanner.hasNext()) {
      return true;
    }

    if (!commandScanner.next().equals("repeats")) {
      throw new ParseCommandException(
          "Invalid command format: create event <eventName> "
              + "(on <dateTime> |from <startDateTime> to <endDateTime>) repeats ...");
    }

    try {
      repeatDays = CalendarDayOfWeek.parseRepeatDays(commandScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Invalid week days specification: Expected combination of "
          + "MTWRFSU");
    }
    return false;
  }

  /**
   * Parse the command to create an all day event that repeats until a specific date.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void handleCreateAllDayUntilEvent(Scanner commandScanner)
      throws ParseCommandException {
    try {
      untilDate = LocalDate.parse(commandScanner.next(), CalendarController.dateFormatter)
          .atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid untilTime format: " + CalendarController.dateFormat);
    }
    isRecurring = true;
  }

  /**
   * Parse the command to create an all day event that repeats N times.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void handleCreateNTimesEvent(Scanner commandScanner)
      throws ParseCommandException {
    try {
      occurrences = Integer.parseInt(commandScanner.next());
    } catch (NumberFormatException e) {
      throw new ParseCommandException("Invalid occurrences format: Expected integer");
    }
    if (occurrences <= 0) {
      throw new ParseCommandException("Occurrences must be positive");
    }
    if (!commandScanner.next().equals("times")) {
      if (isAllDay) {
        throw new ParseCommandException(
            "Invalid command format: create event <eventName> on <dateString> "
                + "repeats <weekdays> for <occurrences> times");
      } else {
        throw new ParseCommandException(
            "Invalid command format: create event [--autoDecline] <eventName> from "
                + "<startDateTime> to <dateDateTime> repeats <weekdays> for <N> times");
      }
    }
    isRecurring = true;
  }

  /**
   * Parse the command to create an event that spans multiple days.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void handleCreateSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateTimeFormat);
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
          "Invalid endDateTime format: " + CalendarController.dateTimeFormat);
    }

    if (notParseRepeatDays(commandScanner)) {
      return;
    }

    switch (commandScanner.next()) {
      case "for":
        handleCreateNTimesEvent(commandScanner);
        break;
      case "until":
        handleCreateSpannedUntilEvent(commandScanner);
        break;
      default:
        throw new ParseCommandException(
            "Invalid command format: create event [--autoDecline] <eventName> from "
                + "<startDateTime> to <dateDateTime> repeats <weekdays> (for|until) ... ");
    }
  }

  /**
   * Parse the command to create an event that spans multiple days and repeats until a specific
   * date.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void handleCreateSpannedUntilEvent(Scanner commandScanner) throws ParseCommandException {
    try {
      untilDate = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid untilTime format: " + CalendarController.dateTimeFormat);
    }
    isRecurring = true;
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    // default value of autoDecline is set to true. This will always decline the event if the
    // event is conflicted with another event.
    autoDecline = true;
    controllerUtility.getCurrentCalendar().model.createEvent(
        EventDTO.getBuilder()
            .setSubject(eventName)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setIsAllDay(isAllDay)
            .setIsRecurring(isRecurring)
            .setRecurringDetails(
                isRecurring
                    ? RecurringDetailsDTO.getBuilder()
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
    controllerUtility.promptOutput("Successfully created event " + eventName);
  }
}
