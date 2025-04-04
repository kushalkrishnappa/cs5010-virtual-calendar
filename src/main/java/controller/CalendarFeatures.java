package controller;

import java.time.LocalDate;
import java.time.YearMonth;

public interface CalendarFeatures {

  void importCalendarFromFile(String filePath);

  // calendar operation features
  void createCalendar(String calendarName, String timezone);
  void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone);
  void exportCalendar(String saveFilePath);
  void switchCalendar(String calendarName);

  // calendar view features
  void viewDay(LocalDate date);
  void nextMonthYear(YearMonth yearMonth);
  void previousMonthYear(YearMonth yearMonth);
}
