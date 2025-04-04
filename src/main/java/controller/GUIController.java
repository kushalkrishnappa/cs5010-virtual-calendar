package controller;

import dto.EventDTO;
import dto.ImportResult;
import exception.CalendarExportException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
      try {
        EditCalendarCommand editCommand = new EditCalendarCommand(currentCalendarName, newCalendarName, newTimezone);
        editCommand.executeCommand(controllerUtility);
        // refresh the calendar list in the view
        view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
        view.setCurrentCalendarTz(newTimezone);
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
    view.setCurrentCalendarTz(controllerUtility.getCurrentCalendar().zoneId.getId());
  }

  @Override
  public void viewDay(LocalDate date) {
    System.out.println("View Day: " + date.toString());
    view.showDayViewDialog(date);
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

}
