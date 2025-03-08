package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import repository.IEventRepository;
import repository.InMemoryEventRepository;

/**
 * This class represents the model for the CalendarApp application. It implements the IModel
 * interface.
 *
 * <p>It provides methods to create, edit, and retrieve events from the calendar.
 *
 * <p>It also provides methods to export the calendar to a CSV file and check if the user is busy
 * at a given time.
 */
public class CalendarModel implements IModel {

  IEventRepository eventRepository;

  private static final DateTimeFormatter calenderExportDateFormatter = DateTimeFormatter.ofPattern(
      "MM/dd/yyyy");
  private static final DateTimeFormatter calenderExportTimeFormatter = DateTimeFormatter.ofPattern(
      "hh:mm a");

  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException {
    // if autodecline is set, check for conflicts and throw exception if there is a conflict
    if (autoDecline) {
      // TODO: check for conflict
    }
    // check what kind of event to create
    if (eventDTO.getStartTime() != null && eventDTO.getEndTime() != null) {
      eventRepository.insertEvent(eventDTO); // create a single spanned event
    } else if (eventDTO.getStartTime() != null) {
      eventRepository.insertEvent(eventDTO); // create a single all day event
    } else {
      throw new IllegalArgumentException("Invalid event");
    }
  }

  @Override
  public Integer editEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws IllegalArgumentException {
    // TODO: Implement this method
    return 0;
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return eventRepository.getEventsOnDate(date);
  }


  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsInRange(startTime, endTime);
  }

  @Override
  public String exportToCSV(String fileName) throws CalendarExportException {
    // Get all events
    List<EventDTO> events = eventRepository.getAllEvents();

    // If there are no events, throw an CalendarExportException
    if (events.isEmpty()) {
      throw new CalendarExportException("No events to export");
    }

    // Define file path - if the file name does not end with .csv, add it
    String csvFilePath = fileName.endsWith(".csv") ? fileName : fileName + ".csv";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      // Write the header
      writer.write(getCSVHeader());
      writer.newLine();

      // Write each event as a row in CSV
      for (EventDTO event : events) {
        writer.write(String.join(",",
            // Subject
            escapeCSV(event.getSubject()),
            // Start Date
            event.getStartTime() != null ? event.getStartTime().format(calenderExportDateFormatter)
                : "",
            // Start Time
            event.getStartTime() != null && !event.getAllDay() ? event.getStartTime()
                .format(calenderExportTimeFormatter) : "",
            // End Date
            event.getEndTime() != null ? event.getEndTime().format(calenderExportDateFormatter)
                : "",
            // End Time
            event.getEndTime() != null && !event.getAllDay() ? event.getEndTime()
                .format(calenderExportTimeFormatter) : "",
            // All Day Event
            event.getAllDay() ? "True" : "False",
            // Description
            escapeCSV(event.getDescription()),
            // Location
            escapeCSV(event.getLocation()),
            // Private
            event.getIsPublic() ? "False" : "True"
        ));
        writer.newLine();
      }
      return csvFilePath;
    } catch (IOException e) {
      throw new CalendarExportException("Error exporting to CSV");
    }
  }

  private static String getCSVHeader() {
    return "Subject,"
        + "Start Date,"
        + "Start Time,"
        + "End Date,"
        + "End Time,"
        + "All Day Event,"
        + "Description,"
        + "Location,"
        + "Private";
  }

  private static String escapeCSV(String value) {
    // if the value is null or empty, return empty string
    if (value == null || value.isEmpty()) {
      return "";
    }
    // Replace each double quote with two double quotes to escape it correctly
    value = value.replace("\"", "\"\"");
    value = value.replace("\n", "");
    // Enclose the entire value in double quotes
    return "\"" + value + "\"";
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    List<EventDTO> eventsAtSpecifiedTime = eventRepository.getEventsAt(dateTime);
    for (EventDTO event : eventsAtSpecifiedTime) {
      System.out.println(event.getSubject());
    }
    return !eventsAtSpecifiedTime.isEmpty();
  }
}
