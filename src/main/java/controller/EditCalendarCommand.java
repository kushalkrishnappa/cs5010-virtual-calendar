package controller;

import controller.CalendarController.ControllerUtility;
import controller.CalendarEntry.CalendarEntryBuilder;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiConsumer;

class EditCalendarCommand extends Command {

  private String calendarName;
  private String newCalendarName;
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
    propertySetters.put("timezone", (builder, zoneIdString) -> builder.setZoneId(zoneIdString));
    propertySetters.put("name", (builder, name) -> {
      newCalendarName = name;
      return;
    });
    return propertySetters;
  }

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    while(commandScanner.hasNext()) {
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
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    CalendarEntry calendarEntry = controllerUtility.removeCalendarEntry(calendarName);
    CalendarEntry updatedCalendarEntry = calendarEntryBuilder.setModel(calendarEntry.model).build();

    if (Objects.nonNull(newCalendarName)) {
      controllerUtility.addCalendarEntry(newCalendarName, calendarEntry);
    } else{
      controllerUtility.addCalendarEntry(calendarName, updatedCalendarEntry);
    }

  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {

  }
}
