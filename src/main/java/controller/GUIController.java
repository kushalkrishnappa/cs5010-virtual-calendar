package controller;

import dto.EventDTO;
import dto.ImportResult;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import model.CalendarDayOfWeek;
import model.IModel;
import service.CSVCalendarImporter;
import service.ICalendarImporter;
import view.IGUIView;

/**
 * This class represents the GUI controller for the calendar application. It builds upon the
 * existing controller and implements the CalendarFeatures interface. The GUIController is
 * responsible for handling user interactions, managing the calendar data, and updating the view
 * accordingly.
 */
public class GUIController extends CalendarController implements CalendarFeatures {

  private final IGUIView view;

  private static final String editThisEvent = "This event only";

  private static final String editThisAndFollowingEvents =
      "This and following events with same name";

  private static final String editAllEvents = "All events with same name";

  private EventData existingEventData;

  private EventData newEventData;

  private Boolean isRecurringDetailsChanged;

  private ICalendarImporter importer;

  /**
   * Constructor for initializing the class attributes for the controller.
   *
   * @param modelFactory The model factory for the calendar application
   * @param view         The view for the calendar application
   */
  public GUIController(Supplier<IModel> modelFactory, IGUIView view) {
    super(modelFactory, view, ControllerMode.GUI);
    this.view = view;
    importer = new CSVCalendarImporter();
  }

  /**
   * This method sets the importer strategy dynamically.
   *
   * @param importer The calendar importer to be set
   */
  public void setImporter(ICalendarImporter importer) {
    this.importer = importer;
  }

  @Override
  public void run() {
    createDefaultCalendar();
    view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
    view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
    view.setMonthYearLabel(YearMonth.now());
    view.setCalendarMonthDates(YearMonth.now());
    view.setFeatures(new CalendarFeaturesAdaptor(this));
  }

  private void createDefaultCalendar() {
    controllerUtility.addCalendarEntry("Default", CalendarEntry.getBuilder()
        .setModel(controllerUtility.getModelFactory().get())
        .setZoneId(ZoneId.systemDefault().getId())
        .build()
    );
    controllerUtility.setCurrentCalendar("Default");
  }

  @Override
  public void importCalendarFromFile(String filePath) {
    Consumer<EventDTO> eventConsumer = eventDto -> {
      controllerUtility.getCurrentCalendar()
          .model.createEvent(eventDto, true);
    };

    try (FileReader reader = new FileReader(filePath)) {
      ImportResult importResult = importer.importEvents(reader, eventConsumer);
      view.displayMessage(importResult.generateSummary());
    } catch (FileNotFoundException e) {
      view.displayError("Import Error: File not found - " + filePath);
    } catch (IOException e) {
      view.displayError("Import Error: Could not read file - " + e.getMessage());
    }
  }

  @Override
  public void createCalendar(String newCalendarName, String newTimezone) {
    if (newCalendarName != null && !newCalendarName.isEmpty() && newTimezone != null
        && !newTimezone.isEmpty()) {
      try {
        // create new calendar entry
        CreateCalendarCommand createCalendarCommand = new CreateCalendarCommand(newCalendarName,
            newTimezone);
        createCalendarCommand.executeCommand(controllerUtility);

        // refresh the calendar list in the view
        view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
        // set the current calendar to the new one
        view.setCurrentCalendar(newCalendarName);
        view.setCurrentCalendarTz(newTimezone);
        // show success message
        view.displayMessage("Calendar '" + newCalendarName + "' created successfully!");
      } catch (Exception e) {
        view.displayError("Error creating calendar: " + e.getMessage());
      }
    } else {
      view.displayError("Please enter value in fields");
    }
  }

