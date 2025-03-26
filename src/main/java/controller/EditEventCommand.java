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
 * This class represents an implementation of the abstract Command class to edit an event in the
 * calendar. It allows the user to update the properties of an event such as name, start time, end
 * time, description, location, and recurrence details.
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
   * The constructor for EditEventCommand class initializes the eventName, startTime, endTime to
   * null. Setters for eventDTO and recurringDetailsDTO are created. It also initializes the
   * eventBuilder and recurringDetailsDTOBuilder.
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

  /**
   * Create a map of property setters for EventDTO.
   *
   * @return A map of property setters for EventDTO.
   */
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

  /**
   * This method parses the command provided by the user. It reads the command from the scanner and
   * determines the type of command (edit calendar, edit event, or edit events). It then calls the
   * appropriate method to continue parsing the command.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return the command object
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * Parse the command to edit a specific event. Reading from  the scanner, it determines the
   * properties to be updated. It also checks if the command is in the correct format.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
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

  /**
   * This method sets the property setters for the event and recurring details DTOs.
   *
   * @param commandScanner the scanner object that reads the command
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * This method parses the command to read an optional quoted string. If string is enclosed in
   * quotes, it removes the quotes and returns the string.
   *
   * @param commandScanner the scanner object that reads the command
   * @return the token read from the scanner
   * @throws ParseCommandException if the command provided is invalid
   */
  private String parseOptionalQuoted(Scanner commandScanner) throws ParseCommandException {
    String token = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (token == null) {
      throw new ParseCommandException("Invalid command format: ");
    }
    return token.startsWith("\"") ? token.substring(1, token.length() - 1) : token;
  }

  /**
   * Parse the command to edit recurring events. It reads the command from the scanner and
   * determines the properties to be updated. It also checks if the command is in the correct
   * format.
   *
   * @param commandScanner Scanner object to parse the command
   * @throws ParseCommandException If the command is not in the correct format
   */
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

  /**
   * This method parses the new property value provided by the user. If the value is in quotes, it
   * removes the quotes and sets the value to the corresponding property setter.
   *
   * @param next the next token read from the scanner
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * Execute the edit event command on the model. It updates the event with the new properties
   * provided by the user.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar
   * @throws EventConflictException  if there is a conflict with the event
   */
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


  /**
   * Prompt the result of the edit event command with a message.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    if (updatedEvents > 0) {
      controllerUtility.promptOutput("Successfully updated event(s)");
    } else {
      controllerUtility.promptOutput("No events were updated");
    }
  }
}
