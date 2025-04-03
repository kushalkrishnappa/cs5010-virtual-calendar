package controller;

import java.time.LocalDate;

public interface CalendarFeatures {

  void importCalendarFromFile(String filePath);
  // calendar operation features
  void createCalendar();
  void editCalendar(String calendarName);
  void exportCalendar(String saveFilePath);
  void switchCalendar(String calendarName);
  // calendar event features
  void viewDay(LocalDate date);
}
