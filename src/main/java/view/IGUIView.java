package view;

import controller.CalendarFeatures;
import java.time.LocalDate;

public interface IGUIView extends IView {

  void showDayViewDialog(LocalDate date);

  void setAvailableCalendars(String[] calendars);

  void setFeatures(CalendarFeatures features);

}
