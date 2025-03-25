package model;

import dto.EventDTO;
import exception.InvalidDateTimeRangeException;
import exception.InvalidEventDetailsException;
import java.util.Objects;

class EventValidator {

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
