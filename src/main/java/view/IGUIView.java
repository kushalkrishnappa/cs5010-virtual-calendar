package view;

import controller.CalendarFeatures;

public interface IGUIView extends IView {

  void setAvailableCalendars(String[] calendars);

  void setFeatures(CalendarFeatures features);

}
