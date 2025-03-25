package model;

import dto.EventDTO;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

class RecurrenceService {
  // TODO: refactor
  static List<EventDTO> generateRecurrence(EventDTO eventDTO) {
    // check if recurring event has repeat days
    if (Objects.isNull(eventDTO.getRecurringDetails().getRepeatDays())) {
      throw new IllegalArgumentException("Recurring event must have repeat days");
    }
    if (Objects.isNull(eventDTO.getRecurringDetails().getOccurrences())
        && Objects.isNull(eventDTO.getRecurringDetails().getUntilDate())) {
      throw new IllegalArgumentException(
          "Recurring event must have either occurrences or until date");
    }
    if (Objects.nonNull(eventDTO.getRecurringDetails().getOccurrences())
        && Objects.nonNull(eventDTO.getRecurringDetails().getUntilDate())) {
      throw new IllegalArgumentException(
          "Recurring event must have either occurrences or until date");
    }

    // check if start time is before end time
    LocalDateTime startTime = eventDTO.getStartTime();
    LocalDateTime endTime = eventDTO.getEndTime();

    // check if start time is before end time
    List<List<LocalDateTime>> recurringDateTimeRange = new ArrayList<>();
    TreeSet<DayOfWeek> daysOfWeek = CalendarDayOfWeek.getJavaTimeDaysOfWeek(
        eventDTO.getRecurringDetails().getRepeatDays());
    DayOfWeek currentDayOfWeek = startTime.getDayOfWeek();

    // check if start time is before end time
    if (!Objects.isNull(eventDTO.getRecurringDetails().getOccurrences())) {
      loopUntilOccurrence(eventDTO, recurringDateTimeRange, currentDayOfWeek, daysOfWeek, startTime,
          endTime);
    } else {
      loopUntilDate(eventDTO, startTime, currentDayOfWeek, daysOfWeek, recurringDateTimeRange,
          endTime);
    }

    // create a list of events from the recurring event
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
          .setRecurringDetails(eventDTO.getRecurringDetails()).build();
      eventDTOs.add(recurringEvent);
    }
    return eventDTOs;
  }


  /**
   * Loop until the date of the recurring event.
   *
   * @param eventDTO               the event to be created
   * @param startTime              the start time
   * @param currentDayOfWeek       the current day of the week
   * @param daysOfWeek             the days of the week
   * @param recurringDateTimeRange the recurring date time range
   * @param endTime                the end time
   */
  private static void loopUntilDate(EventDTO eventDTO, LocalDateTime startTime,
      DayOfWeek currentDayOfWeek,
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
        addRecurrence(currentDateTime, recurringDateTimeRange, endTime,
            eventDTO.getIsAllDay());
      }
      currentDateTime = currentDateTime.plusDays(1);
      currentDayOfWeek = currentDayOfWeek.plus(1);
    }
  }

  /**
   * Loop until the occurrence of the recurring event.
   *
   * @param eventDTO               the event to be created
   * @param recurringDateTimeRange the recurring date time range
   * @param currentDayOfWeek       the current day of the week
   * @param daysOfWeek             the days of the week
   * @param startTime              the start time
   * @param endTime                the end time
   */
  private static void loopUntilOccurrence(EventDTO eventDTO,
      List<List<LocalDateTime>> recurringDateTimeRange, DayOfWeek currentDayOfWeek,
      TreeSet<DayOfWeek> daysOfWeek, LocalDateTime startTime, LocalDateTime endTime) {
    LocalDateTime currentDateTime = startTime;
    int occurrences = eventDTO.getRecurringDetails().getOccurrences();
    while (recurringDateTimeRange.size() < occurrences) {
      currentDayOfWeek = Objects.requireNonNullElse(daysOfWeek.ceiling(currentDayOfWeek),
          daysOfWeek.first());
      currentDateTime = currentDateTime.with(TemporalAdjusters.nextOrSame(currentDayOfWeek));

      addRecurrence(currentDateTime, recurringDateTimeRange, endTime, eventDTO.getIsAllDay());
      currentDateTime = currentDateTime.plusDays(1);
      currentDayOfWeek = currentDayOfWeek.plus(1);
    }
  }


  /**
   * Check for conflict and add the event to the list of recurringDateTimeRange.
   *
   * @param currentDateTime        currentDateTime
   * @param recurringDateTimeRange recurringDateTimeRange
   * @param endTime                endTime
   * @param isAllDay               isAllDay
   */
  private static void addRecurrence(LocalDateTime currentDateTime,
      List<List<LocalDateTime>> recurringDateTimeRange, LocalDateTime endTime, Boolean isAllDay) {
    if (isAllDay) {
      recurringDateTimeRange.add(List.of(currentDateTime, currentDateTime.plusDays(1)));
    } else {
      recurringDateTimeRange.add(
          Arrays.asList(currentDateTime, currentDateTime.with(endTime.toLocalTime())));
    }
  }

}
