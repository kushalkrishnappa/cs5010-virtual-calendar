package controller;

import java.time.LocalDate;
import exception.CalendarExportException;
import java.time.YearMonth;
import java.time.ZoneId;
import dto.EventDTO;
import dto.ImportResult;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
  public void createCalendar() {
    System.out.println("Creating Calendar");
  }

  @Override
  public void editCalendar(String calendarName) {
    System.out.println("Editing Calendar");
  }

  @Override
  public void exportCalendar(String saveFilePath) {
    ExportCalendarCommand exportCalendarCommand = new ExportCalendarCommand(saveFilePath);
    try {
      exportCalendarCommand.executeCommand(controllerUtility);
    } catch (CalendarExportException e){
      view.displayError(e.getMessage());
    }
    exportCalendarCommand.promptResult(controllerUtility);
  }

  @Override
  public void switchCalendar(String calendarName) {
    System.out.println("Switching Calendar");
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
