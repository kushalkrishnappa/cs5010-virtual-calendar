package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
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

  public CalendarModel() {
    this.eventRepository = new InMemoryEventRepository();
  }

  public static void main(String[] args) {
    CalendarModel model = new CalendarModel();
    EventDTO event1 = EventDTO.getBuilder()
        .setSubject("event1\n\n\n,     -     &^#8  \"")
        .setStartTime(LocalDateTime.parse("2025-03-11T10:00:00"))
        .setEndTime(LocalDateTime.parse("2025-03-11T11:00:00"))
        .setIsAllDay(false)
        .setIsPublic(true)
        .build();

    EventDTO event2 = EventDTO.getBuilder()
        .setSubject("event2,\"my time\"\n")
        .setStartTime(LocalDateTime.parse("2025-03-10T10:00:00"))
        .setEndTime(LocalDateTime.parse("2025-03-10T12:00:00"))
        .setIsAllDay(false)
        .setIsPublic(true)
        .build();

    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("all day event")
        .setStartTime(LocalDateTime.parse("2025-03-12T00:00:00"))
        .setEndTime(LocalDateTime.parse("2025-03-13T00:00:00"))
        .setIsAllDay(true)
        .setIsPublic(true)
        .build();

    model.eventRepository.insertEvent(event1);
    model.eventRepository.insertEvent(event2);
    model.eventRepository.insertEvent(allDayEvent);

    System.out.println(model.exportToCSV("/Users/kushalkrishnappa/Desktop/calendar1.csv"));

    System.out.println(model.isBusy(LocalDateTime.parse("2025-03-11T23:59:59")));
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline) {
    // check if it is a recurring event, check for conflicts inherently
    // check if it is an all day event, check for conflicts inherently
    // alas if autoDecline is set, check for conflicts (spanned events only)
    if ((eventDTO.getIsRecurring() && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))
        || (eventDTO.getIsAllDay() && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))
        || (autoDecline && hasConflict(eventDTO.getStartTime(), eventDTO.getEndTime()))) {
      throw new IllegalArgumentException("Event has conflict");
    }

    if (eventDTO.getIsAllDay()) {
      if (!eventDTO.getIsRecurring()) {
        createAllDayEvent(eventDTO);
        return;
      }
      // all day recurring event
      eventDTO = EventDTO.getBuilder()
          .setSubject(eventDTO.getSubject())
          .setStartTime(eventDTO.getStartTime().toLocalDate().atStartOfDay())
          .setEndTime(eventDTO.getStartTime().plusDays(1))
          .setDescription(eventDTO.getDescription())
          .setLocation(eventDTO.getLocation())
          .setIsPublic(eventDTO.getIsPublic())
          .setIsAllDay(true)
          .setIsRecurring(eventDTO.getIsRecurring())
          .setRecurringDetails(eventDTO.getRecurringDetails())
          .build();
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
    generateRecurrence(eventDTO);

  }

  private void generateRecurrence(EventDTO eventDTO) {
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

    for (List<LocalDateTime> dateTimeRange : recurringDateTimeRange) {
      EventDTO recurringEvent = EventDTO.getBuilder()
          .setSubject(eventDTO.getSubject())
          .setStartTime(dateTimeRange.get(0))
          .setEndTime(dateTimeRange.get(1))
          .setDescription(eventDTO.getDescription())
          .setLocation(eventDTO.getLocation())
          .setIsPublic(eventDTO.getIsPublic())
          .setIsAllDay(true)
          .setIsRecurring(true)
          .setRecurringDetails(eventDTO.getRecurringDetails())
          .build();
      eventRepository.insertEvent(recurringEvent);
    }

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
    if (eventDTO.getEndTime() == null) {
      // create a new event with the same details but with the end time set to start of next day
      EventDTO allDayEvent = EventDTO.getBuilder()
          .setSubject(eventDTO.getSubject())
          .setStartTime(eventDTO.getStartTime().toLocalDate().atStartOfDay())
          .setEndTime(eventDTO.getStartTime().plusDays(1))
          .setDescription(eventDTO.getDescription())
          .setLocation(eventDTO.getLocation())
          .setIsPublic(eventDTO.getIsPublic())
          .setIsAllDay(true)
          .setIsRecurring(eventDTO.getIsRecurring())
          .setRecurringDetails(eventDTO.getRecurringDetails())
          .build();
      eventRepository.insertEvent(allDayEvent);
    }
  }


  private void checkConflictAndAdd(LocalDateTime currentDateTime,
      List<List<LocalDateTime>> recurringDateTimeRange, LocalDateTime endTime, Boolean isAllDay) {
    if (isAllDay) {
      if (hasConflict(currentDateTime, currentDateTime.plusDays(1))) {
        throw new EventConflictException("Recurring all day event with occurrences has conflict");
      }
      recurringDateTimeRange.add(List.of(currentDateTime, currentDateTime.plusDays(1)));
    } else {
      if (hasConflict(currentDateTime, currentDateTime.with(endTime.toLocalTime()))) {
        throw new EventConflictException("Recurring all day event with occurrences has conflict");
      }
      recurringDateTimeRange.add(
          List.of(currentDateTime, currentDateTime.with(endTime.toLocalTime())));
    }
  }

  private void validateStartAndEndTimes(EventDTO eventDTO) {
    if (eventDTO.getStartTime() == null) {
      throw new IllegalArgumentException("Start time cannot be null");
    }
    if (eventDTO.getEndTime() == null) {
      throw new IllegalArgumentException("End time cannot be null");
    }
    if (eventDTO.getStartTime().isAfter(eventDTO.getEndTime())) {
      throw new IllegalArgumentException("Start time cannot be after end time");
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
