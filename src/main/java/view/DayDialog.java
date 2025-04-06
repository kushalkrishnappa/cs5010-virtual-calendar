package view;

import controller.CalendarFeatures;
import controller.EventData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class DayDialog extends JDialog {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      "MMMM d, yyyy");

  private LocalDate date;

  private List<EventData> events;

  private JPanel headerPanel;

  private JTable eventsTable;

  private DefaultTableModel tableModel;

  private JButton newEventButton;

  private JButton editButton;

  private CalendarFeatures calendarFeatures;

  public DayDialog(Frame owner, CalendarFeatures calendarFeatures, LocalDate date,
      List<EventData> events) {
    super(owner, date.format(DATE_FORMATTER), true);
    this.date = date;
    this.events = events;
    this.calendarFeatures = calendarFeatures;

    initComponents();
    loadEventsToTable();

    // Set dialog properties
    setSize(600, 500);
    setLocationRelativeTo(owner);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  private void initComponents() {
    setLayout(new BorderLayout());

    // create header panel with date and back button
    createDateAndBackButtonHeader();

    // create table model and table
    createScrollableEventsTable();

    // create buttons for new event and edit
    createButtonsFooter();
  }


  private void createButtonsFooter() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    newEventButton = new JButton("New Event");
    editButton = new JButton("Edit Event");

    // TODO: Add action listeners to these buttons
    newEventButton.addActionListener(e -> {
      EventDialog eventDialog = new EventDialog(this, calendarFeatures, date);
      eventDialog.setVisible(true);
    });
    editButton.addActionListener(e -> {
      System.out.println("Edit Event button clicked");
      runAgainstSelectedEvent(eventData -> {
        EventDialog eventDialog = new EventDialog(this, calendarFeatures, eventData);
        eventDialog.setVisible(true);
        return null;
      });
    });

    buttonPanel.add(newEventButton);
    buttonPanel.add(editButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void createDateAndBackButtonHeader() {
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel dateLabel = new JLabel(date.format(DATE_FORMATTER));
    dateLabel.setFont(new Font("Sans-Serif", Font.BOLD, 16));
    dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JButton backButton = new JButton("Back to Month");
    backButton.addActionListener(e -> dispose());

    headerPanel.add(dateLabel, BorderLayout.WEST);
    headerPanel.add(backButton, BorderLayout.EAST);
    add(headerPanel, BorderLayout.NORTH);
  }

  private void createScrollableEventsTable() {
    this.tableModel = createTableModel();
    eventsTable = new JTable(tableModel);
    eventsTable.setRowHeight(50);
    eventsTable.setShowGrid(true);
    eventsTable.setGridColor(Color.LIGHT_GRAY);
    eventsTable.setFillsViewportHeight(true);

    eventsTable.getColumnModel().getColumn(0); // Time
    eventsTable.getColumnModel().getColumn(1); // Event
    eventsTable.getColumnModel().getColumn(2); // Location
    eventsTable.getColumnModel().getColumn(3); // Description

    // Add event listener for double-click on event
    eventsTable.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
          runAgainstSelectedEvent(eventData -> viewEventDetails(eventData));
        }
      }
    });

    // add the styling to table header
    JTableHeader header = eventsTable.getTableHeader();
    header.setFont(new Font("Sans-Serif", Font.BOLD, 12));
    header.setBackground(new Color(230, 230, 230));
    header.setForeground(Color.BLACK);
    header.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // apply the cell renderer
    EventCellRenderer cellRenderer = new EventCellRenderer();
    for (int i = 0; i < eventsTable.getColumnCount(); i++) {
      eventsTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    // add the table to a scroll pane
    JScrollPane scrollPane = new JScrollPane(eventsTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(scrollPane, BorderLayout.CENTER);
  }

  private DefaultTableModel createTableModel() {
    String[] columnNames = {"Time", "Event", "Location", "Description"};
    return new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  private void runAgainstSelectedEvent(Function<EventData, Void> function) {
    int selectedRow = eventsTable.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < events.size()) {
      EventData event = events.get(selectedRow);
      function.apply(event);
    }
  }

  private Void viewEventDetails(EventData eventData) {
    return null;
    // TODO: Implement the event details dialog
  }

  private void loadEventsToTable() {
    tableModel.setRowCount(0); // Clear existing rows
    for (EventData event : events) {
      Object[] rowData = {
          event.getAllDay()
              ? "All day"
              : event.getEndTime().toLocalDate().isAfter(event.getStartTime().toLocalDate())
                  ? "<html>"
                  + event.getStartTime().format(DateTimeFormatter.ofPattern("dd-MM-yy h:mm a"))
                  + "<br>"
                  + event.getEndTime().format(DateTimeFormatter.ofPattern("dd-MM-yy h:mm a"))
                  + "</html>"
                  : event.getStartTime().format(DateTimeFormatter.ofPattern("h:mm a"))
                      + " - "
                      + event.getEndTime().format(DateTimeFormatter.ofPattern("h:mm a")),
          event.getSubject(),
          event.getLocation(),
          event.getDescription()
      };
      tableModel.addRow(rowData);
    }
  }

  /**
   * Decorator class for the table cell renderer. This will add styling to the cells in the table.
   */
  static class EventCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column) {

      Component cell = super.getTableCellRendererComponent(
          table, value, isSelected, hasFocus, row, column);

      // set border for cells
      setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY),
          BorderFactory.createEmptyBorder(5, 5, 5, 5)));

      // center-align the time column
      if (column == 0) {
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(getFont().deriveFont(Font.BOLD));
        setBackground(new Color(245, 245, 245));
      } else {
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(getFont().deriveFont(Font.PLAIN));

        if (!isSelected) {
          setBackground(Color.WHITE);
        }
      }

      // styling for selected rows
      if (isSelected) {
        setBackground(new Color(210, 230, 255));
        setForeground(Color.BLACK);
      } else {
        setForeground(Color.BLACK);
      }

      return cell;
    }
  }

}