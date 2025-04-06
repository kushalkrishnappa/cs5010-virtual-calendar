package controller;

import java.time.LocalDateTime;

/**
 * Class to store event data
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

  public static EventDataBuilder getBuilder() {
    return new EventDataBuilder();
  }

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

    public EventDataBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public EventDataBuilder setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    public EventDataBuilder setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    public EventDataBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    public EventDataBuilder setLocation(String location) {
      this.location = location;
      return this;
    }

    public EventDataBuilder setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
      return this;
    }

    public EventDataBuilder setIsAllDay(Boolean isAllDay) {
      this.isAllDay = isAllDay;
      return this;
    }

    public EventDataBuilder setIsRecurring(Boolean isRecurring) {
      this.isRecurring = isRecurring;
      return this;
    }

    public EventDataBuilder setRecurringDetails(RecurrenceData recurringDetails) {
      this.recurringDetails = recurringDetails;
      return this;
    }

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

  public String getSubject() {
    return subject;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public Boolean getPublic() {
    return isPublic;
  }

  public Boolean getAllDay() {
    return isAllDay;
  }

  public Boolean getRecurring() {
    return isRecurring;
  }

  public RecurrenceData getRecurringDetails() {
    return recurringDetails;
  }

}