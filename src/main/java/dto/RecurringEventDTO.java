package dto;

import model.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * RecurringEventDTO class implements IRecurringEventDTO and store information about a recurring
 * event.
 */
public class RecurringEventDTO extends EventDTO implements IRecurringEventDTO {

  private final Integer occurrences;
  private final Set<DayOfWeek> repeatDays;
  private final LocalDateTime untilDate;

  /**
   * Protected constructor for EventDTO. The object is created using the EventDTOBuilder class.
   *
   * @param subject     The subject of the event
   * @param startTime   The start time of the event
   * @param endTime     The end time of the event
   * @param description The description of the event
   * @param location    The location of the event
   * @param isPublic    Whether the event is public or not
   */
  private RecurringEventDTO(String subject, LocalDateTime startTime,
      LocalDateTime endTime, String description, String location, Boolean isPublic, Integer occurrences,
      Set<DayOfWeek> repeatDays, LocalDateTime untilDate) {
    super(subject, startTime, endTime, description, location, isPublic);
    if (repeatDays != null
    || (occurrences != null && untilDate == null)
    || (untilDate != null && occurrences == null)) {
      throw new IllegalArgumentException("Cannot set both occurrences and untilDate");
    }
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  /**
   * EventDTOBuilder class that builds an EventDTO object.
   */
  public static class Builder<T extends Builder<T>> extends EventDTO.Builder<T> {

    private Integer occurrences;
    private Set<DayOfWeek> repeatDays;
    private LocalDateTime untilDate;


    /**
     * Sets the number of occurrences of the event.
     *
     * @param occurrences The number of occurrences of the event
     * @return the EventDTOBuilder object
     */
    public T setOccurrences(int occurrences) {
      this.occurrences = occurrences;
      return self();
    }

    /**
     * Sets the repeat days of the event.
     *
     * @param repeatDays The repeat days of the event
     * @return the EventDTOBuilder object
     */
    public T setRepeatDays(Set<DayOfWeek> repeatDays) {
      this.repeatDays = repeatDays;
      return self();
    }

    /**
     * Sets the end date of the event.
     *
     * @param untilDate The end date of the event
     * @return the EventDTOBuilder object
     */
    public T setUntilDate(LocalDateTime untilDate) {
      this.untilDate = untilDate;
      return self();
    }

    /**
     * Builds the EventDTO object with the required attributes.
     *
     * @return the EventDTO object
     */
    public RecurringEventDTO build() {
      return new RecurringEventDTO(
          this.subject,
          this.startTime,
          this.endTime,
          this.description,
          this.location,
          this.isPublic,
          this.occurrences,
          this.repeatDays,
          this.untilDate);
    }
  }

  @Override
  public int getOccurrences() {
    return this.occurrences;
  }

  @Override
  public Set<DayOfWeek> getRepeatDays() {
    return this.repeatDays;
  }

  @Override
  public LocalDateTime getUntilDate() {
    return this.untilDate;
  }
}
