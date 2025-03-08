package repository;

import dto.EventDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryEventRepository implements IEventRepository {

  IntervalTree repository;

  public InMemoryEventRepository() {
    this.repository = new IntervalTree();
  }

  @Override
  public boolean insertEvent(EventDTO event) {
    return repository.insert(event);
  }

  @Override
  public List<EventDTO> searchOverlaps(LocalDateTime startTime, LocalDateTime endTime) {
    return repository.searchOverlapping(startTime, endTime);
  }

  @Override
  public boolean deleteEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return repository.delete(name, startTime, endTime);
  }

  @Override
  public boolean updateEvent(EventDTO eventDTO) {
    EventDTO storedEvent = repository.findEvent(eventDTO.getSubject(), eventDTO.getStartTime(),
        eventDTO.getEndTime());
    if (storedEvent == null) {
      return false;
    }
    repository.delete(storedEvent.getSubject(), storedEvent.getStartTime(),
        storedEvent.getEndTime());
    return repository.insert(eventDTO);
  }

  @Override
  public boolean updateEvents(List<EventDTO> eventDTOList) {
    // TODO: Implement this method
    return false;
  }

  @Override
  public EventDTO getEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return repository.findEvent(name, startTime, endTime);
  }

  @Override
  public List<EventDTO> getAllEvents() {
    return repository.getAllEvents();
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsByName(String name) {
    return repository.findByName(name);
  }

  @Override
  public List<EventDTO> getEventsStartingFrom(String name, LocalDateTime startTime) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsAt(LocalDateTime dateTime) {
    return repository.searchOverlappingPoint(dateTime);
  }

}