  @Override
  public void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone) {
    if (newCalendarName != null && !newCalendarName.isEmpty() && newTimezone != null
        && !newTimezone.isEmpty()) {
      String updatedCalendarName = newCalendarName;
      if (newCalendarName.equals(currentCalendarName)) {
        newCalendarName = null;
      }
      try {
        EditCalendarCommand editCommand = new EditCalendarCommand(
            currentCalendarName,
            newCalendarName,
            newTimezone.equals(controllerUtility.getCurrentCalendar().zoneId.getId())
                ? null
                : newTimezone);
        editCommand.executeCommand(controllerUtility);
        // refresh the calendar list in the view
        view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
        view.setCurrentCalendar(updatedCalendarName);
        view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
      } catch (Exception e) {
        view.displayError("Error editing calendar: " + e.getMessage());
      }
    } else {
      view.displayError("Please enter value in fields");
    }
  }

  @Override
  public void exportCalendar(String saveFilePath) {
    ExportCalendarCommand exportCalendarCommand = new ExportCalendarCommand(saveFilePath);
    try {
      exportCalendarCommand.executeCommand(controllerUtility);
      exportCalendarCommand.promptResult(controllerUtility);
    } catch (CalendarExportException e) {
      view.displayError(e.getMessage());
    }
  }

  @Override
  public void switchCalendar(String calendarName) {
    try {
      UseCommand command = new UseCommand(calendarName);
      command.executeCommand(controllerUtility);
      view.setCurrentCalendar(calendarName);
      view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
    } catch (Exception e) {
      view.displayError("Error switching calendar: " + e.getMessage());
    }
  }

  @Override
  public void requestCalendarCreation() {
    view.showNewCalendarDialog();
  }

  @Override
  public void requestCalendarEdit() {
    view.showEditCalendarDialog();
  }

  @Override
  public void requestCalendarExport() {
    view.showExportCalendarDialog();
  }

  @Override
  public void requestCalendarImport() {
    view.showImportCalendarDialog();
  }

  @Override
  public void viewDay(LocalDate date) {
    List<EventDTO> eventsOnDate = controllerUtility.getCurrentCalendar()
        .model.getEventsOnDate(date);
    List<EventData> events = convertEventDTOsToEventData(eventsOnDate);
    view.showDayViewDialog(date, events);
  }

  @Override
  public void nextMonthYear(YearMonth yearMonth) {
    YearMonth nextMonth = yearMonth.plusMonths(1);
    view.setMonthYearLabel(nextMonth);
    view.setCalendarMonthDates(nextMonth);
  }

  @Override
  public void previousMonthYear(YearMonth yearMonth) {
    YearMonth previousMonth = yearMonth.minusMonths(1);
    view.setMonthYearLabel(previousMonth);
    view.setCalendarMonthDates(previousMonth);
  }

  private List<EventData> convertEventDTOsToEventData(List<EventDTO> eventsDTOs) {
    List<EventData> events = new ArrayList<>();

    for (EventDTO dto : eventsDTOs) {
      events.add(
          EventData.getBuilder()
              .setSubject(dto.getSubject())
              .setDescription(dto.getDescription())
              .setLocation(dto.getLocation())
              .setIsRecurring(dto.getIsRecurring())
              .setIsPublic(dto.getIsPublic())
              .setStartTime(dto.getStartTime())
              .setEndTime(dto.getEndTime())
              .setIsAllDay(dto.getIsAllDay())
              .setRecurringDetails(createRecurringDTOToRecurringData(dto.getRecurringDetails()))
              .build()
      );
    }
    return events;
  }

  private RecurrenceData createRecurringDTOToRecurringData(
      RecurringDetailsDTO recurringDetailsDto) {
    if (recurringDetailsDto == null) {
      return null;
    }
    return new RecurrenceData(
        recurringDetailsDto.getOccurrences(),
        recurringDetailsDto.getRepeatDays().stream().map(
            calendarDayOfWeek -> CalendarWeekDays.valueOf(calendarDayOfWeek.name())
        ).collect(Collectors.toSet()),
        recurringDetailsDto.getUntilDate()
    );
  }

  @Override
  public void createEvent(EventData eventData) {
    if (eventData.getRecurring() && eventData.getRecurringDetails().getRepeatDays().isEmpty()) {
      view.displayError("Select a repeat week day");
      return;
    }
    try {
      CreateEventCommand createEventCommand = new CreateEventCommand(
          eventData.getSubject(),
          true,
          eventData.getStartTime(),
          eventData.getEndTime(),
          eventData.getDescription(),
          eventData.getLocation(),
          eventData.getPublic(),
          eventData.getRecurring(),
          eventData.getAllDay(),
          eventData.getRecurring()
              ? eventData.getRecurringDetails().getRepeatDays().stream().map(
                  calendarWeekDays ->
                      CalendarDayOfWeek.valueOf(calendarWeekDays.name()))
              .collect(Collectors.toSet())
              : null,
          eventData.getRecurring()
              ? eventData.getRecurringDetails().getUntilDate()
              : null,
          eventData.getRecurring()
              ? eventData.getRecurringDetails().getOccurrences()
              : null
      );
      createEventCommand.executeCommand(controllerUtility);
      createEventCommand.promptResult(controllerUtility);
      LocalDate date = eventData.getStartTime().toLocalDate();
      List<EventDTO> eventsOnDate = controllerUtility.getCurrentCalendar()
          .model.getEventsOnDate(date);
      List<EventData> events = convertEventDTOsToEventData(eventsOnDate);
      view.showDayViewDialog(date, events);
    } catch (EventConflictException e) {
      view.displayError("The event conflicts with another existing event.");
    } catch (Exception e) {
      view.displayError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(EventData existingEventData, EventData newEventData) {

    isRecurringDetailsChanged =
        computeIsRecurringDetailsChanged(existingEventData, newEventData) ? true : null;
    if (existingEventData.getRecurring()) {
      this.existingEventData = existingEventData;
      this.newEventData = newEventData;
      if (Boolean.TRUE.equals(isRecurringDetailsChanged)) {
        view.displayRecurringEventOptions(
            new String[]{editThisAndFollowingEvents, editAllEvents});
      } else {
        view.displayRecurringEventOptions(
            new String[]{editThisEvent, editThisAndFollowingEvents, editAllEvents});
      }
    } else {
      invokeEditEventCommand(existingEventData, newEventData, isRecurringDetailsChanged,
          existingEventData.getStartTime(), existingEventData.getEndTime());
    }

  }

  private void invokeEditEventCommand(EventData existingEventData, EventData newEventData,
      Boolean isRecurringDetailsChanged, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    EditEventCommand editEventCommand = new EditEventCommand(
        existingEventData.getSubject(),
        startDateTime,
        endDateTime,
        newEventData.getSubject().equals(existingEventData.getSubject())
            ? null : newEventData.getSubject(),
        newEventData.getStartTime().isEqual(existingEventData.getStartTime())
            ? null : newEventData.getStartTime(),
        newEventData.getEndTime().isEqual(existingEventData.getEndTime())
            ? null : newEventData.getEndTime(),
        newEventData.getDescription().equals(existingEventData.getDescription())
            ? null : newEventData.getDescription(),
        newEventData.getLocation().equals(existingEventData.getLocation())
            ? null : newEventData.getLocation(),
        newEventData.getPublic().equals(existingEventData.getPublic())
            ? null : newEventData.getPublic(),
        isRecurringDetailsChanged,
        newEventData.getAllDay().equals(existingEventData.getAllDay())
            ? null : newEventData.getAllDay(),
        Boolean.TRUE.equals(isRecurringDetailsChanged)
            ? newEventData.getRecurringDetails().getRepeatDays().stream().map(
                calendarWeekDays ->
                    CalendarDayOfWeek.valueOf(calendarWeekDays.name()))
            .collect(Collectors.toSet())
            : null,
        Boolean.TRUE.equals(isRecurringDetailsChanged)
            ? newEventData.getRecurringDetails().getUntilDate()
            : null,
        Boolean.TRUE.equals(isRecurringDetailsChanged)
            ? newEventData.getRecurringDetails().getOccurrences()
            : null
    );
    try {
      editEventCommand.executeCommand(controllerUtility);
      editEventCommand.promptResult(controllerUtility);
      LocalDate date = newEventData.getStartTime().toLocalDate();
      List<EventDTO> eventsOnDate = controllerUtility.getCurrentCalendar()
          .model.getEventsOnDate(date);
      List<EventData> events = convertEventDTOsToEventData(eventsOnDate);
      view.showDayViewDialog(date, events);
    } catch (Exception exception) {
      view.displayError("Error creating event: " + exception.getMessage());
    }
  }

  @Override
  public void selectedRecurringEventOption(String choice) {
    switch (choice) {
      case editThisEvent:
        invokeEditEventCommand(existingEventData, newEventData, null,
            existingEventData.getStartTime(), existingEventData.getEndTime());
        break;
      case editThisAndFollowingEvents:
        invokeEditEventCommand(existingEventData, newEventData, isRecurringDetailsChanged,
            existingEventData.getStartTime(), null);
        break;
      case editAllEvents:
        invokeEditEventCommand(existingEventData, newEventData, isRecurringDetailsChanged,
            null, null);
        break;
    }
  }

  private boolean isWeekDaysUpdated(EventData existingEventData, EventData newEventData) {
    return !newEventData.getRecurringDetails().getRepeatDays().stream().map(
            calendarWeekDays ->
                CalendarDayOfWeek.valueOf(calendarWeekDays.name()))
        .collect(Collectors.toSet())
        .equals(existingEventData.getRecurringDetails().getRepeatDays().stream().map(
                calendarWeekDays ->
                    CalendarDayOfWeek.valueOf(calendarWeekDays.name()))
            .collect(Collectors.toSet()));
  }

  private boolean isOccurrenceOrUntilDateUpdated(EventData existingEventData,
      EventData newEventData) {
    if (Objects.nonNull(existingEventData.getRecurringDetails().getOccurrences())) {
      // is existing occurrence changed
      if (Objects.nonNull(newEventData.getRecurringDetails().getUntilDate())) {
        return true;
      }
      return !existingEventData.getRecurringDetails().getOccurrences()
          .equals(newEventData.getRecurringDetails().getOccurrences());
    } else {
      // is existing until date changed
      if (Objects.nonNull(newEventData.getRecurringDetails().getOccurrences())) {
        return true;
      }
      return !existingEventData.getRecurringDetails().getUntilDate()
          .isEqual(newEventData.getRecurringDetails().getUntilDate());
    }
  }

  private boolean computeIsRecurringDetailsChanged(EventData existingEventData,
      EventData newEventData) {
    if (newEventData.getRecurring()) {
      if (!existingEventData.getRecurring()) {
        return true;
      }
      if (isWeekDaysUpdated(existingEventData, newEventData)) {
        return true;
      }
      if (isOccurrenceOrUntilDateUpdated(existingEventData, newEventData)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void requestEventCreation(LocalDate localDate) {
    view.showCreateEventDialog(localDate);
  }

  @Override
  public void requestEventEdit(EventData eventData) {
    view.showEditEventDialog(eventData);
  }

  @Override
  public void requestEventViewDetails(EventData eventData) {
    view.showEventDetailsDialog(eventData);
  }
}
