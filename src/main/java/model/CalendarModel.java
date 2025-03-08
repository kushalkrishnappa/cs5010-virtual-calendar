package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import repository.IEventRepository;
import repository.InMemoryEventRepository;

/**
 * This class represents the model for the CalendarApp application. It implements the IModel
 * interface.
 *
 * <p>It provides methods to create, edit, and retrieve events from the calendar.
 *
 * <p>It also provides methods to export the calendar to a CSV file and check if the user is busy
 * at a given time.
 */
public class CalendarModel implements IModel {

  IEventRepository eventRepository;

  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException {
    // if autodecline is set, check for conflicts and throw exception if there is a conflict
    if (autoDecline) {
      // TODO: check for conflict
    }
    // check what kind of event to create
    if (eventDTO.getStartTime() != null && eventDTO.getEndTime() != null) {
      eventRepository.insertEvent(eventDTO); // create a single spanned event
    } else if (eventDTO.getStartTime() != null) {
      eventRepository.insertEvent(eventDTO); // create a single all day event
    } else {
      throw new IllegalArgumentException("Invalid event");
    }
  }

  @Override
  public Integer editEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws IllegalArgumentException {
    // TODO: Implement this method
    return 0;
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return eventRepository.getEventsOnDate(date);
  }


  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsInRange(startTime, endTime);
  }

  @Override
  public String exportToCSV(String fileName) throws CalendarExportException {
    // TODO: Implement this method
    return "";
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    List<EventDTO> eventsAtSpecifiedTime = eventRepository.getEventsAt(dateTime);
    for (EventDTO event : eventsAtSpecifiedTime) {
      System.out.println(event.getSubject());
    }
    return !eventsAtSpecifiedTime.isEmpty();
  }
}
