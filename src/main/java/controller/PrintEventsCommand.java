package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * PrintEventsCommand class implements Command and execute the command to print events on a specific
 * date or in a specific interval.
 */
class PrintEventsCommand extends Command {

  private LocalDate onDate;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private List<EventDTO> eventsOnDate;

  /**
   * Constructor for PrintEventsCommand.
   */
  PrintEventsCommand() {
    onDate = null;
    startTime = null;
    endTime = null;
    eventsOnDate = null;
  }

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    try {
      if (!commandScanner.next().equals("events")) {
        throw new ParseCommandException("Invalid command format: print events ...");
      }
      switch (commandScanner.next()) {
        case "on":
          printEventsOnDate(commandScanner);
          break;
        case "from":
          printEventsInInterval(commandScanner);
          break;
        default:
          throw new ParseCommandException("Invalid command format: print events (on|from) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: print events "
              + "(on <dateTime>|from <startDateTime> to <endDateTime>)");
    }
    return this;
  }

  /**
   * Parse the command to print events on a specific date.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void printEventsOnDate(Scanner commandScanner) throws ParseCommandException {
    try {
      onDate = LocalDate.parse(commandScanner.next(), CalendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid onDate format: " + CalendarController.dateFormat);
    }

  }

  /**
   * Parse the command to print events in a specific interval.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
  private void printEventsInInterval(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateTimeFormat);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException(
          "Invalid command format: print events from <dateStringTtimeString> to ...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateTimeFormat);
    }

  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    if (!Objects.isNull(onDate)) {
      eventsOnDate = controllerUtility.getCurrentCalendar().model
          .getEventsOnDate(
              onDate.atStartOfDay()
                  .toLocalDate());
    } else {
      eventsOnDate = controllerUtility.getCurrentCalendar().model
          .getEventsInRange(startTime, endTime);
    }
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    // [startDate] [startTime - EndTime] [NotRecurring] name location
    // [startDate] [ ALL DAY EVENT] [Recurring]  name location
    StringBuilder eventOutput = new StringBuilder();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    for (EventDTO event : eventsOnDate) {
      eventOutput.setLength(0);
      eventOutput.append('[')
          .append(event.getStartTime().format(dateFormatter))
          .append("] ");
      if (event.getIsAllDay()) {
        eventOutput.append(String.format("%-38s", "[ALL DAY EVENT]"));
      } else {
        eventOutput.append('[')
            .append(event.getStartTime().format(dateTimeFormatter))
            .append(" - ")
            .append(event.getEndTime().format(dateTimeFormatter))
            .append("] ");
      }
      eventOutput.append(event.getIsRecurring() ? "[Recurring]     " : "[Not Recurring] ")
          .append(event.getSubject())
          .append(" || ")
          .append(Objects.nonNull(event.getLocation()) ? event.getLocation() : "");
      controllerUtility.promptOutput(eventOutput.toString());
    }
  }
}
