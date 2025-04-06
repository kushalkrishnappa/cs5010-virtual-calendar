package view;

import controller.CalendarFeatures;
import controller.EventData;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface IGUIView extends IView {

  void showDayViewDialog(LocalDate date, List<EventData> events);

  void setMonthYearLabel(YearMonth monthYear);

  void setCalendarMonthDates(YearMonth monthYear);

  void setAvailableCalendars(String[] calendars);

  void setCurrentCalendarTz(String tz);

  void setFeatures(CalendarFeatures features);

  void setCurrentCalendar(String calendarName);

  void showNewCalendarDialog();

  void showEditCalendarDialog();

  void showImportCalendarDialog();

  void showExportCalendarDialog();
}
