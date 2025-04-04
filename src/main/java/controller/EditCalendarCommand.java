package controller;

import controller.CalendarController.ControllerUtility;
import controller.CalendarEntry.CalendarEntryBuilder;
import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;
import model.IModel;

/**
 * This class represents an implementation of abstract Command class to edit a calendar entry. It
 * allows the user to update the name and time zone of an existing calendar entry.
 */
class EditCalendarCommand extends Command {

  private String calendarName;

  private String newCalendarName;

  private String newTimeZone;

  private final CalendarEntryBuilder calendarEntryBuilder;

  private final Map<String, BiConsumer<CalendarEntryBuilder, String>> calendarEntryPropertySetters;

  BiConsumer<CalendarEntryBuilder, String> calendarEntryPropertySetter;

  /**
   * The constructor for EditCalendarCommand class initializes the calendarName, newCalendarName,
   * newTimeZone to null. It also initializes the calendarEntryPropertySetters map and
   * calendarEntryBuilder.
   */
  EditCalendarCommand() {
    this.calendarName = null;
    this.newCalendarName = null;
    this.calendarEntryPropertySetters = createPropertySetters();
    this.calendarEntryPropertySetter = null;
    this.calendarEntryBuilder = CalendarEntry.getBuilder();
  }

  EditCalendarCommand(String calendarName, String newCalendarName, String newTimeZone){
    this.calendarName = calendarName;
    this.newCalendarName = newCalendarName;
    this.newTimeZone = newTimeZone;
    this.calendarEntryPropertySetters = createPropertySetters();
    this.calendarEntryPropertySetter = null;
    this.calendarEntryBuilder = CalendarEntry.getBuilder();
  }

  /**
   * This method creates a map of property setters for the CalendarEntryBuilder. The map contains
   * the property names as keys and the corresponding setter methods as values.
   *
   * @return a map of property setters for the CalendarEntryBuilder
   */
  private Map<String, BiConsumer<CalendarEntryBuilder, String>> createPropertySetters() {
    Map<String, BiConsumer<CalendarEntryBuilder, String>> propertySetters = new HashMap<>();
    propertySetters.put("timezone",
        (builder, zoneIdString) -> {
          builder.setZoneId(zoneIdString);
          newTimeZone = zoneIdString;
        });
    propertySetters.put("name",
        (builder, name) -> {
          newCalendarName = name;
          return;
        });
    return propertySetters;
  }

