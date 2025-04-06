package controller;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarFeaturesAdaptor implements CalendarFeatures {

  final private CalendarFeatures controller;

  public CalendarFeaturesAdaptor(CalendarFeatures controller) {
    this.controller = controller;
  }

  @Override
  public void importCalendarFromFile(String filePath) {
    controller.importCalendarFromFile(filePath);
  }

  @Override
  public void createCalendar(String calendarName, String timezone) {
    controller.createCalendar(calendarName, timezone);
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
}
