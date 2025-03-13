package model;

import dto.EventDTO;
import dto.EventDTO.EventDTOBuilder;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;
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

  private static final DateTimeFormatter calenderExportDateFormatter = DateTimeFormatter.ofPattern(
      "MM/dd/yyyy");
  private static final DateTimeFormatter calenderExportTimeFormatter = DateTimeFormatter.ofPattern(
      "hh:mm a");
  private boolean isEditRequest;

  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline) {
    // check if it is a recurring event, check for conflicts inherently
    // check if it is an all day event, check for conflicts inherently
    // alas if autoDecline is set, check for conflicts (spanned events only)

    // Perform null check for required attributes (At least a start date is required for eventDTO)
    if (eventDTO.getStartTime() == null) {
      throw new InvalidDateTimeRangeException("Start time cannot be null");
    }

    // create a eventDTO with default values if not set
    eventDTO = createValidEventDTO(eventDTO);

    if (eventDTO.getIsAllDay() && eventDTO.getIsRecurring() && hasConflict(eventDTO.getStartTime(),
        eventDTO.getEndTime())) {
      throw new EventConflictException("All day recurring events has conflict");
    } else if (autoDecline && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime())) {
      throw new EventConflictException("Auto-declined events has conflict");
    }

    if (eventDTO.getIsAllDay()) {
      if (!eventDTO.getIsRecurring()) {
        createAllDayEvent(eventDTO);
        return;
      }
    } else {
      // spanned event
      if (!eventDTO.getIsRecurring()) {
        createSpannedEvent(eventDTO);
        return;
      }
      // spanned recurring event
      if (!eventDTO.getStartTime().toLocalDate().equals(eventDTO.getEndTime().toLocalDate())) {
        throw new IllegalArgumentException("Recurring spanned event cannot span more than a day");
      }


    }
    // recurring all day or spanned event
    List<EventDTO> eventDTOs = generateRecurrence(eventDTO);
    eventDTOs.forEach(eventRepository::insertEvent);
  }

  private static EventDTO createValidEventDTO(EventDTO eventDTO) {
    eventDTO = EventDTO.getBuilder()
        .setSubject(eventDTO.getSubject())
        .setDescription(eventDTO.getDescription())
        .setLocation(eventDTO.getLocation())
        .setIsPublic(Objects.requireNonNullElse(eventDTO.getIsPublic(), false))
        .setIsAllDay(!Objects.isNull(eventDTO.getIsAllDay()) && eventDTO.getIsAllDay())
        .setStartTime(
            Objects.isNull(eventDTO.getEndTime()) ? eventDTO.getStartTime().toLocalDate()
                .atStartOfDay() : eventDTO.getStartTime())
        .setEndTime(
            Objects.requireNonNullElse(eventDTO.getEndTime(),
                eventDTO.getStartTime().toLocalDate().atStartOfDay().plusDays(1)))
        .setIsRecurring(!Objects.isNull(eventDTO.getIsRecurring()) && eventDTO.getIsRecurring())
        .setRecurringDetails(eventDTO.getRecurringDetails())
        .build();
    return eventDTO;
  }

  private List<EventDTO> generateRecurrence(EventDTO eventDTO) {
    if (Objects.isNull(eventDTO.getRecurringDetails().getRepeatDays())) {
      throw new IllegalArgumentException("Recurring event must have repeat days");
    }
    if (Objects.isNull(eventDTO.getRecurringDetails().getOccurrences()) && Objects.isNull(
        eventDTO.getRecurringDetails().getUntilDate())) {
      throw new IllegalArgumentException(
          "Recurring event must have either occurrences or until date");
    }

    LocalDateTime startTime = eventDTO.getStartTime();
    LocalDateTime endTime = eventDTO.getEndTime();

    List<List<LocalDateTime>> recurringDateTimeRange = new ArrayList<>();

    TreeSet<DayOfWeek> daysOfWeek = CalendarDayOfWeek.getJavaTimeDaysOfWeek(
        eventDTO.getRecurringDetails().getRepeatDays());

    DayOfWeek currentDayOfWeek = startTime.getDayOfWeek();

    if (!Objects.isNull(eventDTO.getRecurringDetails().getOccurrences())) {
      loopUntilOccurrence(eventDTO, recurringDateTimeRange, currentDayOfWeek, daysOfWeek, startTime,
          endTime);
    } else {
      loopUntilDate(eventDTO, startTime, currentDayOfWeek, daysOfWeek, recurringDateTimeRange,
          endTime);
    }

    List<EventDTO> eventDTOs = new ArrayList<>();
    for (List<LocalDateTime> dateTimeRange : recurringDateTimeRange) {
      EventDTO recurringEvent = EventDTO.getBuilder().setSubject(eventDTO.getSubject())
          .setStartTime(dateTimeRange.get(0)).setEndTime(dateTimeRange.get(1))
          .setDescription(eventDTO.getDescription()).setLocation(eventDTO.getLocation())
          .setIsPublic(eventDTO.getIsPublic()).setIsAllDay(eventDTO.getIsAllDay())
          .setIsRecurring(true).setRecurringDetails(eventDTO.getRecurringDetails()).build();
      eventDTOs.add(recurringEvent);
    }
    return eventDTOs;
  }

  private void loopUntilDate(EventDTO eventDTO, LocalDateTime startTime, DayOfWeek currentDayOfWeek,
      TreeSet<DayOfWeek> daysOfWeek, List<List<LocalDateTime>> recurringDateTimeRange,
      LocalDateTime endTime) {

    LocalDateTime untilDate = eventDTO.getRecurringDetails().getUntilDate();
    if (eventDTO.getStartTime().isAfter(untilDate)) {
      throw new IllegalArgumentException("Until date cannot be before start time");
    }

    LocalDateTime currentDateTime = startTime;
    while (!currentDateTime.isAfter(untilDate)) {
      currentDayOfWeek = Objects.requireNonNullElse(daysOfWeek.ceiling(currentDayOfWeek),
          daysOfWeek.first());
      currentDateTime = currentDateTime.with(TemporalAdjusters.nextOrSame(currentDayOfWeek));

      if (!currentDateTime.isAfter(untilDate)) {
        checkConflictAndAdd(currentDateTime, recurringDateTimeRange, endTime,
            eventDTO.getIsAllDay());
      }
      currentDateTime = currentDateTime.plusDays(1);
      currentDayOfWeek = currentDayOfWeek.plus(1);
    }
  }

  private void loopUntilOccurrence(EventDTO eventDTO,
      List<List<LocalDateTime>> recurringDateTimeRange, DayOfWeek currentDayOfWeek,
      TreeSet<DayOfWeek> daysOfWeek, LocalDateTime startTime, LocalDateTime endTime) {
    LocalDateTime currentDateTime = startTime;
    int occurrences = eventDTO.getRecurringDetails().getOccurrences();
    while (recurringDateTimeRange.size() < occurrences) {
      currentDayOfWeek = Objects.requireNonNullElse(daysOfWeek.ceiling(currentDayOfWeek),
          daysOfWeek.first());
      currentDateTime = currentDateTime.with(TemporalAdjusters.nextOrSame(currentDayOfWeek));

      checkConflictAndAdd(currentDateTime, recurringDateTimeRange, endTime, eventDTO.getIsAllDay());
      currentDateTime = currentDateTime.plusDays(1);
      currentDayOfWeek = currentDayOfWeek.plus(1);
    }
  }

  private void createSpannedEvent(EventDTO eventDTO) {
    // create a single spanned event
    validateStartAndEndTimes(eventDTO);
    eventRepository.insertEvent(eventDTO);
  }

  private void createAllDayEvent(EventDTO eventDTO) {
    // create a single all day event
    // check if the endDate is null and set to start of next day
    eventRepository.insertEvent(eventDTO);
  }


  private void checkConflictAndAdd(LocalDateTime currentDateTime,
      List<List<LocalDateTime>> recurringDateTimeRange, LocalDateTime endTime, Boolean isAllDay) {
    if (isAllDay) {
      if (hasConflict(currentDateTime, currentDateTime.plusDays(1)) && !isEditRequest) {
        throw new EventConflictException("Recurring all day event with occurrences has conflict");
      }
      recurringDateTimeRange.add(List.of(currentDateTime, currentDateTime.plusDays(1)));
    } else {
      recurringDateTimeRange.add(
          Arrays.asList(currentDateTime, currentDateTime.with(endTime.toLocalTime())));
    }
  }

  private void validateStartAndEndTimes(EventDTO eventDTO) {
    if (eventDTO.getStartTime().isAfter(eventDTO.getEndTime())) {
      throw new InvalidDateTimeRangeException("Start time cannot be after end time");
    }
  }

  @Override
  public Integer editEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws EventConflictException, IllegalArgumentException {
    isEditRequest = true;
    if (Objects.isNull(parametersToUpdate)) {
      throw new IllegalArgumentException("Parameters to update cannot be null");
    }
    if (Objects.isNull(endTime)) {
      return editEventsWithName(eventName, startTime, parametersToUpdate);
    }
    isEditRequest = false;
    return editEventWithKey(eventName, startTime, endTime, parametersToUpdate);
  }

  private Integer editEventWithKey(String eventName, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) {
    int eventsUpdated = 0;
    EventDTO existingEvent = eventRepository.getEvent(eventName, startTime, endTime);
    if (Objects.isNull(existingEvent)) {
      throw new IllegalArgumentException("Event with name " + eventName + " not found");
    }

    // existingEvent to update found
    // simple field updates (no conflict check)
    EventDTOBuilder updatedEventBuilder = EventDTO.getBuilder().setSubject(
            Objects.requireNonNullElse(parametersToUpdate.getSubject(), existingEvent.getSubject()))
        .setStartTime(Objects.requireNonNullElse(parametersToUpdate.getStartTime(),
            existingEvent.getStartTime())).setEndTime(
            Objects.requireNonNullElse(parametersToUpdate.getEndTime(), existingEvent.getEndTime()))
        .setDescription(Objects.nonNull(parametersToUpdate.getDescription())
            ? parametersToUpdate.getDescription() : existingEvent.getDescription()).setLocation(
            Objects.nonNull(parametersToUpdate.getLocation()) ? parametersToUpdate.getLocation()
                : existingEvent.getLocation()).setIsPublic(
            Objects.nonNull(parametersToUpdate.getIsPublic()) ? parametersToUpdate.getIsPublic()
                : existingEvent.getIsPublic());

    // if it was previously an all day event
    setEditForAllDayEvent(parametersToUpdate, existingEvent, updatedEventBuilder);
    // changing recurring details
    if (Objects.nonNull(parametersToUpdate.getIsRecurring())
        && parametersToUpdate.getIsRecurring()) {
      // and if was already a part of recurring event
      if (existingEvent.getIsRecurring()) {
        throw new IllegalArgumentException(
            "Recurrence details cannot be updated for existing recurring event "
                + "(Conflict in updating this and following event OR all events in recurrence)");
      } else {
        // convert to recurring event
        updatedEventBuilder.setIsRecurring(true)
            .setRecurringDetails(parametersToUpdate.getRecurringDetails());
      }
    }
    createEvent(updatedEventBuilder.build(), false);
    // if create was successful
    eventRepository.deleteEvent(existingEvent.getSubject(), existingEvent.getStartTime(),
        existingEvent.getEndTime());
    return eventsUpdated;
  }

  private static boolean isKeyFieldsUpdated(EventDTO parametersToUpdate) {
    return Objects.nonNull(parametersToUpdate.getSubject()) || Objects.nonNull(
        parametersToUpdate.getStartTime()) || Objects.nonNull(parametersToUpdate.getEndTime());
  }

  private Integer editEventsWithName(String eventName, LocalDateTime startTime,
      EventDTO parametersToUpdate) {
    // find events to update
    List<EventDTO> eventsByName;
    eventsByName = eventRepository.getEventsByName(eventName);
    if (Objects.nonNull(startTime)) {
      eventsByName = eventsByName.stream()
          .filter(event -> startTime.isBefore(event.getStartTime()) && event.getIsRecurring())
          .collect(Collectors.toList());
    }
    if (eventsByName.isEmpty()) {
      throw new IllegalArgumentException("Event with name " + eventName + " not found");
    }

    EventDTO firstEventHitSinceStartTime = eventsByName.get(0);
    eventsByName = getEventsInHitSeries(firstEventHitSinceStartTime, eventsByName);

    // generate updated events while checking for conflicts
    List<EventDTO> eventsToUpdate = new ArrayList<>();

    // if recurrence details is not set
    if (Objects.isNull(parametersToUpdate.getIsRecurring())
        || !parametersToUpdate.getIsRecurring()) {
      for (EventDTO existingEvent : eventsByName) {
        EventDTOBuilder updatedEventBuilder = EventDTO.getBuilder().setSubject(
                Objects.requireNonNullElse(parametersToUpdate.getSubject(), existingEvent.getSubject()))
            .setStartTime(Objects.requireNonNullElse(parametersToUpdate.getStartTime(),
                existingEvent.getStartTime())).setEndTime(
                Objects.requireNonNullElse(parametersToUpdate.getEndTime(),
                    existingEvent.getEndTime())).setIsAllDay(existingEvent.getIsAllDay())
            .setIsRecurring(existingEvent.getIsRecurring())
            .setRecurringDetails(existingEvent.getRecurringDetails()).setDescription(
                Objects.nonNull(parametersToUpdate.getDescription())
                    ? parametersToUpdate.getDescription() : existingEvent.getDescription())
            .setLocation(
                Objects.nonNull(parametersToUpdate.getLocation()) ? parametersToUpdate.getLocation()
                    : existingEvent.getLocation()).setIsPublic(
                Objects.nonNull(parametersToUpdate.getIsPublic()) ? parametersToUpdate.getIsPublic()
                    : existingEvent.getIsPublic());

        // if it was previously an all day event
        setEditForAllDayEvent(parametersToUpdate, existingEvent, updatedEventBuilder);

        eventsToUpdate.add(updatedEventBuilder.build());
      }

      eventsToUpdate.forEach(event -> createEvent(event, false));
      eventsByName.forEach(
          event -> eventRepository.deleteEvent(event.getSubject(), event.getStartTime(),
              event.getEndTime()));
    } else {
      EventDTOBuilder newRecurEventBuilder = EventDTO.getBuilder().setSubject(eventName)
          .setStartTime(
              Objects.nonNull(parametersToUpdate.getStartTime()) ? parametersToUpdate.getStartTime()
                  : eventsByName.get(0).getStartTime()).setEndTime(
              Objects.nonNull(parametersToUpdate.getEndTime()) ? parametersToUpdate.getEndTime()
                  : eventsByName.get(0).getEndTime()).setIsRecurring(true).setRecurringDetails(
              RecurringDetailsDTO.getBuilder().setRepeatDays(
                      Objects.nonNull(parametersToUpdate.getRecurringDetails().getRepeatDays())
                          ? parametersToUpdate.getRecurringDetails().getRepeatDays()
                          : eventsByName.get(0).getRecurringDetails().getRepeatDays()).setOccurrences(
                      Objects.nonNull(parametersToUpdate.getRecurringDetails().getOccurrences())
                          ? parametersToUpdate.getRecurringDetails().getOccurrences()
                          : Objects.nonNull(parametersToUpdate.getRecurringDetails().getUntilDate())
                              ? null : eventsByName.get(0).getRecurringDetails().getOccurrences())
                  .setUntilDate(
                      Objects.nonNull(parametersToUpdate.getRecurringDetails().getUntilDate())
                          ? parametersToUpdate.getRecurringDetails().getUntilDate()
                          : Objects.nonNull(
                              parametersToUpdate.getRecurringDetails().getOccurrences()) ? null
                              : eventsByName.get(0).getRecurringDetails().getUntilDate()).build())
          .setDescription(Objects.nonNull(parametersToUpdate.getDescription())
              ? parametersToUpdate.getDescription() : eventsByName.get(0).getDescription())
          .setLocation(
              Objects.nonNull(parametersToUpdate.getLocation()) ? parametersToUpdate.getLocation()
                  : eventsByName.get(0).getLocation()).setIsPublic(
              Objects.nonNull(parametersToUpdate.getIsPublic()) ? parametersToUpdate.getIsPublic()
                  : eventsByName.get(0).getIsPublic());

      // if it was previously an all day event
      if (eventsByName.get(0).getIsAllDay()
          // and start or end time is requested to be updated
          && (Objects.nonNull(parametersToUpdate.getStartTime())) || Objects.nonNull(
          parametersToUpdate.getEndTime())) {
        newRecurEventBuilder.setIsAllDay(false);
      } else {
        newRecurEventBuilder.setIsAllDay(eventsByName.get(0).getIsAllDay());
      }

      List<EventDTO> newEventsToAdd = generateRecurrence(newRecurEventBuilder.build());
      eventsToUpdate.addAll(newEventsToAdd);
      eventsToUpdate.forEach(eventRepository::insertEvent);
      eventsByName.forEach(
          event -> eventRepository.deleteEvent(event.getSubject(), event.getStartTime(),
              event.getEndTime()));
    }
    return eventsToUpdate.size();
  }

  private void setEditForAllDayEvent(EventDTO parametersToUpdate, EventDTO existingEvent,
      EventDTOBuilder updatedEventBuilder) {
    if (existingEvent.getIsAllDay()) {
      // and start time is requested to be updated,
      // or end time is requested to be updated
      if (Objects.nonNull(parametersToUpdate.getStartTime())) {
        updatedEventBuilder.setStartTime(
            parametersToUpdate.getStartTime().toLocalDate().atStartOfDay());
        updatedEventBuilder.setEndTime(
            parametersToUpdate.getStartTime().toLocalDate().atStartOfDay().plusDays(1));
      } else if (Objects.nonNull(parametersToUpdate.getEndTime())) {
        updatedEventBuilder.setIsAllDay(false);
      }
    } else {
      updatedEventBuilder.setIsAllDay(existingEvent.getIsAllDay());
    }
  }

  private boolean isNotToBeUpdated(EventDTO conflictEvent, List<EventDTO> eventsToUpdate) {
    for (EventDTO existingEvent : eventsToUpdate) {
      if (conflictEvent.getSubject().equals(existingEvent.getSubject())
          && conflictEvent.getStartTime().equals(existingEvent.getStartTime())
          && conflictEvent.getEndTime().equals(existingEvent.getEndTime())) {
        return false;
      }
    }
    return true;
  }

  private List<EventDTO> getEventsInHitSeries(EventDTO firstHitEvent, List<EventDTO> eventsByName) {
    List<EventDTO> events = generateRecurrence(firstHitEvent);
    return eventsByName.stream().filter(event -> {
      for (EventDTO existingEvent : events) {
        if (existingEvent.getSubject().equals(event.getSubject()) && existingEvent.getStartTime()
            .equals(event.getStartTime()) && existingEvent.getEndTime()
            .equals(event.getEndTime())) {
          return true;
        }
      }
      return false;
    }).collect(Collectors.toList());
  }


  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return eventRepository.getEventsOnDate(date);
  }

  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsInRange(startTime, endTime);
  }

  private Boolean hasConflict(LocalDateTime startTime, LocalDateTime endTime) {
    List<EventDTO> hasEventsOverlapping = eventRepository.searchOverlaps(startTime, endTime);
    return !hasEventsOverlapping.isEmpty();
  }

  @Override
  public String exportToCSV(String fileName) throws CalendarExportException {
    // Get all events
    List<EventDTO> events = eventRepository.getAllEvents();

    // If there are no events, throw an CalendarExportException
    if (events.isEmpty()) {
      throw new CalendarExportException("No events to export");
    }

    // Define file path - if the file name does not end with .csv, add it
    String csvFilePath = fileName.endsWith(".csv") ? fileName : fileName + ".csv";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      // Write the header
      writer.write(getCSVHeader());
      writer.newLine();

      // Write each event as a row in CSV
      for (EventDTO event : events) {
        writer.write(String.join(",",
            // Subject
            escapeCSV(event.getSubject()),
            // Start Date
            event.getStartTime() != null ? event.getStartTime().format(calenderExportDateFormatter)
                : "",
            // Start Time
            event.getStartTime() != null && !event.getIsAllDay() ? event.getStartTime()
                .format(calenderExportTimeFormatter) : "",
            // End Date
            event.getEndTime() != null ? event.getEndTime().format(calenderExportDateFormatter)
                : "",
            // End Time
            event.getEndTime() != null && !event.getIsAllDay() ? event.getEndTime()
                .format(calenderExportTimeFormatter) : "",
            // All Day Event
            event.getIsAllDay() ? "True" : "False",
            // Description
            escapeCSV(event.getDescription()),
            // Location
            escapeCSV(event.getLocation()),
            // Private
            event.getIsPublic() ? "False" : "True"));
        writer.newLine();
      }
      return csvFilePath;
    } catch (IOException e) {
      throw new CalendarExportException("Error exporting to CSV");
    }
  }

  private static String getCSVHeader() {
    return "Subject," + "Start Date," + "Start Time," + "End Date," + "End Time," + "All Day Event,"
        + "Description," + "Location," + "Private";
  }

  private static String escapeCSV(String value) {
    // if the value is null or empty, return empty string
    if (value == null || value.isEmpty()) {
      return "";
    }
    // Replace each double quote with two double quotes to escape it correctly
    value = value.replace("\"", "");
    value = value.replace("\n", "");
    // Enclose the entire value in double quotes
    return "\"" + value + "\"";
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    List<EventDTO> eventsAtSpecifiedTime = eventRepository.getEventsAt(dateTime);
    return !eventsAtSpecifiedTime.isEmpty();
  }
}
