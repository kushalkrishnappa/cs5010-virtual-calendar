package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * IModel interface defines the methods for view of mvc architecture for the CalendarApp
 * application.
 *
 * <p>It provides methods to create, edit, and retrieve events from the calendar.
 *
 * <p>It also provides methods to export the calendar to a CSV file and check if the user is busy
 * at a given time.
 */
public interface IModel {

  /**
   * Creates an event in the calendar.
   *
   * <p>If it contains both start and end time, it creates an event with start and end time.
   *
   * <p>If it contains only start time, it creates an all day event.
   *
   * @param eventDTO    The event to be created
   * @param autoDecline Whether the event should be auto declined
   * @throws EventConflictException   If the event conflicts with an existing event
   * @throws IllegalArgumentException If the event is invalid
   */
  void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException;

  /**
   * Edits a specific event in the calendar.
   *
   * @param name               name of the event
   * @param startTime          start time of the event
   * @param endTime            end time of the event
   * @param parametersToUpdate EventDTO with fields set for the corresponding parameters to be
   *                           updated
   * @return The number of events edited
   * @throws IllegalArgumentException if the edit request is invalid
   */
  Integer editEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws EventConflictException, IllegalArgumentException;


  /**
   * Gets all events in the calendar on specified date.
   *
   * @param date date to get events on
   * @return list of events on the given date
   */
  List<EventDTO> getEventsOnDate(LocalDate date);

  /**
   * Gets all events in the calendar on specified date range.
   *
   * @param start start time of the range
   * @param end  end time of the range
   * @return list of events in the given range
   */
  List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end);

  /**
   * Exports the calendar to a CSV file.
   *
   * @param fileName filename to export
   * @return the file name
   * @throws CalendarExportException if the export fails
   */
  String exportToCSV(String fileName) throws CalendarExportException;

  /**
   * Checks if the user is busy at a given dateTime.
   *
   * @param dateTime the dateTime to check if the user is busy
   * @return true if user is busy, false otherwise
   */
  Boolean isBusy(LocalDateTime dateTime);
}
