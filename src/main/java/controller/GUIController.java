package controller;

import dto.EventDTO;
import dto.ImportResult;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import model.CalendarDayOfWeek;
import model.IModel;
import service.CSVCalendarImporter;
import service.ICalendarImporter;
import view.IGUIView;

public class GUIController extends CalendarController implements CalendarFeatures {

  private final IGUIView view;

  /**
   * Constructor for initializing the class attributes for the controller.
   *
   * @param modelFactory The model factory for the calendar application
   * @param view         The view for the calendar application
   */
  public GUIController(Supplier<IModel> modelFactory, IGUIView view) {
    super(modelFactory, view, ControllerMode.GUI);
    this.view = view;
    controllerUtility.addCalendarEntry("Default", CalendarEntry.getBuilder()
        .setModel(controllerUtility.getModelFactory().get())
        .setZoneId(ZoneId.systemDefault().getId())
        .build()
    );
    controllerUtility.setCurrentCalendar("Default");
  }

  @Override
  public void run() {
    view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
    view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
    view.setMonthYearLabel(YearMonth.now());
    view.setCalendarMonthDates(YearMonth.now());
    view.setFeatures(new CalendarFeaturesAdaptor(this));
  }

  @Override
  public void importCalendarFromFile(String filePath) {
    ICalendarImporter importer = new CSVCalendarImporter();
    Consumer<EventDTO> eventConsumer = eventDto -> {
      controllerUtility.getCurrentCalendar()
          .model.createEvent(eventDto, true);
    };

    try (FileReader reader = new FileReader(filePath)) {
      ImportResult importResult = importer.importEvents(reader, eventConsumer);
      view.displayMessage(importResult.generateSummary());
      // TODO: refresh view
    } catch (FileNotFoundException e) {
      view.displayError("Import Error: File not found - " + filePath);
    } catch (IOException e) {
      view.displayError("Import Error: Could not read file - " + e.getMessage());
    }
  }

  @Override
  public void createCalendar(String calendarName, String timezone) {
    if (calendarName != null && !calendarName.isEmpty() && timezone != null
        && !timezone.isEmpty()) {
      try {
        // create new calendar entry
        controllerUtility.addCalendarEntry(calendarName, CalendarEntry.getBuilder()
            .setModel(controllerUtility.getModelFactory().get())
            .setZoneId(timezone)
            .build());
        // refresh the calendar list in the view
        view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
        // set the current calendar to the new one
        view.setCurrentCalendar(calendarName);
        view.setCurrentCalendarTz(timezone);
        // show success message
        view.displayMessage("Calendar '" + calendarName + "' created successfully!");
      } catch (Exception e) {
        view.displayError("Error creating calendar: " + e.getMessage());
      }
    }
  }

  @Override
  public void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone) {
    if (newCalendarName != null && !newCalendarName.isEmpty() && newTimezone != null
        && !newTimezone.isEmpty()) {
      if (newCalendarName.equals(currentCalendarName)) {
        newCalendarName = null;
      } else if (Arrays.asList(controllerUtility.getAllCalendarNames()).contains(newCalendarName)) {
        view.displayError("Calendar name already exists.");
        return;
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
        view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
      } catch (Exception e) {
        view.displayError("Error editing calendar: " + e.getMessage());
      }
    }
  }

  @Override
  public void exportCalendar(String saveFilePath) {
    ExportCalendarCommand exportCalendarCommand = new ExportCalendarCommand(saveFilePath);
    try {
      exportCalendarCommand.executeCommand(controllerUtility);
    } catch (CalendarExportException e) {
      view.displayError(e.getMessage());
    }
    exportCalendarCommand.promptResult(controllerUtility);
  }

  @Override
  public void switchCalendar(String calendarName) {
    controllerUtility.setCurrentCalendar(calendarName);
    view.setCurrentCalendar(calendarName);
    view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
  }

  @Override
  public void viewDay(LocalDate date) {
    System.out.println("View Day: " + date.toString());
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
    } catch (Exception e) {
      // TODO: catch proper exceptions
      view.displayError("Error creating event: " + e.getMessage());
    }
  }
}
