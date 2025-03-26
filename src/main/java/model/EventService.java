package model;

import dto.EventDTO;
import repository.IEventRepository;

/**
 * This class provides methods for managing calendar events, specifically creating and deleting
 * events using the provided {@link IEventRepository}.
 */
class EventService {

  private final IEventRepository eventRepository;

  /**
   * Constructs an {@code EventService} with the specified event repository.
   *
   * @param eventRepository the repository used to access and manage calendar event data
   */
  EventService(IEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  /**
   * Creates a new event in the calendar using the details provided in the {@link EventDTO}.
   *
   * @param eventDTO the data transfer object containing the details of the event to be created
   */
  void createEvent(EventDTO eventDTO) {
    eventRepository.insertEvent(eventDTO);
  }

  /**
   * Deletes an event from the calendar based on its name, start time, and end time.
   *
   * @param eventDTO the data transfer object containing the identifying details of the event to be
   *                 deleted
   */
  void deleteEvent(EventDTO eventDTO) {
    eventRepository.deleteEvent(
        eventDTO.getSubject(),
        eventDTO.getStartTime(),
        eventDTO.getEndTime());
  }

}
