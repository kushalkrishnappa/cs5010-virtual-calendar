package view;

import controller.CalendarFeatures;
import controller.CalendarWeekDays;
import controller.EventData;
import controller.RecurrenceData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EventDialog extends JDialog {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private JTextField subjectField;
  private JTextField locationField;
  private JTextArea descriptionArea;
  private JCheckBox isAllDayCheckBox;
  private JCheckBox isPublicCheckBox;
  private JCheckBox isRecurringCheckBox;

  private JTextField startDateField;
  private JTextField startTimeField;
  private JTextField endDateField;
  private JTextField endTimeField;

  private JPanel recurrencePanel;
  private JCheckBox mondayCheckBox;
  private JCheckBox tuesdayCheckBox;
  private JCheckBox wednesdayCheckBox;
  private JCheckBox thursdayCheckBox;
  private JCheckBox fridayCheckBox;
  private JCheckBox saturdayCheckBox;
  private JCheckBox sundayCheckBox;

  private JRadioButton occurrencesRadioButton;
  private JRadioButton untilDateRadioButton;
  private JTextField occurrencesField;
  private JTextField untilDateField;

  private JButton saveButton;
  private JButton cancelButton;

  private EventData originalEvent;
  private boolean isEditMode;
  private CalendarFeatures calendarFeatures;
  private LocalDate initialDate;

  public EventDialog(JDialog owner, CalendarFeatures calendarFeatures, LocalDate initialDate) {
    super(owner, "Create New Event", true);
    this.initialDate = initialDate;
    this.isEditMode = false;
    this.calendarFeatures = calendarFeatures;
    this.setLayout(new BorderLayout());
    initComponents();
    populateInitialValues();
    layoutComponents();

    setLocationRelativeTo(owner);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  private void populateInitialValues() {
    // Set default values for a new event
    LocalDate today = initialDate != null ? initialDate : LocalDate.now();
    LocalTime now = LocalTime.now().withSecond(0).withNano(0);
    LocalTime oneHourLater = now.plusHours(1);

    startDateField.setText(today.format(DATE_FORMATTER));
    startTimeField.setText(now.format(TIME_FORMATTER));
    endDateField.setText(today.format(DATE_FORMATTER));
    endTimeField.setText(oneHourLater.format(TIME_FORMATTER));

    // Set initial states
    isPublicCheckBox.setSelected(false);
    isAllDayCheckBox.setSelected(false);
    isRecurringCheckBox.setSelected(false);
    recurrencePanel.setVisible(false);

    occurrencesRadioButton.setSelected(true);
    occurrencesField.setText("5");
    untilDateField.setText(today.plusMonths(1).format(DATE_FORMATTER));
    untilDateField.setEnabled(false);
  }


  private void initComponents() {
    createEventFields();
    createButtons();
  }

  private void createButtons() {
    // Initialize buttons
    saveButton = new JButton(isEditMode ? "Update" : "Create");
    cancelButton = new JButton("Cancel");

    saveButton.addActionListener(e -> saveEvent());
    cancelButton.addActionListener(e -> dispose());

  }


  private void createEventFields() {
    // Initialize basic fields
    subjectField = new JTextField(30);
    locationField = new JTextField(30);
    descriptionArea = new JTextArea(5, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);

    // Initialize date and time fields
    startDateField = new JTextField(10);
    startTimeField = new JTextField(8);
    endDateField = new JTextField(10);
    endTimeField = new JTextField(8);

    isAllDayCheckBox = new JCheckBox("All Day");
    isPublicCheckBox = new JCheckBox("Public");
    isRecurringCheckBox = new JCheckBox("Recurring");

    createEventRecurrenceFields();
    startDateField.getDocument()
        .addDocumentListener(new DateFieldDocumentListener(startDateField, DATE_FORMATTER, false));
    endDateField.getDocument()
        .addDocumentListener(new DateFieldDocumentListener(endDateField, DATE_FORMATTER, false));
    startTimeField.getDocument()
        .addDocumentListener(new DateFieldDocumentListener(startTimeField, TIME_FORMATTER, true));
    endTimeField.getDocument()
        .addDocumentListener(new DateFieldDocumentListener(endTimeField, TIME_FORMATTER, true));
    isAllDayCheckBox.addActionListener(e -> handleAllDayCheckBox());


  }

  private void createEventRecurrenceFields() {
    // Initialize recurrence components

    recurrencePanel = new JPanel();
    mondayCheckBox = new JCheckBox("Monday (M)");
    tuesdayCheckBox = new JCheckBox("Tuesday (T)");
    wednesdayCheckBox = new JCheckBox("Wednesday (W)");
    thursdayCheckBox = new JCheckBox("Thursday (R)");
    fridayCheckBox = new JCheckBox("Friday (F)");
    saturdayCheckBox = new JCheckBox("Saturday (S)");
    sundayCheckBox = new JCheckBox("Sunday (U)");

    occurrencesRadioButton = new JRadioButton("Repeat for");
    untilDateRadioButton = new JRadioButton("Repeat until");
    ButtonGroup recurrenceTypeGroup = new ButtonGroup();
    recurrenceTypeGroup.add(occurrencesRadioButton);
    recurrenceTypeGroup.add(untilDateRadioButton);

    occurrencesField = new JTextField(5);
    untilDateField = new JTextField(10);

    untilDateField.getDocument()
        .addDocumentListener(new DateFieldDocumentListener(untilDateField, DATE_FORMATTER, false));
    isRecurringCheckBox.addActionListener(e -> handleRecurringCheckBox());
    occurrencesRadioButton.addActionListener(e -> occurrencesField.setEnabled(true));
    untilDateRadioButton.addActionListener(e -> untilDateField.setEnabled(true));


  }

  private void handleAllDayCheckBox() {
    boolean isAllDay = isAllDayCheckBox.isSelected();
    startTimeField.setEnabled(!isAllDay);
    endTimeField.setEnabled(!isAllDay);
    endDateField.setEnabled(!isAllDay);

    if (isAllDay) {
      startTimeField.setText("00:00");
      endTimeField.setText("00:00");
      endDateField.setText(startDateField.getText());
    }
  }

  private void handleRecurringCheckBox() {
    recurrencePanel.setVisible(isRecurringCheckBox.isSelected());
    pack();
  }

  private void layoutComponents() {
    setLayout(new BorderLayout());

    // main panel with GridBagLayout for form
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // basic event details
    gbc.gridx = 0;
    gbc.gridy = 0;
    mainPanel.add(new JLabel("Subject:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    mainPanel.add(subjectField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    mainPanel.add(new JLabel("Location:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    mainPanel.add(locationField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    mainPanel.add(new JLabel("Description:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.gridheight = 2;
    mainPanel.add(new JScrollPane(descriptionArea), gbc);

    // date and time section
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    mainPanel.add(new JLabel("Start Date:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 4;
    mainPanel.add(startDateField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 4;
    mainPanel.add(new JLabel("(YYYY-MM-DD)"), gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    mainPanel.add(new JLabel("Start Time:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 5;
    mainPanel.add(startTimeField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 5;
    mainPanel.add(new JLabel("(HH:MM)"), gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    mainPanel.add(new JLabel("End Date:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 6;
    mainPanel.add(endDateField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 6;
    mainPanel.add(new JLabel("(YYYY-MM-DD)"), gbc);

    gbc.gridx = 0;
    gbc.gridy = 7;
    mainPanel.add(new JLabel("End Time:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 7;
    mainPanel.add(endTimeField, gbc);

    gbc.gridx = 2;
    gbc.gridy = 7;
    mainPanel.add(new JLabel("(HH:MM)"), gbc);

    // checkboxes for event properties
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 3;
    mainPanel.add(isAllDayCheckBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 9;
    mainPanel.add(isPublicCheckBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 10;
    mainPanel.add(isRecurringCheckBox, gbc);

    // recurrence panel
    recurrencePanel.setLayout(new GridBagLayout());
    recurrencePanel.setBorder(BorderFactory.createTitledBorder("Recurrence Details"));

    GridBagConstraints recGbc = new GridBagConstraints();
    recGbc.insets = new Insets(3, 3, 3, 3);
    recGbc.anchor = GridBagConstraints.WEST;

    // days of week
    recGbc.gridx = 0;
    recGbc.gridy = 0;
    recGbc.gridwidth = 3;
    recurrencePanel.add(new JLabel("Repeat on:"), recGbc);

    recGbc.gridx = 0;
    recGbc.gridy = 1;
    recGbc.gridwidth = 1;
    recurrencePanel.add(mondayCheckBox, recGbc);

    recGbc.gridx = 1;
    recurrencePanel.add(tuesdayCheckBox, recGbc);

    recGbc.gridx = 2;
    recurrencePanel.add(wednesdayCheckBox, recGbc);

    recGbc.gridx = 0;
    recGbc.gridy = 2;
    recurrencePanel.add(thursdayCheckBox, recGbc);

    recGbc.gridx = 1;
    recurrencePanel.add(fridayCheckBox, recGbc);

    recGbc.gridx = 2;
    recurrencePanel.add(saturdayCheckBox, recGbc);

    recGbc.gridx = 0;
    recGbc.gridy = 3;
    recurrencePanel.add(sundayCheckBox, recGbc);

    // Occurrences or until date
    recGbc.gridx = 0;
    recGbc.gridy = 4;
    recurrencePanel.add(occurrencesRadioButton, recGbc);

    recGbc.gridx = 1;
    recGbc.gridy = 4;
    recurrencePanel.add(occurrencesField, recGbc);

    recGbc.gridx = 2;
    recGbc.gridy = 4;
    recurrencePanel.add(new JLabel("times"), recGbc);

    recGbc.gridx = 0;
    recGbc.gridy = 5;
    recurrencePanel.add(untilDateRadioButton, recGbc);

    recGbc.gridx = 1;
    recGbc.gridy = 5;
    recurrencePanel.add(untilDateField, recGbc);

    recGbc.gridx = 2;
    recGbc.gridy = 5;
    recurrencePanel.add(new JLabel("(YYYY-MM-DD)"), recGbc);

    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    mainPanel.add(recurrencePanel, gbc);

    // buttons panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    // add the main parts to the dialog
    add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    pack();
  }

  private static class DateFieldDocumentListener implements DocumentListener {

    private final JTextField dateField;
    private final DateTimeFormatter formatter;
    private final boolean isTime;

    public DateFieldDocumentListener(JTextField dateField, DateTimeFormatter formatter,
        boolean isTime) {
      this.dateField = dateField;
      this.formatter = formatter;
      this.isTime = isTime;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      processTextChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      processTextChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      processTextChange();
    }

    private void processTextChange() {
      try {
        if (isTime) {
          LocalTime.parse(dateField.getText(), formatter);
        } else {
          LocalDate.parse(dateField.getText(), formatter);
        }
        dateField.setBackground(Color.WHITE);
      } catch (DateTimeParseException exception) {
        dateField.setBackground(new Color(250, 200, 200));
      }
    }
  }

  private void saveEvent() {
    LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;
    try {
      LocalDate startDate = LocalDate.parse(startDateField.getText(), DATE_FORMATTER);
      LocalDate endDate = LocalDate.parse(endDateField.getText(), DATE_FORMATTER);
      LocalTime startTime = LocalTime.parse(startTimeField.getText(), TIME_FORMATTER);
      LocalTime endTime = LocalTime.parse(endTimeField.getText(), TIME_FORMATTER);

      startDateTime = LocalDateTime.of(startDate, startTime);
      endDateTime = LocalDateTime.of(endDate, endTime);
    } catch (DateTimeParseException exception) {
      //pass
      return;
    }

    RecurrenceData recurrenceData = null;
    if (isRecurringCheckBox.isSelected()) {
      // get selected days
      Set<CalendarWeekDays> repeatDays = new HashSet<>();
      if (mondayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.M);
      }
      if (tuesdayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.T);
      }
      if (wednesdayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.W);
      }
      if (thursdayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.R);
      }
      if (fridayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.F);
      }
      if (saturdayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.S);
      }
      if (sundayCheckBox.isSelected()) {
        repeatDays.add(CalendarWeekDays.U);
      }
      Integer occurrences = null;
      LocalDate untilDate = null;

      if (occurrencesRadioButton.isSelected()) {
        occurrences = Integer.parseInt(occurrencesField.getText().trim());
        recurrenceData = new RecurrenceData(occurrences, repeatDays, null);
      } else if (untilDateRadioButton.isSelected()) {
        try {
          untilDate = LocalDate.parse(untilDateField.getText(), DATE_FORMATTER);
          recurrenceData = new RecurrenceData(null, repeatDays, untilDate.atStartOfDay());
        } catch (DateTimeParseException exception) {
          //pass
          return;
        }
      }
    }

    EventData eventData = EventData.getBuilder()
        .setSubject(subjectField.getText().trim())
        .setStartTime(startDateTime)
        .setEndTime(endDateTime)
        .setDescription(descriptionArea.getText().trim())
        .setLocation(locationField.getText().trim())
        .setIsPublic(isPublicCheckBox.isSelected())
        .setIsAllDay(isAllDayCheckBox.isSelected())
        .setIsRecurring(isRecurringCheckBox.isSelected())
        .setRecurringDetails(recurrenceData)
        .build();

    if (isEditMode) {
      // TODO:
    } else {
      calendarFeatures.createEvent(eventData);
    }

    dispose();
  }
}
