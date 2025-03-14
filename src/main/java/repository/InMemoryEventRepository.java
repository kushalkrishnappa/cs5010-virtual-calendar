package repository;

import dto.EventDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * InMemoryEventRepository is an implementation of IEventRepository that stores events in memory.
 *
 * <p>It uses an IntervalTree to store the events and perform operations like insert, search,
 * delete.
 */
public class InMemoryEventRepository implements IEventRepository {

  IntervalTree repository;

  /**
   * Constructor for InMemoryEventRepository.
   */
  public InMemoryEventRepository() {
    this.repository = new IntervalTree();
  }

  /**
   * Insert an event into the repository.
   *
   * @param event The event to be stored in the repository
   * @return true if the event was successfully inserted, false otherwise
   */
  @Override
  public boolean insertEvent(EventDTO event) {
    return repository.insert(event);
  }

  /**
   * Search for events that overlap with the given time range.
   *
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return A list of events that overlap with the given time range
   */
  @Override
  public List<EventDTO> searchOverlaps(LocalDateTime startTime, LocalDateTime endTime) {
    return repository.searchOverlapping(startTime, endTime);
  }

  /**
   * Delete an event from the repository.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return true if the event was successfully deleted, false otherwise
   */
  @Override
  public boolean deleteEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return repository.delete(name, startTime, endTime);
  }

  /**
   * Get an event with the given name, start time and end time.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return The event with the given name, start time and end time
   */
  @Override
  public EventDTO getEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return repository.findEvent(name, startTime, endTime);
  }

  /**
   * Get all events in the repository.
   *
   * @return A list of all events in the repository
   */
  @Override
  public List<EventDTO> getAllEvents() {
    return repository.getAllEvents();
  }

  /**
   * Get all events that occur on the given date.
   *
   * @param date The date to get events for from repository
   * @return A list of events that occur on the given date
   */
  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return repository
        .searchOverlapping(date.atStartOfDay(), LocalDateTime.of(date, LocalTime.MAX));
  }

  /**
   * Get all events that occur in the given time range.
   *
   * @param start The start time of the events range
   * @param end   The end time of the events range
   * @return A list of events that occur in the given time range
   */
  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return searchOverlaps(start, end);
  }

  /**
   * Get all events with the given name.
   *
   * @param name The name of the event to search for
   * @return A list of events with the given name
   */
  @Override
  public List<EventDTO> getEventsByName(String name) {
    return repository.findByName(name);
  }


  /**
   * Get all events that occur at the given date and time.
   *
   * @param dateTime The date and time to get events for
   * @return A list of events that occur at the given date and time
   */
  @Override
  public List<EventDTO> getEventsAt(LocalDateTime dateTime) {
    return repository.searchOverlappingPoint(dateTime);
  }
}
