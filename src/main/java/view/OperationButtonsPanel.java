package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This is the footer panel of the calendar GUI. It contains the buttons for creating a new event.
 * Any other operations can be added to this panel in the future.
 */
public class OperationButtonsPanel extends JPanel {

  private JPanel operationButtonsPanel;

  private JButton createEventBtn;

  private CalendarFeatures calendarFeatures;

  private String currentTimezone;

  /**
   * This constructor initializes the OperationButtonsPanel and adds the new event button to it.
   */
  public OperationButtonsPanel() {
    setLayout(new BorderLayout());
    initOperationsComponents();
    add(operationButtonsPanel);
  }

  private void initOperationsComponents() {

    operationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    operationButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

    // create buttons for the calendar event operations
    createNewEventButton();

    operationButtonsPanel.add(createEventBtn);
  }

  private void createNewEventButton() {
    createEventBtn = new JButton("New Event");

    createEventBtn.addActionListener(e -> {
      calendarFeatures.requestEventCreation(
          ZonedDateTime.now(ZoneId.of(currentTimezone)).toLocalDate());
    });
  }

  /**
   * This method sets the calendar features for the current calendar.
   *
   * @param calendarFeatures the calendar features to be set
   */
  public void setFeatures(CalendarFeatures calendarFeatures) {
    this.calendarFeatures = calendarFeatures;
  }

  /**
   * This method sets the timezone for the current calendar. It is needed to create the new event in
   * the correct timezone.
   *
   * @param tz the timezone to be set
   */
  public void setCurrentTimezone(String tz) {
    this.currentTimezone = tz;
  }
}
