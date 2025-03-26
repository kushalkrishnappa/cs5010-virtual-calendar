package repository;

import dto.EventDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents the interface for the Event Repository. It provides methods to insert,
 * delete, get, and search for events in the repository. The implementation of this interface will
 * handle the actual data storage and retrieval from the underlying data source (e.g., database,
 * ADT, etc.).
 *
 * <p>For this implementation, the IntervalTree ADT is used to store the events.
 *
 */
public interface IEventRepository {

  /**
   * Inserts an event into the repository.
   *
   * @param event The event to be stored in the repository
   */
  boolean insertEvent(EventDTO event);

  /**
   * Deletes an event from the repository based on the name, start time, and end time.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return true if event is deleted successfully, false otherwise
   */
  boolean deleteEvent(String name, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Retrieves an event in the repository based on the name, start time, and end time.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return an event if it exists, empty otherwise
   */
  EventDTO getEvent(String name, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Return all events in the repository.
   *
   * @return list of all events in the repository
   */
  List<EventDTO> getAllEvents();

  /**
   * Retrieves all events in the repository on given date.
   *
   * @param date The date to get events for
   * @return a list of events on the given date
   */
  List<EventDTO> getEventsOnDate(LocalDate date);

  /**
   * Retrieves all events in the repository in the given time range.
   *
   * @param startTime The start time of the events range
   * @param endTime   The end time of the events range
   * @return a list of events in the given time range
   */
  List<EventDTO> getEventsInRange(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Retrieves all events in the repository with the given name.
   *
   * @param name The name of the event
   * @return a list of events with the given name
   */
  List<EventDTO> getEventsByName(String name);

  /**
   * Retrieves all events in the repository at the given date and time.
   *
   * @param dateTime The date and time to get events for
   * @return a list of events at the given date and time
   */
  List<EventDTO> getEventsAt(LocalDateTime dateTime);

  /**
   * Retrieves all events in the repository that overlap with the given time range.
   *
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return a list of events that overlap with the given time range
   */
  List<EventDTO> searchOverlaps(LocalDateTime startTime, LocalDateTime endTime);
}
