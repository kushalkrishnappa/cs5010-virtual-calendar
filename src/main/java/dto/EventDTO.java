package dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * EventDTO class implements IEventDTO and store information about an event.
 */
public class EventDTO {

  private final String subject;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final String description;
  private final String location;
  private final Boolean isPublic;
  private final Boolean isAllDay;
  private final Boolean isRecurring;
  private final RecurringDetailsDTO recurringDetails;

  /**
   * Protected constructor for EventDTO. The object is created using the EventDTOBuilder class.
   *
   * @param subject          The subject of the event
   * @param startTime        The start time of the event
   * @param endTime          The end time of the event
   * @param description      The description of the event
   * @param location         The location of the event
   * @param isPublic         Whether the event is public or not
   * @param isAllDay         Whether the event is all day or not
   * @param isRecurring      Whether the event is recurring or not
   * @param recurringDetails The recurring details of the event
   */
  private EventDTO(
      String subject,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String description,
      String location,
      Boolean isPublic,
      Boolean isAllDay,
      Boolean isRecurring,
      RecurringDetailsDTO recurringDetails) {
    this.subject = subject;
    this.startTime = startTime;
    this.endTime = endTime;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
    this.isAllDay = isAllDay;
    this.isRecurring = isRecurring;
    this.recurringDetails = recurringDetails;
  }

  public static EventDTOBuilder getBuilder() {
    return new EventDTOBuilder();
  }

  /**
   * EventDTOBuilder class that builds an EventDTO object.
   */
  public static class EventDTOBuilder {

    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String location;
    private Boolean isPublic;
    private Boolean isAllDay;
    private Boolean isRecurring;
    private RecurringDetailsDTO recurringDetails;

    private EventDTOBuilder() {
      this.subject = null;
      this.startTime = null;
      this.endTime = null;
      this.description = null;
      this.location = null;
      this.isPublic = null;
      this.isAllDay = null;
      this.isRecurring = null;
      this.recurringDetails = null;
    }

    /**
     * Sets the subject of the event.
     *
     * @param subject The subject of the event
     * @return Builder object
     */
    public EventDTOBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime The start time of the event
     * @return builder object
     */
    public EventDTOBuilder setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime The end time of the event
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the description of the event.
     *
     * @param description The description of the event
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location The location of the event
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setLocation(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets whether the event is public or not.
     *
     * @param isPublic Whether the event is public or not
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
      return this;
    }

    /**
     * Sets whether the event is all day or not.
     *
     * @param isAllDay Whether the event is all day or not
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setIsAllDay(Boolean isAllDay) {
      this.isAllDay = isAllDay;
      return this;
    }

    /**
     * Sets whether the event is recurring or not.
     *
     * @param isRecurring Whether the event is recurring or not
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setIsRecurring(Boolean isRecurring) {
      this.isRecurring = isRecurring;
      return this;
    }

    /**
     * Sets the recurring details of the event.
     *
     * @param recurringDetails The recurring details of the event
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setRecurringDetails(RecurringDetailsDTO recurringDetails) {
      this.recurringDetails = recurringDetails;
      return this;
    }

    /**
     * Builds an EventDTO object with attributes from EventDTOBuilder class.
     *
     * @return EventDTO object
     */
    public EventDTO build() {
      return new EventDTO(
          this.subject,
          this.startTime,
          this.endTime,
          this.description,
          this.location,
          this.isPublic,
          this.isAllDay,
          this.isRecurring,
          this.recurringDetails);
    }
  }

  /**
   * Gets the subject of the event.
   *
   * @return The subject of the event
   */
  public String getSubject() {
    return this.subject;
  }

  /**
   * Gets the start time of the event.
   *
   * @return The start time of the event
   */
  public LocalDateTime getStartTime() {
    return this.startTime;
  }

  /**
   * Gets the end time of the event.
   *
   * @return The end time of the event
   */
  public LocalDateTime getEndTime() {
    return this.endTime;
  }

  /**
   * Gets the description of the event.
   *
   * @return The description of the event
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Gets the location of the event.
   *
   * @return The location of the event
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * Gets whether the event is public or not.
   *
   * @return Whether the event is public or not
   */
  public Boolean getIsPublic() {
    return this.isPublic;
  }

  /**
   * Gets whether the event is all day or not.
   *
   * @return Whether the event is all day or not
   */
  public Boolean getIsAllDay() {
    return this.isAllDay;
  }

  /**
   * Gets whether the event is recurring or not.
   *
   * @return Whether the event is recurring or not
   */
  public Boolean getIsRecurring() {
    return this.isRecurring;
  }

  /**
   * Gets the recurring details of the event.
   *
   * @return The recurring details of the event
   */
  public RecurringDetailsDTO getRecurringDetails() {
    return this.recurringDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventDTO)) {
      return false;
    }
    EventDTO eventDTO = (EventDTO) o;
    return Objects.equals(subject, eventDTO.subject)
        && Objects.equals(startTime, eventDTO.startTime)
        && Objects.equals(endTime, eventDTO.endTime)
        && Objects.equals(description, eventDTO.description)
        && Objects.equals(location, eventDTO.location)
        && Objects.equals(isPublic, eventDTO.isPublic)
        && Objects.equals(isAllDay, eventDTO.isAllDay)
        && Objects.equals(isRecurring, eventDTO.isRecurring)
        && Objects.equals(recurringDetails, eventDTO.recurringDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, startTime, endTime, description, location, isPublic, isAllDay,
        isRecurring, recurringDetails);
  }

}
