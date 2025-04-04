package view;

import controller.CalendarFeatures;
import java.time.LocalDate;
import java.time.YearMonth;

public interface IGUIView extends IView {

  void showDayViewDialog(LocalDate date);

  void setMonthYearLabel(YearMonth monthYear);

  void setCalendarMonthDates(YearMonth monthYear);

  void setAvailableCalendars(String[] calendars);

  void setCurrentCalendarTz(String tz);

  void setFeatures(CalendarFeatures features);

}
