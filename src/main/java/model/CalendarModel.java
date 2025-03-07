package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
      if (doesEventConflict(eventDTO)) {
        throw new EventConflictException("Event conflicts with existing event");
      }
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

  private boolean doesEventConflict(EventDTO newEvent) {
    // get all the events on the specific date
    List<EventDTO> events = eventRepository.getEventsOnDate(newEvent.getStartTime().toLocalDate());
    // iterate the events and check if the user is busy
    for (EventDTO storedEvent : events) {
      if (// condition 1: if the storedEvent is all day event return true
          isALlDayEvent(storedEvent)
          // condition 2: if the new event start time overlaps with stored event return true
          || doesNewEventStartTimeOverlap(newEvent, storedEvent)
          // condition 3: if the new event end time overlaps with stored event return true
          || doesNewEventEndTimeOverlap(newEvent, storedEvent)
          // condition 4: if the new event mask with stored event return true
          || doesNewEventMask(newEvent, storedEvent)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isALlDayEvent(EventDTO storedEvent) {
    return Objects.isNull(storedEvent.getEndTime());
  }

  private static boolean doesNewEventStartTimeOverlap(EventDTO newEvent, EventDTO storedEvent) {
    return newEvent.getStartTime().isBefore(storedEvent.getEndTime()) && newEvent.getEndTime()
        .isAfter(storedEvent.getStartTime());
  }

  private static boolean doesNewEventEndTimeOverlap(EventDTO newEvent, EventDTO storedEvent) {
    return newEvent.getEndTime().isAfter(storedEvent.getStartTime()) && newEvent.getStartTime()
        .isBefore(storedEvent.getEndTime());
  }

  private static boolean doesNewEventMask(EventDTO newEvent, EventDTO storedEvent) {
    return newEvent.getStartTime().isBefore(storedEvent.getStartTime()) && newEvent.getEndTime()
        .isAfter(storedEvent.getEndTime());
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
    // get all the events on the specific date
    List<EventDTO> events = eventRepository.getEventsOnDate(dateTime.toLocalDate());
    // iterate the events and check if the user is busy
    for (EventDTO event : events) {
      if (isALlDayEvent(event)
          || event.getStartTime().isEqual(dateTime)
          || event.getEndTime().isEqual(dateTime)
          || (event.getStartTime().isBefore(dateTime)
          && event.getEndTime().isAfter(dateTime))) {
        return true;
      }
    }
    return false;
  }
}
