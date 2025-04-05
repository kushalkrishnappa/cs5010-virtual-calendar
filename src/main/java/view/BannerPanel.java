package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
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
    calendarSelectorDropdown = new JComboBox<>();
    buttonsPanel.add(calendarSelectorDropdown);

    timeZonePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    timeZonePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    timeZoneBtn = new JButton("Timezone: not set");
    timeZoneBtn.setEnabled(false);
    timeZonePanel.add(timeZoneBtn);
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

    // add dummy action listeners
    createCalendarBtn.addActionListener(e -> System.out.println("Create Calendar clicked"));
    editCalendarBtn.addActionListener(e -> System.out.println("Edit Calendar clicked"));
    calendarSelectorDropdown.addActionListener(
        e -> System.out.println("Calendar selected: " + calendarSelectorDropdown.getSelectedItem())
    );
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

    createCalendarBtn.addActionListener(
        e -> {
          NewCalendarDialog dialog = new NewCalendarDialog(
              (Frame) SwingUtilities.getWindowAncestor(this),
              null,
              null
          );
          dialog.setVisible(true);

          if (dialog.isConfirmed()) {
            String calendarName = dialog.getCalendarName();
            String timezone = dialog.getSelectedTimezone();
            calendarFeatures.createCalendar(calendarName, timezone);
          }
        }
    );
    editCalendarBtn.addActionListener(
        e -> {
          String selectedCalendar = (String) calendarSelectorDropdown.getSelectedItem();
          String timezone = timeZoneBtn.getText().substring(10);

          if (selectedCalendar != null) {
            NewCalendarDialog dialog = new NewCalendarDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selectedCalendar,
                timezone
            );
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
              String newCalendarName = dialog.getCalendarName();
              String newTimezone = dialog.getSelectedTimezone();
              calendarFeatures.editCalendar(selectedCalendar, newCalendarName, newTimezone);
            }
          }
        }
    );
    exportCalendarBtn.addActionListener(e -> getExportCalendarClicked());
    importCalendarBtn.addActionListener(e -> getImportCalendarClicked());
    calendarSelectorDropdown.addActionListener(
        e -> calendarFeatures.switchCalendar((String) calendarSelectorDropdown.getSelectedItem())
    );
  }

  private void getExportCalendarClicked() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(CURRENT_DIR);
    int response = fileChooser.showSaveDialog(this.getParent());
    if (response == JFileChooser.APPROVE_OPTION) {
      calendarFeatures.exportCalendar(fileChooser.getSelectedFile().getAbsolutePath());
      CURRENT_DIR = fileChooser.getSelectedFile().getParentFile();
    }
  }

  private void getImportCalendarClicked() {
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
