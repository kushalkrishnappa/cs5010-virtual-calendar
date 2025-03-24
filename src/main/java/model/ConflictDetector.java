package model;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import repository.IEventRepository;

class ConflictDetector {

  private final IEventRepository eventRepository;

  ConflictDetector(IEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  boolean hasConflict(LocalDateTime startTime, LocalDateTime endTime) {
    return !eventRepository.searchOverlaps(startTime, endTime).isEmpty();
  }

  boolean hasConflict(LocalDateTime dateTime) {
    return !eventRepository.getEventsAt(dateTime).isEmpty();
  }

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
