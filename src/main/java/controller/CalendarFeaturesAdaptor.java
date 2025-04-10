package controller;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * This Class is a class adaptor for {@link GUIController} to be presented as
 * {@link CalendarFeatures}.
 */
public class CalendarFeaturesAdaptor implements CalendarFeatures {

  private final CalendarFeatures controller;

  /**
   * Instantiates a new Calendar features adaptor.
   *
   * @param controller the controller
   */
  public CalendarFeaturesAdaptor(CalendarFeatures controller) {
    this.controller = controller;
  }

  @Override
  public void importCalendarFromFile(String filePath) {
    controller.importCalendarFromFile(filePath);
  }

  @Override
  public void createCalendar(String newCalendarName, String newTimezone) {
    controller.createCalendar(newCalendarName, newTimezone);
  }

  @Override
  public void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone) {
    controller.editCalendar(currentCalendarName, newCalendarName, newTimezone);
  }

  @Override
  public void exportCalendar(String saveFilePath) {
    controller.exportCalendar(saveFilePath);
  }

  @Override
  public void switchCalendar(String calendarName) {
    controller.switchCalendar(calendarName);
  }

  @Override
  public void requestCalendarCreation() {
    controller.requestCalendarCreation();
  }

  @Override
  public void requestCalendarEdit() {
    controller.requestCalendarEdit();
  }

  @Override
  public void requestCalendarExport() {
    controller.requestCalendarExport();
  }

  @Override
  public void requestCalendarImport() {
    controller.requestCalendarImport();
  }

  @Override
  public void viewDay(LocalDate date) {
    controller.viewDay(date);
  }

  @Override
  public void nextMonthYear(YearMonth yearMonth) {
    controller.nextMonthYear(yearMonth);
  }

  @Override
  public void previousMonthYear(YearMonth yearMonth) {
    controller.previousMonthYear(yearMonth);
  }

  @Override
  public void createEvent(EventData eventData) {
    controller.createEvent(eventData);
  }

  @Override
  public void editEvent(EventData existingEventData, EventData newEventData) {
    controller.editEvent(existingEventData, newEventData);
  }

  @Override
  public void selectedRecurringEventOption(String choice) {
    controller.selectedRecurringEventOption(choice);
  }

  @Override
  public void requestEventCreation(LocalDate localDate) {
    controller.requestEventCreation(localDate);
  }

  @Override
  public void requestEventEdit(EventData eventData) {
    controller.requestEventEdit(eventData);
  }

  @Override
  public void requestEventViewDetails(EventData eventData) {
    controller.requestEventViewDetails(eventData);
  }
}
