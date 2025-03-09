package model;

import dto.EventDTO;
import dto.EventDTO.EventDTOBuilder;
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

  enum mode {
    CREATE,
    EDIT;
  }

  private static final DateTimeFormatter calenderExportDateFormatter = DateTimeFormatter.ofPattern(
      "MM/dd/yyyy");
  private static final DateTimeFormatter calenderExportTimeFormatter = DateTimeFormatter.ofPattern(
      "hh:mm a");

  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline) {
    // check if it is a recurring event, check for conflicts inherently
    // check if it is an all day event, check for conflicts inherently
    // alas if autoDecline is set, check for conflicts (spanned events only)
//    if ((eventDTO.getIsRecurring() && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))
//        || (eventDTO.getIsAllDay() && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))
//        || (autoDecline && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))) {
//      throw new IllegalArgumentException("Event has conflict");
//    }

    if (eventDTO.getStartTime() == null) {
      throw new InvalidDateTimeRangeException("Start time cannot be null");
    }
    eventDTO = EventDTO.getBuilder()
        .setSubject(eventDTO.getSubject())
        .setDescription(eventDTO.getDescription())
        .setLocation(eventDTO.getLocation())
        .setIsPublic(Objects.requireNonNullElse(eventDTO.getIsPublic(), false))
        .setIsAllDay(Objects.isNull(eventDTO.getEndTime()))
        .setStartTime(Objects.isNull(eventDTO.getEndTime())
            ? eventDTO.getStartTime().toLocalDate().atStartOfDay()
            : eventDTO.getStartTime())
        .setEndTime(Objects.requireNonNullElse(eventDTO.getEndTime(),
            eventDTO.getStartTime().toLocalDate().atStartOfDay().plusDays(1)))
        .setIsRecurring(eventDTO.getIsRecurring())
        .setRecurringDetails(eventDTO.getRecurringDetails())
        .build();

    if (eventDTO.getIsAllDay() && eventDTO.getIsRecurring()
        && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime())) {
      throw new EventConflictException("All day recurring events has conflict");
    } else if (autoDecline
        && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime())) {
      throw new EventConflictException("Auto-declined events has conflict");
    }

    if (eventDTO.getIsAllDay()) {
      if (!eventDTO.getIsRecurring()) {
        createAllDayEvent(eventDTO);
        return;
      }
      // all day recurring event
//      eventDTO = EventDTO.getBuilder()
//          .setSubject(eventDTO.getSubject())
//          .setStartTime(eventDTO.getStartTime().toLocalDate().atStartOfDay())
//          .setEndTime(eventDTO.getStartTime().plusDays(1))
//          .setDescription(eventDTO.getDescription())
//          .setLocation(eventDTO.getLocation())
//          .setIsPublic(eventDTO.getIsPublic())
//          .setIsAllDay(true)
//          .setIsRecurring(eventDTO.getIsRecurring())
//          .setRecurringDetails(eventDTO.getRecurringDetails())
//          .build();
    } else {
      // spanned event
      if (!eventDTO.getIsRecurring()) {
        createSpannedEvent(eventDTO);
        return;
      }
      // spanned recurring event
      if (!eventDTO.getStartTime().toLocalDate()
          .equals(eventDTO.getEndTime().toLocalDate())) {
        throw new IllegalArgumentException("Recurring spanned event cannot span more than a day");
      }
    }
    // recurring all day or spanned event
    List<EventDTO> eventDTOs = generateRecurrence(eventDTO);
