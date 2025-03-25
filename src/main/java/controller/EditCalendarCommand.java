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

class EditCalendarCommand extends Command {

  private String calendarName;
  private String newCalendarName;
  private String newTimeZone;
  private final CalendarEntryBuilder calendarEntryBuilder;

  private final Map<String, BiConsumer<CalendarEntryBuilder, String>> calendarEntryPropertySetters;
  BiConsumer<CalendarEntryBuilder, String> calendarEntryPropertySetter;

  EditCalendarCommand() {
    this.calendarName = null;
    this.newCalendarName = null;
    this.calendarEntryPropertySetters = createPropertySetters();
    this.calendarEntryPropertySetter = null;
    this.calendarEntryBuilder = CalendarEntry.getBuilder();
  }

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

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    while (commandScanner.hasNext()) {
      switch (commandScanner.next()) {
        case "--name":
          parseCalendarName(commandScanner);
          break;
        case "--property":
          parseCalendarProperty(commandScanner);
      }
    }
    if (Objects.isNull(calendarName) || Objects.isNull(calendarEntryPropertySetter)) {
      throw new ParseCommandException("Please specify calendar name and property to update");
    }
    return this;
  }

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
              .build()
          , false);
    });
    return newModel;
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Calendar updated successfully");
  }
}
