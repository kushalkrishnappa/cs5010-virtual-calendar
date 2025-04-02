package controller;

import java.time.LocalDate;

public interface CalendarFeatures {

  void importCalendarFromFile(String filePath);
  // calendar operation features
  void createCalendar();
  void editCalendar(String calendarName);
  void exportCalendar();
  void importCalendar();
  void switchCalendar(String calendarName);
}
