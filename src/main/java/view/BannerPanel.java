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

public class BannerPanel extends JPanel {

  JPanel buttonsPanel;

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

  public void showExportCalendarDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(CURRENT_DIR);
    int response = fileChooser.showSaveDialog(this.getParent());
    if (response == JFileChooser.APPROVE_OPTION) {
      calendarFeatures.exportCalendar(fileChooser.getSelectedFile().getAbsolutePath());
      CURRENT_DIR = fileChooser.getSelectedFile().getParentFile();
    }
  }

  public void showImportCalendarDialog() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(CURRENT_DIR);
    int response = fileChooser.showOpenDialog(this.getParent());
    if (response == JFileChooser.APPROVE_OPTION) {
      calendarFeatures.importCalendarFromFile(fileChooser.getSelectedFile().getAbsolutePath());
      CURRENT_DIR = fileChooser.getSelectedFile().getParentFile();
    }
  }

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

  public void setCurrentTimezone(String timezone) {
    timeZoneBtn.setText("Timezone: " + timezone);
  }

  public void setCurrentCalendar(String calendarName) {
    calendarSelectorDropdown.setSelectedItem(calendarName);
  }
}
