package controller;

import controller.CalendarController.ControllerUtility;
import dto.EventDTO;
import dto.EventDTO.EventDTOBuilder;
import dto.RecurringDetailsDTO;
import dto.RecurringDetailsDTO.RecurringDetailsDTOBuilder;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;
import model.CalendarDayOfWeek;
import model.IModel;

class EditEventCommand extends Command {

  private String eventName;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private final Map<String, BiConsumer<EventDTOBuilder, String>> eventDTOPropertySetters;
  BiConsumer<EventDTOBuilder, String> eventDTOPropertySetter;
  private final Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>>
      recurringDetailsDTOPropertySetters;
  BiConsumer<RecurringDetailsDTOBuilder, String>
      recurringDetailsDTOPropertySetter;
  private final EventDTOBuilder eventBuilder;
  private final RecurringDetailsDTOBuilder recurringDetailsDTOBuilder;

  private Integer updatedEvents;

  EditEventCommand() {
    eventDTOPropertySetters = createPropertySetters();
    eventBuilder = EventDTO.getBuilder();
    recurringDetailsDTOPropertySetters = createRecurringDetailsPropertySetters();
    recurringDetailsDTOBuilder = RecurringDetailsDTO.getBuilder();
  }

  private Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>>
  createRecurringDetailsPropertySetters() {
    Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>> setters = new HashMap<>();
    setters.put("occurrences",
        (builder, value) -> builder.setOccurrences(
            Integer.parseInt(value)
        ));
    setters.put("repeatDays",
        (builder, value) -> builder.setRepeatDays(
            CalendarDayOfWeek.parseRepeatDays(value)
        ));
    setters.put("untilDateTime",
        (builder, value) -> builder.setUntilDate(
            LocalDateTime.parse(value, CalendarController.dateTimeFormatter)
        ));
    return setters;
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
    try {
      switch (commandScanner.next()) {
        case "event":
          editSpannedEvent(commandScanner);
          break;
        case "events":
          editRecurringEvents(commandScanner);
          break;
        default:
          throw new ParseCommandException("Invalid command format: edit (event|events) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: edit (event|events) <property> <eventName> "
              + "[from <startDateTime> [to <endDateTime>] with] "
              + "<newPropertyValue>");
    }
  }

  private void editSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    eventDTOPropertySetter = eventDTOPropertySetters.get(commandScanner.next());
    recurringDetailsDTOPropertySetter = recurringDetailsDTOPropertySetters.get(
        commandScanner.next());

    if (Objects.isNull(eventDTOPropertySetter)
        && Objects.isNull(recurringDetailsDTOPropertySetter)) {
      throw new ParseCommandException("Invalid property name");
    }
    if (!Objects.isNull(recurringDetailsDTOPropertySetter)) {
      eventBuilder.setIsRecurring(true);
    }

    eventName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (eventName.startsWith("\"") && eventName.endsWith("\"")) {
      eventName = eventName.substring(1, eventName.length() - 1);
    }

    if (!commandScanner.next().equals("from")) {
      throw new ParseCommandException(
          "Invalid command format: edit event <property> <eventName> from ...");
    }

    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to ...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateFormatter);
    }

    if (!commandScanner.next().equals("with")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to <dateStringTtimeString> with ...");
    }

    String next = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    parseNewPropertyValue(next);
  }

  private void editRecurringEvents(Scanner commandScanner) throws ParseCommandException {
    eventBuilder.setIsRecurring(true);
    eventDTOPropertySetter = eventDTOPropertySetters.get(commandScanner.next());
    recurringDetailsDTOPropertySetter = recurringDetailsDTOPropertySetters.get(
        commandScanner.next());

    if (Objects.isNull(eventDTOPropertySetter)
        && Objects.isNull(recurringDetailsDTOPropertySetter)) {
      throw new ParseCommandException("Invalid property name");
    }
    eventName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (eventName.startsWith("\"") && eventName.endsWith("\"")) {
      eventName = eventName.substring(1, eventName.length() - 1);
    }

    String next = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);

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
                + "from <dateStringTtimeString> with ...");
      }

      next = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    }

    parseNewPropertyValue(next);
  }

  private void parseNewPropertyValue(String next) throws ParseCommandException {
    if (next.startsWith("\"") && next.endsWith("\"")) {
      next = next.substring(1, next.length() - 1);
    }
    try {
      eventDTOPropertySetter.accept(eventBuilder, next);
      recurringDetailsDTOPropertySetter.accept(recurringDetailsDTOBuilder, next);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }
  }

  @Override
  void executeCommand(IModel model) throws CalendarExportException, EventConflictException {
    updatedEvents = model.editEvent(eventName, startTime, endTime, eventBuilder
        .setRecurringDetails(
            Objects.nonNull(recurringDetailsDTOPropertySetter) ?
                recurringDetailsDTOBuilder.build()
                : null)
        .setIsRecurring(Objects.nonNull(recurringDetailsDTOPropertySetter))
        .build());
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (updatedEvents > 0) {
      controllerUtility.promptOutput("Successfully updated event(s)\n");
    }
  }
}
