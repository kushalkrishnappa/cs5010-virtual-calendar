package controller;

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
  }

  @Override
  public void run() {
    view.setAvailableCalendars(controllerUtility.getAllCalendarNames());
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
  public void exportCalendar() {
    System.out.println("Exporting Calendar");
  }

  @Override
  public void importCalendar() {
    System.out.println("Importing Calendar");
  }

  @Override
  public void switchCalendar(String calendarName) {
    System.out.println("Switching Calendar");
  }

}
