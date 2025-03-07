package model;

import dto.EventDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryEventRepository implements IEventRepository {

  List<EventDTO> repository = new ArrayList<>();

  @Override
  public boolean insertEvent(EventDTO event) {
    return repository.add(event);
  }

  @Override
  public boolean deleteEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return false;
  }

  @Override
  public boolean updateEvent(EventDTO eventDTO) {
    return false;
  }

  @Override
  public boolean updateEvents(List<EventDTO> eventDTOList) {
    return false;
  }

  @Override
  public Optional<EventDTO> getEvent(String name, LocalDateTime startTime, LocalDateTime endTime) {
    return Optional.empty();
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return repository;
  }

  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsByName(String name) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsStartingFrom(String name, LocalDateTime startTime) {
    return List.of();
  }
}
