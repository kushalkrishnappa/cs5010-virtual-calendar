package view;

import controller.CalendarFeatures;
import controller.EventData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class represents the GUI view of the Calendar application. It implements the IGUIView
 * interface and provides methods to display the calendar GUI components and handle user
 * interactions. It extends JFrame to create a window for the application.
 */
public class SwingView extends JFrame implements IGUIView {

  private BannerPanel bannerPanel;

  private OperationButtonsPanel operationButtonsPanel;

  private DatesPanel datesPanel;

  private CalendarFeatures calendarFeatures;

  private DayDialog dayDialog;

  private EventDialog eventDialog;

  /**
   * It initializes the main JFrame which is the initial component of calendar GUI and sets up the
   * components of the calendar GUI application. It mainly initializes the banner panel, the
   * operation buttons panel (footer) and the dates panel (data section).
   */
  public SwingView() {
    // setup the main JFrame
    setTitle("Calendar Application");
    setSize(1000, 1000);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // init the components
    initComponents();
    layoutComponents();

    pack();
    setMaximumSize(new Dimension(1000, getHeight()));
    setResizable(false);
    setLocationRelativeTo(null);

    // make the UI visible
    setVisible(true);
  }

  private void initComponents() {
    bannerPanel = new BannerPanel();
    operationButtonsPanel = new OperationButtonsPanel();
    datesPanel = new DatesPanel();
  }

  private void layoutComponents() {
    add(bannerPanel, BorderLayout.NORTH);
    add(operationButtonsPanel, BorderLayout.SOUTH);
    add(datesPanel, BorderLayout.CENTER);
  }

  @Override
  public void showDayViewDialog(LocalDate date, List<EventData> events) {
    if (dayDialog != null) {
      dayDialog.dispose();
    }
    if (eventDialog != null) {
      eventDialog.dispose();
    }
    dayDialog = new DayDialog(this, calendarFeatures, date, events);
    dayDialog.setVisible(true);
  }

  @Override
  public void setMonthYearLabel(YearMonth monthYear) {
    datesPanel.updateMonthYearLabel(monthYear);
  }

  @Override
  public void setCalendarMonthDates(YearMonth yearMonth) {
    datesPanel.updateCalendarYearMonthDates(yearMonth);
  }

  @Override
  public void setAvailableCalendars(String[] calendars) {
    bannerPanel.setCalendarSelector(calendars);
  }

  @Override
  public void setCurrentCalendarTz(String tz) {
    bannerPanel.setCurrentTimezone(tz);
    datesPanel.setCurrentTimezone(tz);
    operationButtonsPanel.setCurrentTimezone(tz);
  }

  @Override
  public void setFeatures(CalendarFeatures features) {
    this.calendarFeatures = features;
    bannerPanel.setFeatures(features);
    datesPanel.setFeatures(features);
    operationButtonsPanel.setFeatures(features);
  }

  @Override
  public void setCurrentCalendar(String calendarName) {
    bannerPanel.setCurrentCalendar(calendarName);
  }

  @Override
  public void showNewCalendarDialog() {
    bannerPanel.showCalendarDialog(false);
  }

  @Override
  public void showEditCalendarDialog() {
    bannerPanel.showCalendarDialog(true);
  }

  @Override
  public void showImportCalendarDialog() {
    bannerPanel.showImportCalendarDialog();
  }

  @Override
  public void showExportCalendarDialog() {
    bannerPanel.showExportCalendarDialog();
  }

  @Override
  public void displayRecurringEventOptions(String[] options) {
    String choice = RadioDialogOptions.show(dayDialog, "", options);
    if (choice != null) {
      calendarFeatures.selectedRecurringEventOption(choice);
    }
  }

  @Override
  public void displayMessage(String output) {
    JOptionPane.showMessageDialog(this, output, "", JOptionPane.PLAIN_MESSAGE);
  }

  @Override
  public void displayError(String error) {
    JOptionPane.showMessageDialog(this, error, "", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public Readable getInputStream() {
    throw new UnsupportedOperationException("Not supported in GUI mode.");
  }

  @Override
  public void showCreateEventDialog(LocalDate localDate) {
    if (dayDialog != null && dayDialog.isVisible()) {
      eventDialog = new EventDialog(dayDialog, calendarFeatures, localDate);
    } else {
      eventDialog = new EventDialog(this, calendarFeatures, localDate);
    }

    eventDialog.setVisible(true);
  }

  @Override
  public void showEditEventDialog(EventData eventData) {
    eventDialog = new EventDialog(dayDialog, calendarFeatures, eventData);
    eventDialog.setVisible(true);
  }

  @Override
  public void showEventDetailsDialog(EventData eventData) {
    eventDialog = new EventDialog(dayDialog, calendarFeatures, eventData);
    eventDialog.showNonEditableDialog();
    eventDialog.setVisible(true);
  }
}
