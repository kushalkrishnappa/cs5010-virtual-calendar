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
 * This class represents an implementation of the abstract Command class to print events on a
 * specific date or in a specific interval. It parses the command, executes the print operation, and
 * prompts the result.
 */
class PrintEventsCommand extends Command {

  private LocalDate onDate;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private List<EventDTO> eventsOnDate;

  /**
   * This constructor for PrintEventsCommand initializes the onDate, startTime, endTime, and
   * eventsOnDate.
   */
  PrintEventsCommand() {
    onDate = null;
    startTime = null;
    endTime = null;
    eventsOnDate = null;
  }

  /**
   * This method starts parsing the print events command from the Scanner object. It checks if the
   * command contains `events` keyword and the `on` or `from` keyword before calling the appropriate
   * method to continue the parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * This method makes call to model to get the events on a specific date or in a specific interval.
   * It uses the controller utility to get the events. If the onDate is not null, it gets the events
   * on that date, otherwise it gets the events in the specified interval.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if the calendar export fails
   * @throws EventConflictException  if there is an event conflict
   */
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

  /**
   * Prompt the result of the print events command. It formats the output to show the events in a
   * specific date or in a specific interval.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
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
