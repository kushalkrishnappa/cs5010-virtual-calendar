package dto;

import java.time.LocalDateTime;

/**
 * EventDTO class implements IEventDTO and store information about an event.
 */
public class EventDTO implements IEventDTO {

  private final String subject;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final String description;
  private final String location;
  private final Boolean isPublic;

  /**
   * Protected constructor for EventDTO. The object is created using the EventDTOBuilder class.
   *
   * @param subject The subject of the event
   * @param startTime The start time of the event
   * @param endTime The end time of the event
   * @param description The description of the event
   * @param location The location of the event
   * @param isPublic Whether the event is public or not
   */
  protected EventDTO(
      String subject,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String description,
      String location,
      Boolean isPublic) {
    this.subject = subject;
    this.startTime = startTime;
    this.endTime = endTime;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  /**
   * Returns a new EventDTOBuilder object. It is used to build an EventDTO object with the required
   * attributes recursively.
   *
   * @return eventDTOBuilder object
   */
  public static EventDTOBuilder getBuilder() {
    return new EventDTOBuilder();
  }

  /**
   * EventDTOBuilder class that builds an EventDTO object.
   */
  public static class EventDTOBuilder {

    protected String subject;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected String description;
    protected String location;
    protected Boolean isPublic;

    /**
     * Private empty constructor for EventDTOBuilder.
     */
    protected EventDTOBuilder() {
    }

    /**
     * Sets the subject of the event.
     *
     * @param subject The subject of the event
     * @return EventDTOBuilder object
     */
    public EventDTOBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime The start time of the event
     * @return EventDTOBuilder object
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
    public EventDTOBuilder setIsPublic(boolean isPublic) {
      this.isPublic = isPublic;
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
          this.isPublic
      );
    }
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStartTime() {
    return this.startTime;
  }

  @Override
  public LocalDateTime getEndTime() {
    return this.endTime;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public Boolean isPublic() {
    return this.isPublic;
  }
}
