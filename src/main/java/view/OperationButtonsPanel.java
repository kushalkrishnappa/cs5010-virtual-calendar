package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OperationButtonsPanel extends JPanel {

  private JPanel OperationButtonsPanel;

  private JButton createEventBtn;

  private JButton editEventBtn;

  private JButton copyEventsBtn;

  private JButton showStatusBtn;

  private JButton printEventsBtn;

  public OperationButtonsPanel() {
    setLayout(new BorderLayout());
    initOperationsComponents();
    add(OperationButtonsPanel, BorderLayout.CENTER);
  }

  private void initOperationsComponents() {

    OperationButtonsPanel = new JPanel(new GridLayout(6, 1, 5, 5));
    OperationButtonsPanel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Event Operations"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )
    );

    // create buttons for the calendar event operations
    createButtons();
    // add buttons to the panel
    addButtonsToPanel();

    // Set preferred size for the side panel
    setPreferredSize(new Dimension(150, 400));
  }

  private void createButtons() {
    // buttons for the calendar operations
    createEventBtn = new JButton("Create");
    editEventBtn = new JButton("Edit");
    copyEventsBtn = new JButton("Copy");
    showStatusBtn = new JButton("Busy Status");
    printEventsBtn = new JButton("Print");
  }

  private void addButtonsToPanel() {
    OperationButtonsPanel.add(createEventBtn);
    OperationButtonsPanel.add(editEventBtn);
    OperationButtonsPanel.add(copyEventsBtn);
    OperationButtonsPanel.add(showStatusBtn);
    OperationButtonsPanel.add(printEventsBtn);
  }

}
