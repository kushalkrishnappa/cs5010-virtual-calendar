package controller;

import java.time.LocalDate;
import java.time.YearMonth;

public interface CalendarFeatures {

  void importCalendarFromFile(String filePath);

  // calendar operation features
  void createCalendar(String newCalendarName, String newTimezone);
  void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone);
  void exportCalendar(String saveFilePath);
  void switchCalendar(String calendarName);
  void requestCalendarCreation();
  void requestCalendarEdit();
  void requestCalendarExport();
  void requestCalendarImport();

  // calendar view features
  void viewDay(LocalDate date);
  void nextMonthYear(YearMonth yearMonth);
  void previousMonthYear(YearMonth yearMonth);

  // event operations
  void createEvent(EventData eventData);

  void editEvent(EventData existingEventData, EventData newEventData);

  void selectedRecurringEventOption(String choice);
}
