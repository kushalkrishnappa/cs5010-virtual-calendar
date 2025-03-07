package controller;

import dto.EventDTO;
import exception.ParseCommandException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PrintEventsCommand extends Command {

  private LocalDate onDate;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private List<EventDTO> eventsOnDate;

  PrintEventsCommand(CalendarController calendarController, Scanner commandScanner) {
    super(calendarController, commandScanner);
  }

  @Override
  void parseCommand() throws ParseCommandException {
    if (!commandScanner.next().equals("events")) {
      throw new ParseCommandException("Invalid command format: print events...");
    }

    switch (commandScanner.next()) {
      case "on":
        printEventsOnDate();
        break;
      case "from":
        printEventsInInterval();
        break;
      default:
        throw new ParseCommandException("Invalid command format: print events (on|from)...");
    }
  }

  private void printEventsOnDate() throws ParseCommandException {
    try {
      onDate = LocalDate.parse(commandScanner.next(), calendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid onDate format: " + calendarController.dateFormatter);
    }

    command = () -> eventsOnDate = calendarController.getModel().getEventsOnDate(onDate);
  }

  private void printEventsInInterval() throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + calendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException("Invalid command format: print events from "
          + "<dateStringTtimeString> to...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + calendarController.dateFormatter);
    }

    command = () -> eventsOnDate = calendarController.getModel()
        .getEventsInRange(startTime, endTime);
  }

  @Override
  void promptResult() {
    for (EventDTO event : eventsOnDate) {
      if (Objects.isNull(event.getEndTime())) {
        calendarController.promptOutput(event.getSubject() + " [All day]\n");
        continue;
      }
      calendarController.promptOutput(
          event.getSubject() + " [" + event.getStartTime() + " - " + event.getEndTime() + "]\n"
      );
    }
  }

}
