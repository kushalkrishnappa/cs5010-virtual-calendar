package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.ZoneId;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class represents a dialog for creating or editing a calendar. The edit mode allows the user
 * to modify an existing calendar's name and timezone which will be populated in the dialog fields.
 */
public class NewCalendarDialog extends JDialog {

  private JTextField calendarNameField;

  private JComboBox<String> timezoneComboBox;

  private boolean confirmed = false;

  private final boolean editMode;

  private String editCalendarName;

  private String editTimezone;

  private static final String[] TIMEZONES = ZoneId.getAvailableZoneIds().stream().sorted()
      .toArray(String[]::new);

  /**
   * This constructor initializes the NewCalendarDialog with the specified parent frame, calendar
   * name, and timezone. If the calendar name is null, it indicates that a new calendar is being
   * created.
   *
   * @param parent       the parent frame of the dialog
   * @param calendarName the name of the calendar to be edited, or null for a new calendar
   * @param timezone     the timezone of the calendar to be edited, or null for a new calendar
   */
  public NewCalendarDialog(Frame parent, String calendarName, String timezone) {
    super(parent, calendarName == null ? "New Calendar" : "Edit Calendar", true);

    this.editMode = calendarName != null;
    if (editMode) {
      this.editCalendarName = calendarName;
      this.editTimezone = timezone;
    }

    // set up the 'New Calendar' dialog box
    setLayout(new BorderLayout());
    setSize(400, 200);
    setLocationRelativeTo(parent);

    // init components
    initComponents();
  }

  private void initComponents() {
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // create 'Calendar Name' text field
    createCalendarNameTextField(formPanel);

    // create 'Timezone' dropdown
    createTimezoneComboBox(formPanel);

    // create 'Confirm' and 'Cancel' buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    createConfirmAndCancelButtons(buttonPanel);

    // add form and button panels to the dialog
    add(formPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    // handle dialog close
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        confirmed = false;
      }
    });
  }

  private void createCalendarNameTextField(JPanel formPanel) {
    formPanel.add(new JLabel("Calendar Name:"));
    calendarNameField = new JTextField(20);
    if (editMode) {
      calendarNameField.setText(editCalendarName);
    }
    formPanel.add(calendarNameField);
  }

  private void createTimezoneComboBox(JPanel formPanel) {
    formPanel.add(new JLabel("Timezone:"));
    timezoneComboBox = new JComboBox<>(TIMEZONES);

    // Add tooltip support for timezone combo box
    timezoneComboBox.setRenderer(new DropdownToolTipRenderer());

    // Set fixed width for timezone combo box
    timezoneComboBox.setPreferredSize(
        new Dimension(200, timezoneComboBox.getPreferredSize().height));
    timezoneComboBox.setMaximumSize(new Dimension(200, timezoneComboBox.getPreferredSize().height));

    timezoneComboBox.setSelectedItem(
        editMode
            ? editTimezone
            : ZoneId.systemDefault().getId()); // set default timezone
    formPanel.add(timezoneComboBox);
  }

  private void createConfirmAndCancelButtons(JPanel buttonPanel) {
    // create buttons
    JButton confirmButton = new JButton("Confirm");
    JButton cancelButton = new JButton("Cancel");
    // add buttons to the panel
    buttonPanel.add(confirmButton);
    buttonPanel.add(cancelButton);
    // add action listeners
    confirmButton.addActionListener(e -> {
      if (validateInput()) {
        confirmed = true;
        setVisible(false);
      }
    });
    cancelButton.addActionListener(e -> {
      confirmed = false;
      setVisible(false);
    });
  }

  private boolean validateInput() {
    String calendarName = getCalendarName();
    if (calendarName == null || calendarName.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Please enter a calendar name.",
          "Input Error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Returns whether the dialog was confirmed.
   *
   * @return true if confirmed, false if canceled
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Gets the calendar name entered by the user.
   *
   * @return the calendar name
   */
  public String getCalendarName() {
    return calendarNameField.getText().trim();
  }

  /**
   * Gets the selected timezone.
   *
   * @return the selected timezone
   */
  public String getSelectedTimezone() {
    return (String) timezoneComboBox.getSelectedItem();
  }

}
