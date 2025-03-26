package model;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import repository.IEventRepository;

/**
 * This class is responsible for identifying conflicts between calendar events.
 *
 * <p>It uses an {@link IEventRepository} to access event data and determine if a given time
 * range or specific time point overlaps with any existing events.
 */
class ConflictDetector {

  private final IEventRepository eventRepository;

  /**
   * Constructs a {@code ConflictDetector} with the specified event repository.
   *
   * @param eventRepository the repository used to access calendar event data
   */
  ConflictDetector(IEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Checks if there is any event scheduled that overlaps with the given start and end times.
   *
   * @param startTime the start of the time range to check
   * @param endTime   the end of the time range to check
   * @return {@code true} if there is an overlapping event, {@code false} otherwise
   */
  boolean hasConflict(LocalDateTime startTime, LocalDateTime endTime) {
    return !eventRepository.searchOverlaps(startTime, endTime).isEmpty();
  }

  /**
   * Checks if there is any event scheduled at the given date and time.
   *
   * @param dateTime the specific dateTime to check for events
   * @return {@code true} if there is an event at the given time, {@code false} otherwise
   */
  boolean hasConflict(LocalDateTime dateTime) {
    return !eventRepository.getEventsAt(dateTime).isEmpty();
  }

  /**
   * Returns a list of events that conflict with the given event.
   *
   * <p>This method considers both standard and recurring events when checking for conflicts.
   *
   * @param event the event to check for conflicts with
   * @return a list of events that conflict with the given event
   */
  List<EventDTO> getConflicts(EventDTO event) {
    List<EventDTO> conflicts = new ArrayList<>();
    if (event.getIsRecurring()) {
      RecurrenceService.generateRecurrence(event).forEach(
          eventDTO -> conflicts.addAll(
              eventRepository.searchOverlaps(eventDTO.getStartTime(), eventDTO.getEndTime())
          )
      );
    } else {
      conflicts.addAll(eventRepository.searchOverlaps(event.getStartTime(), event.getEndTime()));
    }
    return conflicts;
  }
}
