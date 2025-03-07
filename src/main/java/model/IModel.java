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
   * <p> If it contains both start and end time, it creates an event with start and end time.
   *
   * <p> Command for creating an event: create event --autoDecline <eventName> from
   * <dateStringTtimeString> to <dateStringTtimeString>
   *
   * <p> If it contains only start time, it creates an all day event.
   *
   * <p> Command for creating an all day event: create event --autoDecline <eventName> on
   * <dateStringTtimeString>
   *
   * @param eventDTO    The event to be created
   * @param autoDecline Whether the event should be auto declined
   * @throws EventConflictException   If the event conflicts with an existing event
   * @throws IllegalArgumentException If the event is invalid
   */
  void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException;

  /**
   * Creates a recurring event in the calendar.
   *
   * <p> If it contains RecurringEventDTO both start and end time, it creates a recurring event
   * with start and end. The event repeats on the specified weekdays for the specified number of
   * occurrence or until date.
   *
   * <p> If the occurrence attribute is set in the RecurringEventsDTO object, the event repeats for
   * the specified number of times. If the untilDate attribute is set in the RecurringDTO object,
   * the event repeats until the specified date.
   *
   * <p> The RecurringEventDTO object cannot set both occurrences and untilDate. It can only set
   * one
   * of them. If both are set, the method will throw an IllegalArgumentException.
   *
   * @param recurringEventDTO The recurring event to be created
   * @param autoDecline       Whether the event should be auto declined
   * @throws EventConflictException   If the event conflicts with an existing event
   * @throws IllegalArgumentException If the event is invalid
   */
  void createRecurringEvent(RecurringEventDTO recurringEventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException;

  /**
   * Edits an event in the calendar.
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
      EventDTO parametersToUpdate) throws IllegalArgumentException;


  // print events on <dateStringTtimeString>
  List<EventDTO> getEventsOnDate(LocalDate date);

  // print events from <dateStringTtimeString> to <dateStringTtimeString>
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
