package controller;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * This interface defines the features and operations that can be performed on a calendar. It
 * includes methods for importing, exporting, creating, and editing calendars, as well as viewing
 * events and performing event operations.
 */
public interface CalendarFeatures {

  /**
   * Imports a calendar from a file. The file should be in a format that is compatible with the
   * calendar application.
   *
   * @param filePath the path of the file to be imported
   */
  void importCalendarFromFile(String filePath);

  // calendar operation features

  /**
   * This feature allows the user to create a new calendar. The user can specify the name and
   * timezone of the new calendar.
   *
   * @param newCalendarName the name of the new calendar to be created
   * @param newTimezone     the timezone of the new calendar
   */
  void createCalendar(String newCalendarName, String newTimezone);

  /**
   * This feature allows the user to edit an existing calendar. The user can specify the new name
   * and timezone of the calendar.
   *
   * @param currentCalendarName the name of the calendar to be edited
   * @param newCalendarName     the new name of the calendar
   * @param newTimezone         the new timezone of the calendar
   */
  void editCalendar(String currentCalendarName, String newCalendarName, String newTimezone);

  /**
   * Exports the current calendar to a file. The user can specify the path where the file should be
   * saved.
   *
   * @param saveFilePath the path of the file to save the exported calendar
   */
  void exportCalendar(String saveFilePath);

  /**
   * The feature allows the user to switch between different calendars. The user can select a
   * calendar from a list of available calendars in the calendar selector dropdown.
   *
   * @param calendarName the name of the calendar to be switched to
   */
  void switchCalendar(String calendarName);

  /**
   * This feature allows the user to see a dialog that takes the name and timezone of the new
   * calendar in order to create a new calendar.
   */
  void requestCalendarCreation();

  /**
   * This feature allows the user to see a dialog that takes the name and timezone of the calendar
   * in order to edit an existing calendar.
   */
  void requestCalendarEdit();

  /**
   * This feature allows the user to see a dialog that prompts to select a directory to store the
   * exported calendar file.
   */
  void requestCalendarExport();

  /**
   * This feature allows the user to see a dialog that prompts to select a directory to import the
   * calendar file from.
   */
  void requestCalendarImport();

  // calendar view features

  /**
   * This feature allows the user to view a specific day in the calendar. The user can select a date
   * from the calendar view, and the application will display the events for that date in a dialog.
   *
   * @param date the date to be viewed
   */
  void viewDay(LocalDate date);

  /**
   * This features allows the user to move to the next month in the calendar view. The user can
   * press the next month button, and the application will display the days of the next month in the
   * calendar view.
   *
   * @param yearMonth the YearMonth object representing the month to be displayed
   */
  void nextMonthYear(YearMonth yearMonth);

  /**
   * This feature allows the user to move to the previous month in the calendar view. The user can
   * press the previous month button, and the application will display the days of the previous
   * month in the calendar view.
   *
   * @param yearMonth the YearMonth object representing the month to be displayed
   */
  void previousMonthYear(YearMonth yearMonth);

  // event operations

  /**
   * This feature allows the user to create a new event in the calendar. The user can specify the
   * details of the event. The event will be added to the calendar and displayed in the day view
   * dialog.
   *
   * @param eventData the event data to be created
   */
  void createEvent(EventData eventData);

  /**
   * This feature allows the user to edit an existing event in the calendar. The user can specify
   * the new details of the event. The event will be updated in the calendar and displayed in the
   * day view dialog.
   *
   * @param existingEventData the existing event data to be edited
   * @param newEventData      the new event data to be created
   */
  void editEvent(EventData existingEventData, EventData newEventData);

  /**
   * This is a callback handler to accept the user's choice of the recurring event options while
   * editing the recurring event. Typical choices are "edit this event", "edit all events", "edit
   * this and following events".
   *
   * @param choice the choice of the user for the recurring event options
   */
  void selectedRecurringEventOption(String choice);

  /**
   * This feature allows the user to see a dialog that prompts to select a directory to create a new
   * event. The local date on which the event is created is passed to the dialog.
   *
   * @param localDate the date of the event to be created
   */
  void requestEventCreation(LocalDate localDate);

  /**
   * This feature allows the user to see a dialog that prompts a event dialog to edit an event.
   *
   * @param eventData the event data to be edited
   */
  void requestEventEdit(EventData eventData);

  /**
   * This feature allows the user to see an event dialog that is displayed and can view the details
   * of an event used while creating the event.
   *
   * @param eventData the event data to be displayed in the dialog
   */
  void requestEventViewDetails(EventData eventData);

  /**
   * The feature allows the user to jump to the current year and month in the calendar timezone.
   */
  void requestThisMonthView();
}
