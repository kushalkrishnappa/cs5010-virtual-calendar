package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

/**
 * This class represents the banner panel of the calendar GUI. It contains the calendar selector
 * dropdown and buttons for creating, editing, exporting, and importing calendars. It also displays
 * the current timezone.
 */
public class BannerPanel extends JPanel {

  private JPanel buttonsPanel;

  private String[] calendars;

  private JComboBox<String> calendarSelectorDropdown;

  private JButton createCalendarBtn;

  private JButton editCalendarBtn;

  private JButton exportCalendarBtn;

  private JButton importCalendarBtn;

  private JButton timeZoneBtn;

  private JPanel timeZonePanel;

  private CalendarFeatures calendarFeatures;

  private static File CURRENT_DIR;

  /**
   * This constructor initializes the BannerPanel and sets up the layout and components. It
   * initializes the calendar selector dropdown and buttons for calendar operations.
   */
  public BannerPanel() {
    setLayout(new BorderLayout());
    initBannerComponents();
    CURRENT_DIR = new File(System.getProperty("user.home"));
  }

  private void initBannerComponents() {
    // button panel
    buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    // calendar selector dropdown
    buttonsPanel.add(new JLabel("Calendar:"));
    createFixedWidthComboBox();
    buttonsPanel.add(calendarSelectorDropdown);

    createTimezoneButton();
    add(timeZonePanel, BorderLayout.EAST);

    // add buttons to the panel
    createButtons();
    // add buttons to the panel
    addButtonsToPanel();

    // add separator lines
    JSeparator topSeparator = new JSeparator();
    JSeparator bottomSeparator = new JSeparator();

    // add components to the BannerPanel
    add(topSeparator, BorderLayout.NORTH);
    add(buttonsPanel, BorderLayout.CENTER);
    add(bottomSeparator, BorderLayout.SOUTH);
  }

  private void createFixedWidthComboBox() {
    JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setRenderer(new DropdownToolTipRenderer());
    comboBox.setPreferredSize(new Dimension(150, comboBox.getPreferredSize().height));
    comboBox.setMaximumSize(new Dimension(150, comboBox.getPreferredSize().height));
    calendarSelectorDropdown = comboBox;
  }

  private void createTimezoneButton() {
    timeZonePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    timeZonePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    timeZoneBtn = new JButton("Timezone: not set");
    timeZoneBtn.setEnabled(false);
    timeZonePanel.add(timeZoneBtn);
  }

  private void createButtons() {
    // buttons for the calendar operations
    createCalendarBtn = new JButton("New Calendar");
    editCalendarBtn = new JButton("Edit Calendar");
    exportCalendarBtn = new JButton("Export");
    importCalendarBtn = new JButton("Import");
  }

  private void addButtonsToPanel() {
    // add the calendar operation buttons to the panel
    buttonsPanel.add(createCalendarBtn);
    buttonsPanel.add(editCalendarBtn);
    buttonsPanel.add(exportCalendarBtn);
    buttonsPanel.add(importCalendarBtn);
  }

  /**
   * This method sets the calendar features for the current calendar and adds the action listeners
   * to the buttons mapping to which calendar feature they are mapped with.
   *
   * @param features the calendar features to be set
   */
  public void setFeatures(CalendarFeatures features) {
    calendarFeatures = features;
    createCalendarBtn.addActionListener(e -> calendarFeatures.requestCalendarCreation());
    editCalendarBtn.addActionListener(e -> calendarFeatures.requestCalendarEdit());
    exportCalendarBtn.addActionListener(e -> calendarFeatures.requestCalendarExport());
    importCalendarBtn.addActionListener(e -> calendarFeatures.requestCalendarImport());
    calendarSelectorDropdown.addActionListener(
        e -> calendarFeatures.switchCalendar((String) calendarSelectorDropdown.getSelectedItem())
    );
  }

  /**
   * This method shows the calendar dialog for creating or editing a calendar. It takes a boolean
   * parameter to determine if the dialog is in edit mode. If in edit mode, it pre-fills the dialog
   * with the selected calendar name and timezone. If in create mode (i.e, isEditMode=False), it
   * shows an empty dialog.
   *
   * @param isEditMode true if dialog is in edit mode, false otherwise
   */
  public void showCalendarDialog(Boolean isEditMode) {
    String selectedCalendar = null;
    String selectedTimezone = null;
    if (isEditMode) {
      selectedCalendar = (String) calendarSelectorDropdown.getSelectedItem();
      selectedTimezone = timeZoneBtn.getText().substring(10);
    }

    NewCalendarDialog dialog = new NewCalendarDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        selectedCalendar,
        selectedTimezone
    );
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      String newCalendarName = dialog.getCalendarName();
      String newtTimezone = dialog.getSelectedTimezone();
      if (isEditMode) {
        calendarFeatures.editCalendar(selectedCalendar, newCalendarName, newtTimezone);
      } else {
        calendarFeatures.createCalendar(newCalendarName, newtTimezone);
      }
    }
  }

  /**
   * This method shows the export calendar dialog to the user. It allows the user to select a
   * directory to save the exported calendar file. The current directory is set to the last used
   * directory.
   */
  public void showExportCalendarDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(CURRENT_DIR);
    int response = fileChooser.showSaveDialog(this.getParent());
    if (response == JFileChooser.APPROVE_OPTION) {
      calendarFeatures.exportCalendar(fileChooser.getSelectedFile().getAbsolutePath());
      CURRENT_DIR = fileChooser.getSelectedFile().getParentFile();
    }
  }

  /**
   * This method shows the import calendar dialog to the user. It allows the user to select a
   * directory to import the calendar file from. The current directory is set to the last used
   * directory.
   */
  public void showImportCalendarDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(CURRENT_DIR);
    int response = fileChooser.showOpenDialog(this.getParent());
    if (response == JFileChooser.APPROVE_OPTION) {
      calendarFeatures.importCalendarFromFile(fileChooser.getSelectedFile().getAbsolutePath());
      CURRENT_DIR = fileChooser.getSelectedFile().getParentFile();
    }
  }

  /**
   * This method sets the available calendars in the calendar selector dropdown. It removes all
   * existing items from the dropdown and adds the new items.
   *
   * @param calendars the list of available calendars
   */
  public void setCalendarSelector(String[] calendars) {
    this.calendars = calendars;
    refreshCalendarSelector();
  }

  private void refreshCalendarSelector() {
    ActionListener[] actionListeners = calendarSelectorDropdown.getActionListeners();
    for (ActionListener actionListener : actionListeners) {
      calendarSelectorDropdown.removeActionListener(actionListener);
    }
    calendarSelectorDropdown.removeAllItems();
    for (String calendar : calendars) {
      calendarSelectorDropdown.addItem(calendar);
    }
    for (ActionListener actionListener : actionListeners) {
      calendarSelectorDropdown.addActionListener(actionListener);
    }
  }

  /**
   * This method sets the current timezone for the calendar. It updates the timezone button text
   * to display the selected timezone.
   *
   * @param timezone the timezone to be set
   */
  public void setCurrentTimezone(String timezone) {
    timeZoneBtn.setText("Timezone: " + timezone);
  }

  /**
   * This method sets the current calendar to be displayed in the calendar view. It updates the
   * calendar selector dropdown to show the selected calendar.
   *
   * @param calendarName the name of the calendar to be set as current
   */
  public void setCurrentCalendar(String calendarName) {
    calendarSelectorDropdown.setSelectedItem(calendarName);
  }
}
