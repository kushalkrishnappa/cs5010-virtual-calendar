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
import java.util.function.Consumer;
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

/**
 * This class represents a dialog that displays the events for a specific day. It extends JDialog
 * and provides GUI for viewing and managing events on that day. The dialog includes a table to
 * display the events, a header with the date, and buttons for creating and editing events.
 */
public class DayDialog extends JDialog {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      "MMMM d, yyyy");

  private final LocalDate date;

  private final List<EventData> events;

  private JTable eventsTable;

  private DefaultTableModel tableModel;

  private final CalendarFeatures calendarFeatures;

  /**
   * This constructor initializes the DayDialog with the specified owner frame, calendar features,
   * date, and events. It sets up the layout and components of the dialog.
   *
   * @param owner            the owner frame of the dialog
   * @param calendarFeatures the calendar features to be used for event management
   * @param date             the date for which events are to be displayed
   * @param events           the list of events for the specified date
   */
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

    JButton newEventButton = new JButton("New Event");
    JButton editButton = new JButton("Edit Event");

    newEventButton.addActionListener(e -> {
      calendarFeatures.requestEventCreation(date);
    });
    editButton.addActionListener(e -> {
      runAgainstSelectedEvent(calendarFeatures::requestEventEdit);
    });

    buttonPanel.add(newEventButton);
    buttonPanel.add(editButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void createDateAndBackButtonHeader() {
    JPanel headerPanel = new JPanel(new BorderLayout());
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
          runAgainstSelectedEvent(calendarFeatures::requestEventViewDetails);
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

  private void runAgainstSelectedEvent(Consumer<EventData> function) {
    int selectedRow = eventsTable.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < events.size()) {
      EventData event = events.get(selectedRow);
      function.accept(event);
    }
  }

  private void loadEventsToTable() {
    tableModel.setRowCount(0); // Clear existing rows
    for (EventData event : events) {
      Object[] rowData = {event.getAllDay()
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
   * This class represents a custom cell renderer for the events table. It extends
   * DefaultTableCellRenderer and adds some customization to the rendering of the table cells. It
   * sets the background color, font, and border for the cells based on their state (selected or not
   * selected) and the column type (time or event).
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
