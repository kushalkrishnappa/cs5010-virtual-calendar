package controller;

import java.time.LocalDate;

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
  public void createCalendar() {

  }

  @Override
  public void editCalendar(String calendarName) {

  }

  @Override
  public void exportCalendar(String saveFilePath) {
    controller.exportCalendar(saveFilePath);
  }


  @Override
  public void switchCalendar(String calendarName) {

  }

  @Override
  public void viewDay(LocalDate date) {
    controller.viewDay(date);
  }

}
