package controller;

import java.time.LocalDateTime;

/**
 * Class to store event data transfer between controller and view.
 */
public class EventData {

  private final String subject;

  private final LocalDateTime startTime;

  private final LocalDateTime endTime;

  private final String description;

  private final String location;

  private final Boolean isPublic;

  private final Boolean isAllDay;

  private final Boolean isRecurring;

  private final RecurrenceData recurringDetails;

  /**
   * Constructor for EventData.
   *
   * @param subject          the subject of the event
   * @param startTime        the start time of the event
   * @param endTime          the end time of the event
   * @param description      the description of the event
   * @param location         the location of the event
   * @param isPublic         whether the event is public or not
   * @param isAllDay         whether the event is all day or not
   * @param isRecurring      whether the event is recurring or not
   * @param recurringDetails the recurring details of the event
   */
  private EventData(
      String subject,
      LocalDateTime startTime,
      LocalDateTime endTime,
      String description,
      String location,
      Boolean isPublic,
      Boolean isAllDay,
      Boolean isRecurring,
      RecurrenceData recurringDetails) {
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

  /**
   * Gets builder.
   *
   * @return the builder
   */
  public static EventDataBuilder getBuilder() {
    return new EventDataBuilder();
  }

  /**
   * The Event data builder.
   */
  public static class EventDataBuilder {

    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String location;
    private Boolean isPublic;
    private Boolean isAllDay;
    private Boolean isRecurring;
    private RecurrenceData recurringDetails;

    private EventDataBuilder() {
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
     * Sets subject.
     *
     * @param subject the subject
     * @return the subject
     */
    public EventDataBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     * @return the start time
     */
    public EventDataBuilder setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     * @return the end time
     */
    public EventDataBuilder setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets description.
     *
     * @param description the description
     * @return the description
     */
    public EventDataBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets location.
     *
     * @param location the location
     * @return the location
     */
    public EventDataBuilder setLocation(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets is public.
     *
     * @param isPublic the is public
     * @return the is public
     */
    public EventDataBuilder setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
      return this;
    }

    /**
     * Sets is all day.
     *
     * @param isAllDay the is all day
     * @return the is all day
     */
    public EventDataBuilder setIsAllDay(Boolean isAllDay) {
      this.isAllDay = isAllDay;
      return this;
    }

    /**
     * Sets is recurring.
     *
     * @param isRecurring the is recurring
     * @return the is recurring
     */
    public EventDataBuilder setIsRecurring(Boolean isRecurring) {
      this.isRecurring = isRecurring;
      return this;
    }

    /**
     * Sets recurring details.
     *
     * @param recurringDetails the recurring details
     * @return the recurring details
     */
    public EventDataBuilder setRecurringDetails(RecurrenceData recurringDetails) {
      this.recurringDetails = recurringDetails;
      return this;
    }

    /**
     * Build event data.
     *
     * @return the event data
     */
    public EventData build() {
      return new EventData(
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
   * Gets subject.
   *
   * @return the subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets start time.
   *
   * @return the start time
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }

  /**
   * Gets end time.
   *
   * @return the end time
   */
  public LocalDateTime getEndTime() {
    return endTime;
  }

  /**
   * Gets description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets location.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Gets public.
   *
   * @return the public
   */
  public Boolean getPublic() {
    return isPublic;
  }

  /**
   * Gets all day.
   *
   * @return the all day
   */
  public Boolean getAllDay() {
    return isAllDay;
  }

  /**
   * Gets recurring.
   *
   * @return the recurring
   */
  public Boolean getRecurring() {
    return isRecurring;
  }

  /**
   * Gets recurring details.
   *
   * @return the recurring details
   */
  public RecurrenceData getRecurringDetails() {
    return recurringDetails;
  }
}