package service;

import dto.EventDTO;
import exception.CalendarExportException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This strategy exports the events in a csv format required by Google calendar's import.
 */
public class CSVCalendarExporter implements ICalendarExporter {


  private static final DateTimeFormatter calenderExportDateFormatter =
      DateTimeFormatter.ofPattern("MM/dd/yyyy");

  private static final DateTimeFormatter calenderExportTimeFormatter =
      DateTimeFormatter.ofPattern("hh:mm a");

  @Override
  public String export(List<EventDTO> events) {
    StringBuilder csvContent = new StringBuilder();

    csvContent.append(getCSVHeader())
        .append(System.lineSeparator());

    // Write each event as a row in CSV
    for (EventDTO event : events) {
      csvContent.append(String.join(",",
              // Subject
              escapeCSV(event.getSubject()),
              // Start Date
              event.getStartTime() != null ? event.getStartTime().format(calenderExportDateFormatter)
                  : "",
              // Start Time
              event.getStartTime() != null && !event.getIsAllDay() ? event.getStartTime()
                  .format(calenderExportTimeFormatter) : "",
              // End Date
              event.getEndTime() != null ? event.getEndTime().format(calenderExportDateFormatter)
                  : "",
              // End Time
              event.getEndTime() != null && !event.getIsAllDay() ? event.getEndTime()
                  .format(calenderExportTimeFormatter) : "",
              // All Day Event
              event.getIsAllDay() ? "True" : "False",
              // Description
              escapeCSV(event.getDescription()),
              // Location
              escapeCSV(event.getLocation()),
              // Private
              event.getIsPublic() ? "False" : "True"))
          .append(System.lineSeparator());
    }
    return csvContent.toString();
  }

  /**
   * Get the CSV header.
   *
   * @return the CSV header
   */
  private static String getCSVHeader() {
    return "Subject," + "Start Date," + "Start Time," + "End Date," + "End Time," + "All Day Event,"
        + "Description," + "Location," + "Private";
  }

  /**
   * Escapes a value for CSV format.
   *
   * @param value The value to escape
   * @return The escaped value
   */
  private static String escapeCSV(String value) {
    // if the value is null or empty, return empty string
    if (value == null || value.isEmpty()) {
      return "";
    }
    // Replace each double quote with two double quotes to escape it correctly
    value = value.replace("\"", "");
    value = value.replace("\n", "");
    // Enclose the entire value in double quotes
    return "\"" + value + "\"";
  }
}