  /**
   * This method starts parsing the edit calendar command from the Scanner object. It checks if the
   * edit calendar command contains `--name` or `--property` keyword before calling the appropriate
   * method to continue the parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   */
  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    while (commandScanner.hasNext()) {
      switch (commandScanner.next()) {
        case "--name":
          parseCalendarName(commandScanner);
          break;
        case "--property":
          parseCalendarProperty(commandScanner);
          break;
        default:
          throw new ParseCommandException(
              "Invalid command format: edit calendar --name <calendar name> "
                  + "--property <property name> <new property value>");
      }
    }
    if (Objects.isNull(calendarName) || Objects.isNull(calendarEntryPropertySetter)) {
      throw new ParseCommandException("Please specify calendar name and property to update");
    }
    return this;
  }

  /**
   * Parse the calendar property from the command scanner. It reads the next token from the scanner
   * and sets the calendarEntryPropertySetter to the corresponding setter method from the map.
   *
   * @param commandScanner the scanner object that reads the command
   * @throws ParseCommandException if the command provided is invalid
   */
  private void parseCalendarProperty(Scanner commandScanner) throws ParseCommandException {
    String propertyName = commandScanner.next();
    calendarEntryPropertySetter = calendarEntryPropertySetters.get(propertyName);

    if (Objects.isNull(calendarEntryPropertySetter)) {
      throw new ParseCommandException("Invalid property name");
    }

    String value = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (value.startsWith("\"") && value.endsWith("\"")) {
      value = value.substring(1, value.length() - 1);
    }
    calendarEntryPropertySetter.accept(calendarEntryBuilder, value);
  }

  /**
   * Parse the calendar name from the command scanner. It reads the next token from the scanner and
   * sets the calendarName to the value read. If the calendar name is not found, it throws a
   * ParseCommandException.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @throws ParseCommandException if the command provided is invalid
   */
  private void parseCalendarName(Scanner commandScanner) throws ParseCommandException {
    calendarName = commandScanner.findWithinHorizon("\"([^\"]*)\"|\\S+", 0);
    if (Objects.isNull(calendarName)) {
      throw new ParseCommandException("Invalid command format: create calendar --name <name>");
    }
    calendarName =
        calendarName.startsWith("\"")
            ? calendarName.substring(1, calendarName.length() - 1)
            : calendarName;
  }

  /**
   * Execute the edit calendar command on the model. It updates the calendar entry with the provided
   * name and time zone. If the calendar name is not found, it throws an IllegalArgumentException.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar
   * @throws EventConflictException  if there is a conflict with the event
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {

    CalendarEntry calendarEntry = controllerUtility.removeCalendarEntry(calendarName);
    if (Objects.isNull(calendarEntry)) {
      throw new IllegalArgumentException("Calendar with the provided name doesn't exists");
    }

    CalendarEntry updatedCalendarEntry = calendarEntryBuilder.setModel(
            Objects.nonNull(newTimeZone)
                ? shiftCalendarTimezone(calendarEntry.model, calendarEntry.zoneId,
                ZoneId.of(newTimeZone), controllerUtility.getModelFactory().get())
                : calendarEntry.model
        )
        .setZoneId(
            Objects.nonNull(newTimeZone)
                ? newTimeZone
                : calendarEntry.zoneId.toString())
        .build();

    if (Objects.nonNull(newCalendarName)) {
      controllerUtility.addCalendarEntry(newCalendarName, calendarEntry);
    } else {
      controllerUtility.addCalendarEntry(calendarName, updatedCalendarEntry);
    }

  }

  /**
   * This method shifts the calendar timezone of the model to the new timezone. It iterates through
   * all the events in the existing model and creates new events in the new model with the updated
   * timezone.
   *
   * @param existingModel  the existing model with old timezone
   * @param existingZoneId the existing timezone of the model
   * @param newZoneId      the new timezone to shift to
   * @param newModel       the model to which the events will be shifted
   * @return the new model with updated timezone
   */
  private IModel shiftCalendarTimezone(IModel existingModel, ZoneId existingZoneId,
      ZoneId newZoneId, IModel newModel) {
    existingModel.getAllEvents().forEach(event -> {
      newModel.createEvent(
          EventDTO.getBuilder()
              .setSubject(event.getSubject())
              .setDescription(event.getDescription())
              .setLocation(event.getLocation())
              .setIsPublic(event.getIsPublic())
              .setIsAllDay(event.getIsAllDay())
              .setIsRecurring(event.getIsRecurring())
              .setStartTime(event.getStartTime()
                  .atZone(existingZoneId)
                  .withZoneSameInstant(newZoneId)
                  .toLocalDateTime())
              .setEndTime(event.getEndTime()
                  .atZone(existingZoneId)
                  .withZoneSameInstant(newZoneId)
                  .toLocalDateTime())
              .setRecurringDetails(
                  Objects.nonNull(event.getRecurringDetails())
                      ? RecurringDetailsDTO.getBuilder()
                      .setRepeatDays(event.getRecurringDetails().getRepeatDays())
                      .setOccurrences(event.getRecurringDetails().getOccurrences())
                      .setUntilDate(
                          Objects.nonNull(event.getRecurringDetails().getUntilDate())
                              ? event.getRecurringDetails().getUntilDate()
                              .atZone(existingZoneId)
                              .withZoneSameInstant(newZoneId)
                              .toLocalDateTime()
                              : null
                      )
                      .build()
                      : null
              )
              .build(), false);
    });
    return newModel;
  }

  /**
   * Prompt the result of edit calendar command with message.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Calendar updated successfully");
  }
}
