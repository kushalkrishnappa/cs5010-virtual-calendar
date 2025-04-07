package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OperationButtonsPanel extends JPanel {

  private JPanel OperationButtonsPanel;

  private JButton createEventBtn;

  private CalendarFeatures calendarFeatures;

  private String currentTimezone;

  public OperationButtonsPanel() {
    setLayout(new BorderLayout());
    initOperationsComponents();
    add(OperationButtonsPanel);
  }

  private void initOperationsComponents() {

    OperationButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    OperationButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

    // create buttons for the calendar event operations
    createNewEventButton();

    OperationButtonsPanel.add(createEventBtn);
  }

  private void createNewEventButton() {
    createEventBtn = new JButton("New Event");

    createEventBtn.addActionListener(e -> {
      calendarFeatures.requestEventCreation(
          ZonedDateTime.now(ZoneId.of(currentTimezone)).toLocalDate());
    });
  }

  public void setFeatures(CalendarFeatures calendarFeatures) {
    this.calendarFeatures = calendarFeatures;
  }


  public void setCurrentTimezone(String tz) {
    this.currentTimezone = tz;
  }
}
