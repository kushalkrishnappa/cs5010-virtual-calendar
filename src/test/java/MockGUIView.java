import controller.CalendarFeatures;
import controller.EventData;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import view.IGUIView;

/**
 * Mock for the {@link IGUIView} interface.
 */
public class MockGUIView implements IGUIView {

  boolean showDayViewDialogCalled;
  boolean setMonthYearLabelCalled;
  boolean setCalendarMonthDatesCalled;
  boolean setAvailableCalendarsCalled;
  boolean setCurrentCalendarCalled;
  boolean setFeaturesCalled;
  boolean setCurrentCalendarTzCalled;
  boolean showNewCalendarDialogCalled;
  boolean showEditCalendarDialogCalled;
  boolean showImportCalendarDialogCalled;
  boolean showExportCalendarDialogCalled;
  boolean displayRecurringEventOptionsCalled;
  boolean showCreateEventDialogCalled;
  boolean showEditEventDialogCalled;
  boolean showEventDetailsDialogCalled;
  boolean displayMessageCalled;
  boolean displayErrorCalled;

  String lastSetCurrentCalendar;
  String lastSetCurrentCalendarTz;
  LocalDate lastCreateEventDialogDate;
  EventData lastEditEventDialogData;
  EventData lastEventDetailsDialogData;
  CalendarFeatures lastSetFeatures;
  String lastDisplayErrorMessage;
  LocalDate lastShowDayViewDialogDate;
  List<EventData> lastShowDayViewDialogEvents;
  YearMonth lastSetMonthYearLabelMonthYear;
  YearMonth lastSetCalendarMonthDatesMonthYear;
  String lastDisplayMessage;
  List<String> lastDisplayRecurringEventOptions;

  /**
   * Instantiates a new MockGUIView.
   */
  public MockGUIView() {
    reset();
  }

  /**
   * Resets all the fields to initial state.
   */
  public void reset() {
    showDayViewDialogCalled = false;
    setMonthYearLabelCalled = false;
    setCalendarMonthDatesCalled = false;
    setAvailableCalendarsCalled = false;
    setCurrentCalendarCalled = false;
    setFeaturesCalled = false;
    setCurrentCalendarTzCalled = false;
    showNewCalendarDialogCalled = false;
    showEditCalendarDialogCalled = false;
    showImportCalendarDialogCalled = false;
    showExportCalendarDialogCalled = false;
    displayRecurringEventOptionsCalled = false;
    showCreateEventDialogCalled = false;
    showEditEventDialogCalled = false;
    showEventDetailsDialogCalled = false;
    displayMessageCalled = false;
    displayErrorCalled = false;

    lastSetCurrentCalendar = null;
    lastSetCurrentCalendarTz = null;
    lastCreateEventDialogDate = null;
    lastEditEventDialogData = null;
    lastEventDetailsDialogData = null;
    lastSetFeatures = null;
    lastDisplayErrorMessage = null;
    lastShowDayViewDialogDate = null;
    lastShowDayViewDialogEvents = null;
    lastSetMonthYearLabelMonthYear = null;
    lastSetCalendarMonthDatesMonthYear = null;
    lastDisplayMessage = null;
    lastDisplayRecurringEventOptions = null;
  }

  @Override
  public void showDayViewDialog(LocalDate date, List<EventData> events) {
    showDayViewDialogCalled = true;
    lastShowDayViewDialogDate = date;
    lastShowDayViewDialogEvents = events;
  }

  @Override
  public void setMonthYearLabel(YearMonth monthYear) {
    setMonthYearLabelCalled = true;
    lastSetMonthYearLabelMonthYear = monthYear;
  }

  @Override
  public void setCalendarMonthDates(YearMonth monthYear) {
    setCalendarMonthDatesCalled = true;
    lastSetCalendarMonthDatesMonthYear = monthYear;
  }

  @Override
  public void setAvailableCalendars(String[] calendars) {
    setAvailableCalendarsCalled = true;
  }

  @Override
  public void setCurrentCalendarTz(String tz) {
    setCurrentCalendarTzCalled = true;
    lastSetCurrentCalendarTz = tz;
  }

  @Override
  public void setFeatures(CalendarFeatures features) {
    setFeaturesCalled = true;
    lastSetFeatures = features;
  }

  @Override
  public void setCurrentCalendar(String calendarName) {
    setCurrentCalendarCalled = true;
    lastSetCurrentCalendar = calendarName;
  }

  @Override
  public void showNewCalendarDialog() {
    showNewCalendarDialogCalled = true;
  }

  @Override
  public void showEditCalendarDialog() {
    showEditCalendarDialogCalled = true;
  }

  @Override
  public void showImportCalendarDialog() {
    showImportCalendarDialogCalled = true;
  }

  @Override
  public void showExportCalendarDialog() {
    showExportCalendarDialogCalled = true;
  }

  @Override
  public void displayRecurringEventOptions(String[] options) {
    displayRecurringEventOptionsCalled = true;
    lastDisplayRecurringEventOptions = Arrays.asList(options);
  }

  @Override
  public void showCreateEventDialog(LocalDate localDate) {
    showCreateEventDialogCalled = true;
    lastCreateEventDialogDate = localDate;
  }

  @Override
  public void showEditEventDialog(EventData eventData) {
    showEditEventDialogCalled = true;
    lastEditEventDialogData = eventData;
  }

  @Override
  public void showEventDetailsDialog(EventData eventData) {
    showEventDetailsDialogCalled = true;
    lastEventDetailsDialogData = eventData;
  }

  @Override
  public void displayMessage(String output) {
    displayMessageCalled = true;
    lastDisplayMessage = output;
  }

  @Override
  public void displayError(String error) {
    displayErrorCalled = true;
    lastDisplayErrorMessage = error;
  }

  @Override
  public Readable getInputStream() {
    throw new UnsupportedOperationException("Not supported in GUI mode.");
  }
}
