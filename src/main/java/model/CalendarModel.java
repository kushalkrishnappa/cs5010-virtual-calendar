package model;

import dto.EventDTO;
import dto.EventDTO.EventDTOBuilder;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import repository.IEventRepository;
import repository.InMemoryEventRepository;
import service.ICalendarExporter;

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
  ConflictDetector conflictDetector;
  EventService eventService;

  /**
   * Constructs a CalendarModel object with an InMemoryEventRepository.
   */
  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
    this.conflictDetector = new ConflictDetector(eventRepository);
    this.eventService = new EventService(eventRepository);
  }

  /**
   * Create an event in the calendar. The event can be set to auto-decline if there is a conflict.
   *
   * <p>It checks if the event is recurring, and if so, creates multiple events.
   *
   * <p>It checks if the event is all day, and if so, creates a single or recurring all day event.
   *
   * @param eventDTO    The event to be created
   * @param autoDecline Whether the event should be auto declined
   */
  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline) {
    if (Objects.isNull(eventDTO)) {
      throw new IllegalArgumentException("EventDTO cannot be null");
    }
    EventValidator.validateEvent(eventDTO);
    eventDTO = EventValidator.createValidEvent(eventDTO);

    if (eventDTO.getIsRecurring()) {
      // recurring all day or spanned event
      // generate recurrence
      List<EventDTO> eventDTOs = RecurrenceService.generateRecurrence(eventDTO);
      // check for conflict
      if (eventDTOs.stream().anyMatch(
          event -> conflictDetector
              .hasConflict(event.getStartTime(), event.getEndTime()))) {
        throw new EventConflictException("Recurring event have conflict");
      }
      // insert the events
      eventDTOs.forEach(eventService::createEvent);
    } else {
      // an all day or spanned event
      // check conflict if autoDecline is specified
      if (autoDecline
          && conflictDetector.hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime())) {
        throw new EventConflictException("Auto-declined event has conflict");
      } else {
        eventService.createEvent(eventDTO);
      }
    }
  }

  /**
   * Edit a specific event in the calendar.
   *
   * @param eventName          name of the event
   * @param startTime          start time of the event
   * @param endTime            end time of the event
   * @param parametersToUpdate EventDTO with fields set for the corresponding parameters to be
   *                           updated
   * @return The number of events edited
   * @throws EventConflictException   If the event conflicts with an existing event
   * @throws IllegalArgumentException If the edit request is invalid
   */
  @Override
  public Integer editEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws EventConflictException, IllegalArgumentException {
    if (Objects.isNull(parametersToUpdate)) {
      throw new IllegalArgumentException("parametersToUpdate cannot be null");
    }
    if (Objects.isNull(endTime)) {
      return editEventsWithName(eventName, startTime, parametersToUpdate);
    }
    return editEventWithKey(eventName, startTime, endTime, parametersToUpdate);
  }

  /**
   * Edit an event with the same name.
   *
   * @param eventName          the event name
   * @param startTime          the start time
   * @param endTime            the end time
   * @param parametersToUpdate the parameters to update
   * @return the number of events updated
   */
  private Integer editEventWithKey(String eventName, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) {
    int eventsUpdated = 0;

    EventDTO existingEvent = eventRepository.getEvent(eventName, startTime, endTime);
    if (Objects.isNull(existingEvent)) {
      throw new IllegalArgumentException("Event with name " + eventName + " not found");
    }

    // existingEvent to update found
    // simple field updates (no conflict check)
    EventDTOBuilder updatedEventBuilder = getEventBuilderWithUpdatedParameters(
        existingEvent, parametersToUpdate);

    // changing recurring details
    if (Objects.isNull(parametersToUpdate.getIsRecurring())) {
      // and if was already a part of recurring event
      if (existingEvent.getIsRecurring()) {
        throw new IllegalArgumentException(
            "Recurrence details cannot be updated for existing recurring event "
                + "(Conflict in updating this and following event OR all events in recurrence)");
      }
    }

    EventDTO updatedEvent = updatedEventBuilder.build();
    EventValidator.validateEvent(updatedEvent);
    // any event conflict except the one to be updated
    if (conflictDetector.getConflicts(updatedEvent).stream()
        .anyMatch(event -> !event.equals(existingEvent))) {
      throw new EventConflictException("Updated event has conflict with existing event");
    }

    if (updatedEvent.getIsRecurring()) {
      List<EventDTO> eventsToAdd = RecurrenceService.generateRecurrence(updatedEvent);
      eventsToAdd.forEach(eventService::createEvent);
    } else {
      eventService.createEvent(updatedEvent);
    }
    eventService.deleteEvent(existingEvent);
    eventsUpdated++;
    return eventsUpdated;
  }

  /**
   * Edit events with the same name.
   *
   * @param eventName          the event name
   * @param startTime          the start time
   * @param parametersToUpdate the parameters to update
   * @return the number of events updated
   */
  private Integer editEventsWithName(String eventName, LocalDateTime startTime,
      EventDTO parametersToUpdate) {
    // get the existing recurring events whose start time in after the provided start time
    List<EventDTO> eventsByName = eventRepository.getEventsByName(eventName);
    eventsByName = eventsByName.stream()
        .filter(EventDTO::getIsRecurring) // ignore events that are not part of recurrence series
        .collect(Collectors.toList());
    if (Objects.nonNull(startTime)) {
      eventsByName = eventsByName.stream()
          .filter(event -> (startTime.isBefore(event.getStartTime())
              || startTime.equals(event.getStartTime())))
          .collect(Collectors.toList());
    }
    // found no events to update
    if (eventsByName.isEmpty()) {
      throw new IllegalArgumentException("Event with name " + eventName + " not found");
    }

    // gather the events to update in a list
    List<EventDTO> eventsToUpdate = new ArrayList<>();

    // if the recurrence is false or set to null
    if (Objects.isNull(parametersToUpdate.getIsRecurring())
        || !parametersToUpdate.getIsRecurring()) {
      for (EventDTO existingEvent : eventsByName) {
        EventDTOBuilder updatedEventBuilder = getEventBuilderWithUpdatedParameters(
            existingEvent, parametersToUpdate);
        EventDTO updatedEvent = updatedEventBuilder.build();
        EventValidator.validateEvent(updatedEvent);
        // check conflicts (early exit)
        List<EventDTO> finalEventsByName = eventsByName;
        if (conflictDetector.getConflicts(updatedEvent).stream()
            .anyMatch(event -> {
              return finalEventsByName.stream().noneMatch(event::equals);
            })) {
          throw new EventConflictException("Updated event has conflict with existing event");
        }

        eventsToUpdate.add(updatedEvent);
      }

    } else {
      // update recurrence details of the recurrence series.
      // delete the events in future and add a new series
      EventDTOBuilder newRecurEventBuilder = getEventBuilderWithUpdatedParameters(
          eventsByName.get(0), parametersToUpdate);

      EventDTO updatedRecurEvent = newRecurEventBuilder.build();
      EventValidator.validateEvent(updatedRecurEvent);
      // check conflict
      List<EventDTO> finalEventsByName = eventsByName;
      if (conflictDetector.getConflicts(updatedRecurEvent).stream()
          .anyMatch(event -> {
            return finalEventsByName.stream().noneMatch(event::equals);
          })) {
        throw new EventConflictException("Updated event has conflict with existing event");
      }

      eventsToUpdate.addAll(RecurrenceService.generateRecurrence(updatedRecurEvent));

    }
    eventsToUpdate.forEach(eventService::createEvent);
    eventsByName.forEach(eventService::deleteEvent);
    return eventsByName.size();
  }

  private static EventDTOBuilder getEventBuilderWithUpdatedParameters(
      EventDTO existingEvent, EventDTO parametersToUpdate) {
    if (Objects.nonNull(parametersToUpdate.getIsRecurring())
        && parametersToUpdate.getIsRecurring()
        && Objects.nonNull(parametersToUpdate.getRecurringDetails().getOccurrences())
        && Objects.nonNull(parametersToUpdate.getRecurringDetails().getUntilDate())) {
      throw new IllegalArgumentException(
          "Either occurrence or until date must be specified for updating, not both");
    }

    return EventDTO.getBuilder()
        .setSubject(
            Objects.requireNonNullElse(
                parametersToUpdate.getSubject(),
                existingEvent.getSubject()))
        .setStartTime(
            Objects.nonNull(parametersToUpdate.getStartTime())
                ? (Objects.isNull(parametersToUpdate.getEndTime()) && existingEvent.getIsAllDay())
                ? parametersToUpdate.getStartTime().toLocalDate().atStartOfDay()
                : parametersToUpdate.getStartTime()
                : existingEvent.getStartTime())
        .setEndTime(
            Objects.nonNull(parametersToUpdate.getEndTime())
                ? parametersToUpdate.getEndTime()
                : Objects.nonNull(parametersToUpdate.getStartTime()) && existingEvent.getIsAllDay()
                    ? parametersToUpdate.getStartTime().toLocalDate().atStartOfDay().plusDays(1)
                    : existingEvent.getEndTime())
        .setLocation(
            Objects.nonNull(parametersToUpdate.getLocation())
                ? parametersToUpdate.getLocation()
                : existingEvent.getLocation())
        .setDescription(
            Objects.nonNull(parametersToUpdate.getDescription())
                ? parametersToUpdate.getDescription()
                : existingEvent.getDescription())
        .setIsPublic(
            Objects.requireNonNullElse(
                parametersToUpdate.getIsPublic(),
                existingEvent.getIsPublic()))
        .setIsAllDay(
            Objects.nonNull(parametersToUpdate.getEndTime())
                ? false
                : existingEvent.getIsAllDay())
        .setIsRecurring(
            Objects.requireNonNullElse(
                parametersToUpdate.getIsRecurring(),
                existingEvent.getIsRecurring()))
        .setRecurringDetails(
            Objects.isNull(parametersToUpdate.getRecurringDetails())
                ? existingEvent.getRecurringDetails()
                : RecurringDetailsDTO.getBuilder()
                    .setRepeatDays(
                        Objects.nonNull(parametersToUpdate.getRecurringDetails().getRepeatDays())
                            ? parametersToUpdate.getRecurringDetails().getRepeatDays()
                            : existingEvent.getRecurringDetails().getRepeatDays())
                    .setOccurrences(
                        Objects.nonNull(parametersToUpdate.getRecurringDetails().getOccurrences())
                            ? parametersToUpdate.getRecurringDetails().getOccurrences()
                            : Objects.nonNull(
                                parametersToUpdate.getRecurringDetails().getUntilDate())
                                ? null : existingEvent.getRecurringDetails().getOccurrences())
                    .setUntilDate(
                        Objects.nonNull(parametersToUpdate.getRecurringDetails().getUntilDate())
                            ? parametersToUpdate.getRecurringDetails().getUntilDate()
                            : Objects.nonNull(
                                parametersToUpdate.getRecurringDetails().getOccurrences())
                                ? null : existingEvent.getRecurringDetails().getUntilDate())
                    .build()
        );
  }

  /**
   * Get all events in the calendar on specified date.
   *
   * @param date date to get events on
   * @return list of events on the given date
   */
  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return eventRepository.getEventsOnDate(date);
  }

  /**
   * Get all events in the calendar on specified date range.
   *
   * @param startTime start time of the range
   * @param endTime   end time of the range
   * @return list of events in the given range
   */
  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsInRange(startTime, endTime);
  }

  @Override
  public List<EventDTO> getAllEvents() {
    return eventRepository.getAllEvents();
  }

  @Override
  public String exportEventsWithExporter(ICalendarExporter exporter) {
    List<EventDTO> events = eventRepository.getAllEvents();
    // If there are no events, throw a CalendarExportException
    if (events.isEmpty()) {
      throw new CalendarExportException("No events to export");
    }
    return exporter.export(events);
  }

  /**
   * Check if the user is busy at a given time.
   *
   * @param dateTime the dateTime to check if the user is busy
   * @return true if the user is busy at the given dateTime, false otherwise
   */
  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    return conflictDetector.hasConflict(dateTime);
  }
}
