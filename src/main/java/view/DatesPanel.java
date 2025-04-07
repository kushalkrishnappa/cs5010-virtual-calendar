package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * CalendarPanel displays the monthly calendar view with days and events for the selected month
 */
public class DatesPanel extends JPanel {

  // fields for month navigation and display

  private JPanel monthNavPanel;

  private JLabel monthYearLabel;

  private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

  private JButton prevMonthBtn;

  private JButton nextMonthBtn;

  // fields for the calendar table

  private JTable calendarTable;

  private DefaultTableModel tableModel;

  private YearMonth currentYearMonth;

  private CalendarFeatures calendarFeatures;

  private String currentTimeZone;

  /**
   * Constructor initializes the calendar panel
   */
  public DatesPanel() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    initComponents();
  }

  /**
   * Initialize panel components
   */
  private void initComponents() {
    // month navigation and display
    monthNavPanel = new JPanel(new BorderLayout());

    // create monthYearLabel and set current month
    createMonthYearLabel();

    // create buttons for month navigation
    createPrevMonthBtn();
    createNextMonthBtn();

    // add buttons and label to the month navigation panel
    updateMonthNavPanel();

    // create and set calendar table
    createCalendarTable();

    // Add components to the panel
    add(monthNavPanel, BorderLayout.NORTH);
    add(new JScrollPane(calendarTable), BorderLayout.CENTER);
  }

  private void createMonthYearLabel() {
    monthYearLabel = new JLabel("", JLabel.CENTER); // create empty label
    monthYearLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14)); // set font
  }

  private void createPrevMonthBtn() {
    prevMonthBtn = new JButton("<<");
  }

  private void createNextMonthBtn() {
    nextMonthBtn = new JButton(">>");
  }

  private void updateMonthNavPanel() {
    monthNavPanel.add(prevMonthBtn, BorderLayout.WEST);
    monthNavPanel.add(monthYearLabel, BorderLayout.CENTER);
    monthNavPanel.add(nextMonthBtn, BorderLayout.EAST);
  }

  public void updateMonthYearLabel(YearMonth monthYear) {
    currentYearMonth = monthYear;
    monthYearLabel.setText(currentYearMonth.format(monthYearFormatter));
  }

  private void createCalendarTable() {
    DefaultTableModel newTableModel = createTableModel();
    setDaysOfWeekForTableModel(newTableModel);
    tableModel = newTableModel;

    calendarTable = new JTable(tableModel);

    // set table properties
    calendarTable.setRowHeight(80);
    calendarTable.setShowGrid(true);
    calendarTable.setGridColor(Color.LIGHT_GRAY);
    calendarTable.setFillsViewportHeight(true);
    // custom renderer for calendar cells
    calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());
    // the table columns cannot be moved to left and right
    calendarTable.getTableHeader().setReorderingAllowed(false);
  }

  private DefaultTableModel createTableModel() {
    return new DefaultTableModel() {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
  }

  private void setDaysOfWeekForTableModel(DefaultTableModel tableModel) {
    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    tableModel.setColumnIdentifiers(dayNames);
  }

  /**
   * Set features for controller communication
   *
   * @param features The calendar features
   */
  public void setFeatures(CalendarFeatures features) {
    calendarFeatures = features;

    // prev month button action listener
    prevMonthBtn.addActionListener(e -> {
      calendarFeatures.previousMonthYear(currentYearMonth);
    });

    // next month button action listener
    nextMonthBtn.addActionListener(e -> {
      calendarFeatures.nextMonthYear(currentYearMonth);
    });

    // add mouse listener to handle cell clicks
    calendarTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int row = calendarTable.getSelectedRow();
        int col = calendarTable.getSelectedColumn();
        if (row >= 0 && col >= 0) {
          Object value = calendarTable.getValueAt(row, col);
          if (value != null) {
            String cellValue = value.toString();
            if (!cellValue.isEmpty()) {
              // Extract day number from cell value
              String dayStr = cellValue.split("\n")[0].trim();
              try {
                int day = Integer.parseInt(dayStr);
                // Calculate the actual date to handle days from previous/next months
                LocalDate selectedDate = getDateFromCell(row, col, day);
                calendarFeatures.viewDay(selectedDate);
              } catch (NumberFormatException ex) {
                // Not a valid day cell
              }
            }
          }
        }
      }
    });
  }

  /**
   * Update the calendar with the current month
   */
  public void updateCalendarYearMonthDates(YearMonth calendarYearMonth) {

    // clear and set new rows of the table
    tableModel.setRowCount(0);
    tableModel.setRowCount(6); // max 6 rows for a month

    // get the day of week for the first day of month
    LocalDate firstDayOfMonth = calendarYearMonth.atDay(1);
    // get the calendar table cell for first day of month
    int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

    // get number of days in month
    int daysInMonth = calendarYearMonth.lengthOfMonth();

    // get number of days in previous month
    YearMonth prevMonth = calendarYearMonth.minusMonths(1);
    int daysInPrevMonth = prevMonth.lengthOfMonth();

    // fill the calendar table
    int dayCounter = 1;
    int row = 0;

    // previous month days (fill the cells prior to start filling this month's cells)
    for (int i = 0; i < dayOfWeek; i++) {
      int prevMonthDay = daysInPrevMonth - dayOfWeek + i + 1;
      String cellContent = prevMonthDay + "\n";
      tableModel.setValueAt(cellContent, row, i);
    }

    // current month's first week days (fill first week of this month's cell)
    for (int i = dayOfWeek; i < 7; i++) {
      String cellContent = dayCounter + "\n";
      // TODO: Add event details to cell content
      tableModel.setValueAt(cellContent, row, i);
      dayCounter++;
    }

    // rest of the days (remaining cells in the current month)
    while (dayCounter <= daysInMonth) {
      row++;
      for (int i = 0; i < 7 && dayCounter <= daysInMonth; i++) {
        String cellContent = dayCounter + "\n";
        // TODO: Add event details to cell content
        tableModel.setValueAt(cellContent, row, i);
        dayCounter++;
      }
    }

    // next month days (unfilled cells in the current month will be filled with next month's dates)
    int nextMonthDay = 1;
    while (row < 6) {
      for (int i = 0; i < 7; i++) {
        if (tableModel.getValueAt(row, i) == null) {
          String cellContent = nextMonthDay + "\n";
          tableModel.setValueAt(cellContent, row, i);
          nextMonthDay++;
        }
      }
      row++;
    }

    // create the new updated view of the calendar table
    calendarTable.repaint();
  }

  /**
   * Get the actual date for a cell based on its position and displayed day
   *
   * @param row          The row of the cell
   * @param col          The column of the cell
   * @param displayedDay The day number displayed in the cell
   * @return The LocalDate corresponding to this cell
   */
  private LocalDate getDateFromCell(int row, int col, int displayedDay) {
    // get first day of month position
    LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
    int firstDayPosition = firstDayOfMonth.getDayOfWeek().getValue() % 7;

    // calculate position in the calendar
    int position = row * 7 + col;
    int daysInMonth = currentYearMonth.lengthOfMonth();

    // previous month
    if (position < firstDayPosition) {
      return currentYearMonth.minusMonths(1).atDay(displayedDay);
    }
    // next month
    else if (position >= firstDayPosition + daysInMonth) {
      return currentYearMonth.plusMonths(1).atDay(displayedDay);
    }
    // current month
    else {
      return currentYearMonth.atDay(displayedDay);
    }
  }

  public void setCurrentTimezone(String tz) {
    this.currentTimeZone = tz;
    calendarTable.repaint();
  }

  /**
   * Custom renderer for calendar cells
   */
  class CalendarCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
      Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
          column);

      if (value != null) {
        String cellContent = value.toString();
        if (!cellContent.isEmpty()) {

          String[] parts = cellContent.split("\n");
          int day = Integer.parseInt(parts[0]);

          String parsedDay = parseDay(day);
          setText(parsedDay);

          // style days from other months
          boolean isCurrentMonth = isCurrentMonth(row, column);

          if (!isCurrentMonth) {
            setBackground(new Color(240, 240, 240));
            setForeground(Color.GRAY);
          } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
          }

          // highlight current day
          highlightCurrentDay(day, isCurrentMonth);

          // set vertical alignment to top
          setVerticalAlignment(SwingConstants.TOP);
        }
      }

      return cell;
    }

    private String parseDay(int day) {
      return "<html>"
          + "<font size='4' color='black'>"
          + day
          + "</font>"
          + "</html>";
    }

    private boolean isCurrentMonth(int row, int column) {
      boolean isCurrentMonth = true;
      int firstDayOfWeek = currentYearMonth.atDay(1).getDayOfWeek().getValue() % 7;
      int position = row * 7 + column;
      int daysInMonth = currentYearMonth.lengthOfMonth();

      if (position < firstDayOfWeek || position >= firstDayOfWeek + daysInMonth) {
        isCurrentMonth = false;
      }
      return isCurrentMonth;
    }

    private void highlightCurrentDay(int day, boolean isCurrentMonth) {
      LocalDate today = ZonedDateTime.now(ZoneId.of(currentTimeZone)).toLocalDate();
      if (day == today.getDayOfMonth() &&
          currentYearMonth.getMonthValue() == today.getMonthValue() &&
          currentYearMonth.getYear() == today.getYear() &&
          isCurrentMonth) {
        setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
      } else {
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
      }
    }

  }
}