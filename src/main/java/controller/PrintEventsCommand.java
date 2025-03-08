package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import model.IModel;

public class PrintEventsCommand extends Command {

  private LocalDate onDate;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private List<EventDTO> eventsOnDate;

  @Override
  void parseCommand(Scanner commandScanner) throws ParseCommandException {
    if (!commandScanner.next().equals("events")) {
      throw new ParseCommandException("Invalid command format: print events...");
    }

    switch (commandScanner.next()) {
      case "on":
        printEventsOnDate(commandScanner);
        break;
      case "from":
        printEventsInInterval(commandScanner);
        break;
      default:
        throw new ParseCommandException("Invalid command format: print events (on|from)...");
    }
  }

  private void printEventsOnDate(Scanner commandScanner) throws ParseCommandException {
    try {
      onDate = LocalDate.parse(commandScanner.next(), CalendarController.dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid onDate format: " + CalendarController.dateFormatter);
    }

  }

  private void printEventsInInterval(Scanner commandScanner) throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException(
          "Invalid command format: print events from " + "<dateStringTtimeString> to...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateFormatter);
    }

  }

  @Override
  void executeCommand(IModel model) throws CalendarExportException, EventConflictException {
    if (!Objects.isNull(onDate)) {
      eventsOnDate = model.getEventsOnDate(onDate);
    } else {
      eventsOnDate = model.getEventsInRange(startTime, endTime);
    }
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    for (EventDTO event : eventsOnDate) {
      if (Objects.isNull(event.getEndTime())) {
        controllerUtility.promptOutput(event.getSubject() + " [All day]\n");
        continue;
      }
      controllerUtility.promptOutput(
          event.getSubject() + " [" + event.getStartTime() + " - " + event.getEndTime() + "]\n");
    }
  }
}
