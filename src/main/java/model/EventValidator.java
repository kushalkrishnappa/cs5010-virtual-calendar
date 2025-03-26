package model;

import dto.EventDTO;
import exception.InvalidDateTimeRangeException;
import exception.InvalidEventDetailsException;
import java.util.Objects;

/**
 * This class provides static methods for validating the details of an {@link EventDTO} to ensure
 * data integrity before creating or updating events.
 */
class EventValidator {

  /**
   * Validates the core properties of an {@link EventDTO}.
   *
   * <p>This method checks for null start times, valid start and end time relationships for
   * non-all-day events, and proper configuration of recurring event details.
   *
   * @param eventDTO the {@link EventDTO} to be validated
   * @throws InvalidDateTimeRangeException if the start or end times are invalid
   * @throws InvalidEventDetailsException  if the recurring event details are invalid
   */
  static void validateEvent(EventDTO eventDTO) {
    // Start time is not null
    if (eventDTO.getStartTime() == null) {
      throw new InvalidDateTimeRangeException("Start time cannot be null");
    }

    // End time is not null for non all day event and is after start time otherwise
    if (Objects.isNull(eventDTO.getEndTime())) {
      if (Objects.nonNull(eventDTO.getIsAllDay()) && !eventDTO.getIsAllDay()) {
        throw new InvalidDateTimeRangeException("End time cannot be null for non all day event");
      }
    } else if (eventDTO.getStartTime().isAfter(eventDTO.getEndTime())) {
      throw new InvalidDateTimeRangeException("Start time cannot be after end time");
    }

    // recurring details are properly set
    if (Objects.nonNull(eventDTO.getIsRecurring()) && eventDTO.getIsRecurring()) {
      if (Objects.isNull(eventDTO.getRecurringDetails())) {
        throw new InvalidEventDetailsException(
            "Recurring details is null for event marked as recurring");
      } else if (Objects.nonNull(eventDTO.getEndTime())
          && !eventDTO.getStartTime().toLocalDate().isEqual(eventDTO.getEndTime().toLocalDate())
          && !(Objects.nonNull(eventDTO.getIsAllDay()) && eventDTO.getIsAllDay())) {
        throw new InvalidEventDetailsException(
            "Start and end time must be on same day for event marked as recurring");
      }
    }

  }

  /**
   * Creates a valid {@link EventDTO} by setting default values for certain fields if they are
   * null.
   *
   * <p>This method ensures that essential fields like {@code isPublic}, {@code isAllDay}, and
   * {@code endTime} are properly initialized.
   *
   * @param eventDTO rhe original {@link EventDTO}
   * @return a new {@link EventDTO} with default values set
   */
  static EventDTO createValidEvent(EventDTO eventDTO) {
    eventDTO = EventDTO.getBuilder()
        .setSubject(eventDTO.getSubject())
        .setDescription(eventDTO.getDescription())
        .setLocation(eventDTO.getLocation())
        .setIsPublic(Objects.requireNonNullElse(eventDTO.getIsPublic(), false))
        .setIsAllDay(
            Objects.nonNull(eventDTO.getIsAllDay())
                ? eventDTO.getIsAllDay()
                : Objects.isNull(eventDTO.getEndTime())
        )
        .setStartTime(
            Objects.isNull(eventDTO.getEndTime()) ? eventDTO.getStartTime().toLocalDate()
                .atStartOfDay() : eventDTO.getStartTime())
        .setEndTime(
            Objects.requireNonNullElse(eventDTO.getEndTime(),
                eventDTO.getStartTime().toLocalDate().atStartOfDay().plusDays(1)))
        .setIsRecurring(Objects.nonNull(eventDTO.getIsRecurring()) && eventDTO.getIsRecurring())
        .setRecurringDetails(eventDTO.getRecurringDetails())
        .build();
    return eventDTO;
  }

}
