package dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * RecurringEventDTO class implements IRecurringEventDTO and store information about a recurring
 * event.
 */
public class RecurringEventDTO extends EventDTO implements IRecurringEventDTO {

  private final int occurrences;
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
      LocalDateTime endTime, String description, String location, Boolean isPublic, int occurrences,
      Set<DayOfWeek> repeatDays, LocalDateTime untilDate) {
    super(subject, startTime, endTime, description, location, isPublic);
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  /**
   * Returns a new EventDTOBuilder object. It is used to build an EventDTO object with the required
   * attributes recursively.
   *
   * @return eventDTOBuilder object
   */
  public static RecurringEventDTOBuilder getBuilder() {
    return new RecurringEventDTOBuilder();
  }

  /**
   * EventDTOBuilder class that builds an EventDTO object.
   */
  public static class RecurringEventDTOBuilder extends EventDTOBuilder {

    private int occurrences;
    private Set<DayOfWeek> repeatDays;
    private LocalDateTime endDate;

    /**
     * Protected constructor for RecurringEventDTOBuilder with super instantiation of
     * EventDTOBuilder.
     */
    private RecurringEventDTOBuilder() {
      super();
    }

    /**
     * Sets the number of occurrences of the event.
     *
     * @param occurrences The number of occurrences of the event
     * @return the EventDTOBuilder object
     */
    public RecurringEventDTOBuilder setOccurrences(int occurrences) {
      this.occurrences = occurrences;
      return this;
    }

    /**
     * Sets the repeat days of the event.
     *
     * @param repeatDays The repeat days of the event
     * @return the EventDTOBuilder object
     */
    public RecurringEventDTOBuilder setRepeatDays(Set<DayOfWeek> repeatDays) {
      this.repeatDays = repeatDays;
      return this;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate The end date of the event
     * @return the EventDTOBuilder object
     */
    public RecurringEventDTOBuilder setEndDate(LocalDateTime endDate) {
      this.endDate = endDate;
      return this;
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
          this.endDate);
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
