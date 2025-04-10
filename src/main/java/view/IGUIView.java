package view;

import controller.CalendarFeatures;
import controller.EventData;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * This interface represents the GUI view of the Calendar application. It adds additional methods
 * that are required for GUI functionality by extending the IView interface. It includes methods for
 * loading the UI components and displaying event data.
 */
public interface IGUIView extends IView {

  /**
   * It displays the dialog on mouseclick on the day in the calendar view.
   *
   * @param date   the date of the day clicked
   * @param events the list of events for the clicked date
   */
  void showDayViewDialog(LocalDate date, List<EventData> events);

  /**
   * It sets the month and year label in the calendar view.
   *
   * @param monthYear the YearMonth object representing the month to be displayed
   */
  void setMonthYearLabel(YearMonth monthYear);

  /**
   * It populates the calendar with the dates of the month and displays the dates in the 6 x 6
   * grid.
   *
   * @param monthYear the YearMonth object representing the month to be displayed
   */
  void setCalendarMonthDates(YearMonth monthYear);

  /**
   * It sets the current available calendar names in the calendar selector dropdown.
   *
   * @param calendars the list of available calendars
   */
  void setAvailableCalendars(String[] calendars);

  /**
   * Sets the timezone for the current calendar.
   *
   * @param tz the timezone to be set
   */
  void setCurrentCalendarTz(String tz);

  /**
    * Sets the calendar features for the current calendar.
   *
   * @param features the features of the calendar
   */
  void setFeatures(CalendarFeatures features);

  /**
   * Sets the current calendar to be displayed in the calendar view.
   *
   * @param calendarName the name of the calendar to be set as current
   */
  void setCurrentCalendar(String calendarName);

  /**
   * A dialog to show to the user when the create calendar button is clicked.
   */
  void showNewCalendarDialog();

  /**
   * A dialog to show to the user when the edit calendar button is clicked.
   */
  void showEditCalendarDialog();

  /**
   * A dialog to show to the user when the import calendar button is clicked.
   */
  void showImportCalendarDialog();

  /**
   * A dialog to show to the user when the export calendar button is clicked.
   */
  void showExportCalendarDialog();

  /**
   * A sub dialog to populate within the EventDetails dialog to show the options for recurring
   * events. This dialog is shown when the user selects the "Recurring Event" checkbox in the
   * Event dialog.
   *
   * @param options the options to be displayed in the dialog
   */
  void displayRecurringEventOptions(String[] options);

  /**
   * A dialog to show to the user when the create event button is clicked in the event dialog.
   *
   * @param localDate the date of the event to be created
   */
  void showCreateEventDialog(LocalDate localDate);

  /**
   * A dialog to show to the user when the edit event button is clicked in the event dialog.
   *
   * @param eventData the event data to be displayed in the dialog
   */
  void showEditEventDialog(EventData eventData);

  /**
   * A dialog to show to the user when the user double-clicks on an event in the event dialog on a
   * specific date.
   *
   * @param eventData the event data to be displayed in the dialog
   */
  void showEventDetailsDialog(EventData eventData);
}
