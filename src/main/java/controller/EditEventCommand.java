package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import dto.EventDTO.EventDTOBuilder;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import model.IModel;

class EditEventCommand extends Command {

  private String eventName;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private final Map<String, BiConsumer<EventDTOBuilder, String>> propertySetters;
  BiConsumer<EventDTOBuilder, String> propertySetter;
  private final EventDTOBuilder eventBuilder;

  private Integer updatedEvents;

  EditEventCommand() {
    propertySetters = createPropertySetters();
    eventBuilder = EventDTO.getBuilder();
  }

  private Map<String, BiConsumer<EventDTOBuilder, String>> createPropertySetters() {
    Map<String, BiConsumer<EventDTOBuilder, String>> setters = new HashMap<>();
    setters.put("name",
        (builder, value) -> builder.setSubject(value));
    setters.put("startTime",
        (builder, value) -> builder.setStartTime(
            LocalDateTime.parse(value, CalendarController.dateTimeFormatter)));
    setters.put("endTime",
        (builder, value) -> builder.setEndTime(
            LocalDateTime.parse(value, CalendarController.dateTimeFormatter)));
    setters.put("description",
        (builder, value) -> builder.setDescription(value));
    setters.put("location",
        (builder, value) -> builder.setLocation(value));
    setters.put("isPublic",
        (builder, value) -> builder.setIsPublic(
            Boolean.parseBoolean(value)));
    return setters;
  }

  @Override
  void parseCommand(Scanner commandScanner) throws ParseCommandException {
    switch (commandScanner.next()) {
      case "event":
        editSpannedEvent(commandScanner);
        break;
      case "events":
        editRecurringEvents(commandScanner);
        break;
      default:
        throw new ParseCommandException("Invalid command format: edit (event|events)...");
    }
  }

  private void editSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    propertySetter = propertySetters.get(commandScanner.next());

    eventName = commandScanner.next();

    if (!commandScanner.next().equals("from")) {
      throw new ParseCommandException(
          "Invalid command format: edit event <property> <eventName> from...");
    }

    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("with")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to <dateStringTtimeString> with...");
    }

    try {
      propertySetter.accept(eventBuilder, commandScanner.next());
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }
  }

  private void editRecurringEvents(Scanner commandScanner) throws ParseCommandException {
    eventBuilder.setIsRecurring(true);
    propertySetter = propertySetters.get(commandScanner.next());

    eventName = commandScanner.next();

    String next = commandScanner.next();

    if (next.equals("from")) {
      try {
        startTime = LocalDateTime.parse(commandScanner.next(),
            CalendarController.dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid startDateTime format: " + CalendarController.dateFormatter);
      }

      if (!commandScanner.next().equals("with")) {
        throw new ParseCommandException(
            "Invalid command format: edit events <property> <eventName> "
                + "from <dateStringTtimeString> with...");
      }

      next = commandScanner.next();
    }

    try {
      propertySetter.accept(eventBuilder, next);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }
  }

  @Override
  void executeCommand(IModel model) throws CalendarExportException, EventConflictException {
    updatedEvents = model.editEvent(eventName, startTime, endTime, eventBuilder.build());
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Edited " + updatedEvents + " event(s)\n");
  }
}
