package controller;

import dto.EventDTO.Builder;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

class EditEventCommand extends Command {

  private String eventName;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private final Map<String, BiConsumer<Builder<?>, String>> propertySetters;
  BiConsumer<Builder<?>, String> propertySetter;

  private Integer updatedEvents;


  EditEventCommand(CalendarController calendarController, Scanner commandScanner) {
    super(calendarController, commandScanner);
    propertySetters = createPropertySetters();
  }

  private Map<String, BiConsumer<Builder<?>, String>> createPropertySetters() {
    Map<String, BiConsumer<Builder<?>, String>> setters = new HashMap<>();
    setters.put("name", (builder, value) -> builder.setSubject(value));
    setters.put("startTime",
        (builder, value) -> builder.setStartTime(
            LocalDateTime.parse(value, calendarController.dateTimeFormatter)));
    setters.put("endTime",
        (builder, value) -> builder.setEndTime(
            LocalDateTime.parse(value, calendarController.dateTimeFormatter)));
    return setters;
  }

  @Override
  void parseCommand() throws ParseCommandException {
    switch (commandScanner.next()) {
      case "event":
        editSingleEvent();
        break;
      case "events":
        editMultipleEvents();
        break;
      default:
        throw new ParseCommandException("Invalid command format: edit (event|events)...");
    }

  }

  private void editSingleEvent() throws ParseCommandException {
    propertySetter = propertySetters.get(commandScanner.next());

    eventName = commandScanner.next();

    if (!commandScanner.next().equals("from")) {
      throw new ParseCommandException(
          "Invalid command format: edit event <property> <eventName> from...");
    }

    try {
      startTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + calendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + calendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("with")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to <dateStringTtimeString> with...");
    }

    Builder<?> eventBuilder = new Builder();
    try {
      propertySetter.accept(eventBuilder, commandScanner.next());
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }

    command = () -> updatedEvents = calendarController.getModel()
        .editEvent(eventName, startTime, endTime, eventBuilder
            .setStartTime(startTime)
            .setEndTime(endTime)
            .build());
  }

  private void editMultipleEvents() throws ParseCommandException {
    propertySetter = propertySetters.get(commandScanner.next());

    eventName = commandScanner.next();

    String next = commandScanner.next();

    if (next.equals("from")) {
      try {
        startTime = LocalDateTime.parse(commandScanner.next(),
            calendarController.dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid startDateTime format: " + calendarController.dateFormatter);
      }

      if (!commandScanner.next().equals("with")) {
        throw new ParseCommandException(
            "Invalid command format: edit events <property> <eventName> "
                + "from <dateStringTtimeString> with...");
      }

      next = commandScanner.next();
    }

    Builder<?> eventBuilder = new Builder();
    try {
      propertySetter.accept(eventBuilder, next);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }

    command = () -> updatedEvents = calendarController.getModel()
        .editEvent(eventName, startTime, endTime, eventBuilder
            .setStartTime(startTime)
            .build());
  }

  @Override
  void promptResult() {
    calendarController.promptOutput("Edited " + updatedEvents + " event(s)\n");
  }
}
