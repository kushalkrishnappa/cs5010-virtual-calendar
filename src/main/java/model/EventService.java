package model;

import dto.EventDTO;
import repository.IEventRepository;

class EventService {

  private final IEventRepository eventRepository;

  EventService(IEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  void createEvent(EventDTO eventDTO) {
    eventRepository.insertEvent(eventDTO);
  }

  boolean deleteEvent(EventDTO eventDTO) {
    return eventRepository.deleteEvent(
        eventDTO.getSubject(),
        eventDTO.getStartTime(),
        eventDTO.getEndTime());
  }

}
