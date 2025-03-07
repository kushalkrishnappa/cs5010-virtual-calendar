package model;

import dto.EventDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Represents a repository for events. This interface defines methods for accessing and managing
 * events in the repository.
 */
public interface IEventRepository {

  /**
   * Inserts an event into the repository.
   *
   * @param event The event to be stored in the repository
   * @return true if event is stored successfully, false otherwise
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
   * Update an event in the repository based on event name, start time, and end time.
   *
   * @param eventDTO The event to be updated
   * @return true if event is updated successfully, false otherwise
   */
  boolean updateEvent(EventDTO eventDTO);

  /**
   * Update a list of events in the repository based on name, start time, and end time.
   *
   * @param eventDTOList List of events to be updated
   * @return true if events are updated successfully, false otherwise
   */
  boolean updateEvents(List<EventDTO> eventDTOList);

  /**
   * Retrieves an event in the repository based on the name, start time, and end time.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return an event if it exists, empty otherwise
   */
  Optional<EventDTO> getEvent(String name, LocalDateTime startTime, LocalDateTime endTime);

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
   * Retrieves all events in the repository starting from the given time with the given name.
   *
   * @param name      The name of the event
   * @param startTime The start time of the event
   * @return a list of events starting from given time with given name
   */
  List<EventDTO> getEventsStartingFrom(String name, LocalDateTime startTime);

}
