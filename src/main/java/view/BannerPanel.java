package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class BannerPanel extends JPanel {

  JPanel buttonsPanel;

  private String[] calendars;

  private JComboBox<String> calendarSelectorDropdown;

  private JButton createCalendarBtn;

  private JButton editCalendarBtn;

  private JButton exportCalendarBtn;

  private JButton importCalendarBtn;

  private CalendarFeatures calendarFeatures;

  public BannerPanel() {
    setLayout(new BorderLayout());
    initBannerComponents();
  }

  private void initBannerComponents() {
    // button panel
    buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    // calendar selector dropdown
    buttonsPanel.add(new JLabel("Calendar:"));
    calendarSelectorDropdown = new JComboBox<>();
    buttonsPanel.add(calendarSelectorDropdown);

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
    exportCalendarBtn.addActionListener(e -> System.out.println("Export Calendar clicked"));
    importCalendarBtn.addActionListener(e -> System.out.println("Import Calendar clicked"));
    calendarSelectorDropdown.addActionListener(
        e -> System.out.println("Calendar selected: " + calendarSelectorDropdown.getSelectedItem())
    );
  }

  private void createButtons() {
    // buttons for the calendar operations
    createCalendarBtn = new JButton("Create Calendar");
    editCalendarBtn = new JButton("Edit");
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

    createCalendarBtn.addActionListener(e -> calendarFeatures.createCalendar());
    editCalendarBtn.addActionListener(
        e -> calendarFeatures.editCalendar((String) calendarSelectorDropdown.getSelectedItem())
    );
    exportCalendarBtn.addActionListener(e -> calendarFeatures.exportCalendar());
    importCalendarBtn.addActionListener(e -> calendarFeatures.importCalendar());
    calendarSelectorDropdown.addActionListener(
        e -> calendarFeatures.switchCalendar((String) calendarSelectorDropdown.getSelectedItem())
    );
  }

  public void setCalendarSelector(String[] calendars) {
    this.calendars = calendars;
    refreshCalendarSelector();
  }

  private void refreshCalendarSelector() {
    calendarSelectorDropdown.removeAllItems();
    for (String calendar : calendars) {
      calendarSelectorDropdown.addItem(calendar);
    }
  }

}
