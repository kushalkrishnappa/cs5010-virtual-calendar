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

/**
 * EditEventCommand class implements Command and execute the command to edit an event or a series of
 * events.
 */
class EditEventCommand extends Command {

  private String eventName;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private final Map<String, BiConsumer<EventDTOBuilder, String>> eventDTOPropertySetters;
  BiConsumer<EventDTOBuilder, String> eventDTOPropertySetter;

  private final Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>>
      recurringDetailsDTOPropertySetters;

  BiConsumer<RecurringDetailsDTOBuilder, String> recurringDetailsDTOPropertySetter;

  private final EventDTOBuilder eventBuilder;

  private final RecurringDetailsDTOBuilder recurringDetailsDTOBuilder;

  private Integer updatedEvents;

  /**
   * Constructor for EditEventCommand.
   */
  EditEventCommand() {
    eventName = null;
    startTime = null;
    endTime = null;
    eventDTOPropertySetters = createPropertySetters();
    eventBuilder = EventDTO.getBuilder();
    recurringDetailsDTOPropertySetters = createRecurringDetailsPropertySetters();
    recurringDetailsDTOBuilder = RecurringDetailsDTO.getBuilder();
  }


  /**
   * Create a map of property setters for RecurringDetailsDTO.
   *
   * @return A map of property setters for RecurringDetailsDTO.
   */
  private final Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>>
  createRecurringDetailsPropertySetters() {
    Map<String, BiConsumer<RecurringDetailsDTOBuilder, String>> setters = new HashMap<>();
    setters.put("occurrences",
        (builder, value) -> builder.setOccurrences(
            Integer.parseInt(value)
        ));
    setters.put("weekdays",
        (builder, value) -> builder.setRepeatDays(
            CalendarDayOfWeek.parseRepeatDays(value)
        ));
    setters.put("untilDateTime",
        (builder, value) -> builder.setUntilDate(
            LocalDateTime.parse(value, CalendarController.dateTimeFormatter)
        ));
    return setters;
  }

  private final Map<String, BiConsumer<EventDTOBuilder, String>> createPropertySetters() {
    Map<String, BiConsumer<EventDTOBuilder, String>> setters = new HashMap<>();
    setters.put("name",
        (builder, value) -> builder.setSubject(value));
    setters.put("startDateTime",
        (builder, value) -> builder.setStartTime(
            LocalDateTime.parse(value, CalendarController.dateTimeFormatter)));
    setters.put("endDateTime",
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
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    try {
      switch (commandScanner.next()) {
        case "calendar":
          Command command = new EditCalendarCommand();
          return command.parseCommand(commandScanner);
        case "event":
          editSpannedEvent(commandScanner);
          break;
        case "events":
          editRecurringEvents(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: edit (calendar|event|events) ...");
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException(
          "Invalid command format: edit (calendar|event|events) ...");
    }
    return this;
  }

  private void editSpannedEvent(Scanner commandScanner) throws ParseCommandException {
    setPropertySetters(commandScanner);

    try {
      eventName = parseOptionalQuoted(commandScanner);
    } catch (ParseCommandException e) {
      throw new ParseCommandException(e.getMessage() + "edit event <property> <eventName> ...");
    }

    if (!commandScanner.next().equals("from")) {
      throw new ParseCommandException(
          "Invalid command format: edit event <property> <eventName> from ...");
    }

    try {
      startTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid startDateTime format: " + CalendarController.dateTimeFormat);
    }

    if (!commandScanner.next().equals("to")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to ...");
    }

    try {
      endTime = LocalDateTime.parse(commandScanner.next(), CalendarController.dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException(
          "Invalid endDateTime format: " + CalendarController.dateTimeFormat);
    }

    if (!commandScanner.next().equals("with")) {
      throw new ParseCommandException("Invalid command format: edit event <property> <eventName> "
          + "from <dateStringTtimeString> to <dateStringTtimeString> with ...");
    }

    String next = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    parseNewPropertyValue(next);
  }

  private void setPropertySetters(Scanner commandScanner) throws ParseCommandException {
    String propertyName = commandScanner.next();
    eventDTOPropertySetter = eventDTOPropertySetters.get(propertyName);
    recurringDetailsDTOPropertySetter = recurringDetailsDTOPropertySetters.get(
        propertyName);

    if (Objects.isNull(eventDTOPropertySetter)
        && Objects.isNull(recurringDetailsDTOPropertySetter)) {
      throw new ParseCommandException("Invalid property name");
    }
  }

  private String parseOptionalQuoted(Scanner commandScanner) throws ParseCommandException {
    String token = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (token == null) {
      throw new ParseCommandException("Invalid command format: ");
    }
    return token.startsWith("\"") ? token.substring(1, token.length() - 1) : token;
  }

  private void editRecurringEvents(Scanner commandScanner) throws ParseCommandException {
    eventBuilder.setIsRecurring(true);
    setPropertySetters(commandScanner);
    try {
      eventName = parseOptionalQuoted(commandScanner);
    } catch (ParseCommandException e) {
      throw new ParseCommandException(e.getMessage() + "edit events <property> <eventName> ...");
    }

    String next = commandScanner.next();

    if (next.equals("from")) {
      try {
        startTime = LocalDateTime.parse(commandScanner.next(),
            CalendarController.dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new ParseCommandException(
            "Invalid startDateTime format: " + CalendarController.dateTimeFormat);
      }

      if (!commandScanner.next().equals("with")) {
        throw new ParseCommandException(
            "Invalid command format: edit events <property> <eventName> "
                + "from <dateStringTtimeString> with ...");
      }

    } else {
      if (!next.equals("with")) {
        throw new ParseCommandException(
            "Invalid command format: edit events <property> <eventName> "
                + "(from|with) ...");
      }
    }
    try {
      parseNewPropertyValue(parseOptionalQuoted(commandScanner));
    } catch (ParseCommandException e) {
      throw new ParseCommandException(e.getMessage() + "edit events <property> <eventName> "
          + "[from <dateStringTtimeString>] with <propertyValue>");
    }
  }

  private void parseNewPropertyValue(String next) throws ParseCommandException {
    if (next.startsWith("\"") && next.endsWith("\"")) {
      next = next.substring(1, next.length() - 1);
    }
    try {
      if (Objects.nonNull(eventDTOPropertySetter)) {
        eventDTOPropertySetter.accept(eventBuilder, next);
      }
      if (Objects.nonNull(recurringDetailsDTOPropertySetter)) {
        recurringDetailsDTOPropertySetter.accept(recurringDetailsDTOBuilder, next);
      }
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid update Date format provided");
    }
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    updatedEvents = controllerUtility.getCurrentCalendar().model
        .editEvent(eventName, startTime, endTime,
            eventBuilder
                .setRecurringDetails(
                    Objects.nonNull(recurringDetailsDTOPropertySetter) ?
                        recurringDetailsDTOBuilder.build()
                        : null)
                .setIsRecurring(Objects.nonNull(recurringDetailsDTOPropertySetter) ? true : null)
                .build());
  }


  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (updatedEvents > 0) {
      controllerUtility.promptOutput("Successfully updated event(s)");
    } else {
      controllerUtility.promptOutput("No events were updated");
    }
  }
}