//    eventDTOs.forEach(eventRepository::insertEvent);

    for (EventDTO event : eventDTOs) {
      eventRepository.insertEvent(event);
    }
  }

  private List<EventDTO> generateRecurrence(EventDTO eventDTO) {
    if (Objects.isNull(eventDTO.getRecurringDetails().getRepeatDays())) {
      throw new IllegalArgumentException("Recurring event must have repeat days");
    }
    if (Objects.isNull(eventDTO.getRecurringDetails().getOccurrences())
        && Objects.isNull(eventDTO.getRecurringDetails().getUntilDate())) {
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
      EventDTO recurringEvent = EventDTO.getBuilder()
          .setSubject(eventDTO.getSubject())
          .setStartTime(dateTimeRange.get(0))
          .setEndTime(dateTimeRange.get(1))
          .setDescription(eventDTO.getDescription())
          .setLocation(eventDTO.getLocation())
          .setIsPublic(eventDTO.getIsPublic())
          .setIsAllDay(eventDTO.getIsAllDay())
          .setIsRecurring(true)
          .setRecurringDetails(eventDTO.getRecurringDetails())
          .build();
      eventDTOs.add(recurringEvent);
    }
    return eventDTOs;
  }

  private void loopUntilDate(EventDTO eventDTO, LocalDateTime startTime,
      DayOfWeek currentDayOfWeek, TreeSet<DayOfWeek> daysOfWeek,
      List<List<LocalDateTime>> recurringDateTimeRange, LocalDateTime endTime) {

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
      currentDayOfWeek = currentDayOfWeek.plus(1);
    }
  }

  private void loopUntilOccurrence(EventDTO eventDTO,
      List<List<LocalDateTime>> recurringDateTimeRange,
      DayOfWeek currentDayOfWeek, TreeSet<DayOfWeek> daysOfWeek, LocalDateTime startTime,
      LocalDateTime endTime) {
    LocalDateTime currentDateTime = startTime;
    int occurrences = eventDTO.getRecurringDetails().getOccurrences();
    while (recurringDateTimeRange.size() < occurrences) {
      currentDayOfWeek = Objects.requireNonNullElse(daysOfWeek.ceiling(currentDayOfWeek),
          daysOfWeek.first());
      currentDateTime = currentDateTime.with(TemporalAdjusters.nextOrSame(currentDayOfWeek));

      checkConflictAndAdd(currentDateTime, recurringDateTimeRange, endTime, eventDTO.getIsAllDay());
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
      if (hasConflict(currentDateTime, currentDateTime.plusDays(1))) {
        throw new EventConflictException("Recurring all day event with occurrences has conflict");
      }
      recurringDateTimeRange.add(List.of(currentDateTime, currentDateTime.plusDays(1)));
    } else {
      recurringDateTimeRange.add(
          List.of(currentDateTime, currentDateTime.with(endTime.toLocalTime())));
    }
  }

  private void validateStartAndEndTimes(EventDTO eventDTO) {
    if (eventDTO.getStartTime().isAfter(eventDTO.getEndTime())) {
      throw new InvalidDateTimeRangeException("Start time cannot be after end time");
    }
  }

  @Override
  public Integer editEvent(String eventName, LocalDateTime startTime, LocalDateTime endTime,
      EventDTO parametersToUpdate) throws IllegalArgumentException {
    if (Objects.isNull(parametersToUpdate)) {
      throw new IllegalArgumentException("Parameters to update cannot be null");
    }
    if (Objects.isNull(endTime)) {
      return editEventsWithName(eventName, startTime, parametersToUpdate);
    }
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
    EventDTOBuilder updatedEventBuilder = EventDTO.getBuilder()
        .setSubject(existingEvent.getSubject())
        .setStartTime(existingEvent.getStartTime())
        .setEndTime(existingEvent.getEndTime())
        .setIsAllDay(existingEvent.getIsAllDay())
        .setIsRecurring(existingEvent.getIsRecurring())
        .setRecurringDetails(existingEvent.getRecurringDetails())
        .setDescription(
            Objects.requireNonNullElse(parametersToUpdate.getDescription(),
                existingEvent.getDescription()))
        .setLocation(
            Objects.requireNonNullElse(parametersToUpdate.getLocation(),
                existingEvent.getLocation()))
        .setIsPublic(
            Objects.requireNonNullElse(parametersToUpdate.getIsPublic(),
                existingEvent.getIsPublic()));

    // changing key fields (conflict checks)
    if (isKeyFieldsUpdated(parametersToUpdate)) {
      String updateSubject = Objects.requireNonNullElse(parametersToUpdate.getSubject(),
          existingEvent.getSubject());
      LocalDateTime updateStart = Objects.requireNonNullElse(parametersToUpdate.getStartTime(),
          existingEvent.getStartTime());
      LocalDateTime updateEnd = Objects.requireNonNullElse(parametersToUpdate.getEndTime(),
          existingEvent.getEndTime());

      // check conflict
      EventDTO conflictEvent = eventRepository.getEvent(updateSubject, updateStart, updateEnd);
      if (Objects.nonNull(conflictEvent)) {
        throw new EventConflictException(
            "Update key parameters conflict with existing event");
      }
      updatedEventBuilder
          .setSubject(updateSubject)
          .setStartTime(updateStart)
          .setEndTime(updateEnd);

      // if it was previously an all day event
      if (existingEvent.getIsAllDay()
          && Objects.isNull(parametersToUpdate.getSubject())) {
        updatedEventBuilder.setIsAllDay(false);
      } else {
        updatedEventBuilder.setIsAllDay(existingEvent.getIsAllDay());
      }

    }

    // changing recurring details
    if (parametersToUpdate.getIsRecurring()) {
      if (existingEvent.getIsRecurring()) {
        throw new IllegalArgumentException(
            "Recurrence details cannot be updated for existing recurring event "
                + "(Conflict in updating this and following event OR all events in recurrence)");
      } else {
        // not an existing recurring event
        eventRepository.deleteEvent(
            existingEvent.getSubject(),
            existingEvent.getStartTime(),
            existingEvent.getEndTime());
        try {
          createEvent(updatedEventBuilder
              .setIsRecurring(true)
              .setRecurringDetails(parametersToUpdate.getRecurringDetails())
              .build(), true);
          eventsUpdated = Objects.requireNonNullElse(
              parametersToUpdate.getRecurringDetails().getOccurrences(),
              1);
        } catch (EventConflictException | IllegalArgumentException e) {
          eventRepository.insertEvent(existingEvent);
          throw new EventConflictException("New Recurring events conflict with existing event");
        }
      }
    } else {
      // spanned or all day event (conflict checked)
      eventRepository.deleteEvent(
          existingEvent.getSubject(),
          existingEvent.getStartTime(),
          existingEvent.getEndTime()
      );
      eventRepository.insertEvent(updatedEventBuilder.build());
      eventsUpdated = 1;
    }

    return eventsUpdated;
  }

  private static boolean isKeyFieldsUpdated(EventDTO parametersToUpdate) {
    return Objects.nonNull(parametersToUpdate.getSubject())
        || Objects.nonNull(parametersToUpdate.getStartTime())
        || Objects.nonNull(parametersToUpdate.getEndTime());
  }

  private Integer editEventsWithName(String eventName, LocalDateTime startTime,
      EventDTO parametersToUpdate) {
    // find events to update
    List<EventDTO> eventsByName;
    eventsByName = eventRepository.getEventsByName(eventName);
    if (Objects.nonNull(startTime)) {
      eventsByName = eventsByName.stream()
          .filter(event -> !startTime.isBefore(event.getStartTime())
              && !event.getIsRecurring())
          .collect(Collectors.toList());
    }
    if (eventsByName.isEmpty()) {
      throw new IllegalArgumentException("Event with name " + eventName + " not found");
    }

    // generate updated events while checking for conflicts
    List<EventDTO> eventsToUpdate = new ArrayList<>();

    // if recurrence details is not set
    if (Objects.isNull(parametersToUpdate.getIsRecurring())
        || !parametersToUpdate.getIsRecurring()) {
      for (EventDTO existingEvent : eventsByName) {
        EventDTOBuilder updatedEventBuilder = EventDTO.getBuilder()
            .setSubject(existingEvent.getSubject())
            .setStartTime(existingEvent.getStartTime())
            .setEndTime(existingEvent.getEndTime())
            .setIsAllDay(existingEvent.getIsAllDay())
            .setIsRecurring(existingEvent.getIsRecurring())
            .setRecurringDetails(existingEvent.getRecurringDetails())
            .setDescription(
                Objects.requireNonNullElse(parametersToUpdate.getDescription(),
                    existingEvent.getDescription()))
            .setLocation(
                Objects.requireNonNullElse(parametersToUpdate.getLocation(),
                    existingEvent.getLocation()))
            .setIsPublic(
                Objects.requireNonNullElse(parametersToUpdate.getIsPublic(),
                    existingEvent.getIsPublic()));
        // if key values are updated?
        if (isKeyFieldsUpdated(parametersToUpdate)) {
          String updateSubject = Objects.requireNonNullElse(parametersToUpdate.getSubject(),
              existingEvent.getSubject());
          LocalDateTime updateStart = Objects.requireNonNullElse(parametersToUpdate.getStartTime(),
              existingEvent.getStartTime());
          LocalDateTime updateEnd = Objects.requireNonNullElse(parametersToUpdate.getEndTime(),
              existingEvent.getEndTime());

          // check conflict
          EventDTO conflictEvent = eventRepository.getEvent(updateSubject, updateStart, updateEnd);
          if (Objects.nonNull(conflictEvent)) {
            throw new EventConflictException(
                "Update key parameters conflict with existing event");
          }

          updatedEventBuilder
              .setSubject(updateSubject)
              .setStartTime(updateStart)
              .setEndTime(updateEnd);

          // if it was previously an all day event
          if (existingEvent.getIsAllDay()
              && Objects.isNull(parametersToUpdate.getSubject())) {
            updatedEventBuilder.setIsAllDay(false);
          } else {
            updatedEventBuilder.setIsAllDay(existingEvent.getIsAllDay());
          }

        }

        eventsToUpdate.add(updatedEventBuilder.build());
        eventsByName.forEach(event -> eventRepository.deleteEvent(
            event.getSubject(), event.getStartTime(), event.getEndTime()));
        eventsToUpdate.forEach(eventRepository::insertEvent);
      }
    } else {
      // recurrence details is set
      EventDTO firstEventInSeries = EventDTO.getBuilder()
          .setSubject(eventName)
          .setStartTime(eventsByName.get(0).getStartTime())
          .setEndTime(eventsByName.get(0).getEndTime())
          .setIsAllDay(eventsByName.get(0).getIsAllDay())
          .setIsRecurring(true)
          .setRecurringDetails(parametersToUpdate.getRecurringDetails())
          .setDescription(
              Objects.requireNonNullElse(parametersToUpdate.getDescription(),
                  parametersToUpdate.getDescription()))
          .setLocation(
              Objects.requireNonNullElse(parametersToUpdate.getLocation(),
                  parametersToUpdate.getLocation()))
          .setIsPublic(
              Objects.requireNonNullElse(parametersToUpdate.getIsPublic(),
                  parametersToUpdate.getIsPublic()))
          .build();
      eventsByName.forEach(event -> eventRepository.deleteEvent(
          event.getSubject(), event.getStartTime(), event.getEndTime()));
      try {
        eventsToUpdate = generateRecurrence(firstEventInSeries);
      } catch (EventConflictException | IllegalArgumentException e) {
        eventsByName.forEach(eventRepository::insertEvent);
        throw new EventConflictException("New Recurring event conflict with existing event");
      }
      eventsToUpdate.forEach(eventRepository::insertEvent);
    }
    return eventsToUpdate.size();
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
            event.getIsPublic() ? "False" : "True"
        ));
        writer.newLine();
      }
      return csvFilePath;
    } catch (IOException e) {
      throw new CalendarExportException("Error exporting to CSV");
    }
  }

  private static String getCSVHeader() {
    return "Subject,"
        + "Start Date,"
        + "Start Time,"
        + "End Date,"
        + "End Time,"
        + "All Day Event,"
        + "Description,"
        + "Location,"
        + "Private";
  }

  private static String escapeCSV(String value) {
    // if the value is null or empty, return empty string
    if (value == null || value.isEmpty()) {
      return "";
    }
    // Replace each double quote with two double quotes to escape it correctly
    value = value.replace("\"", "\"\"");
    value = value.replace("\n", "");
    // Enclose the entire value in double quotes
    return "\"" + value + "\"";
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
