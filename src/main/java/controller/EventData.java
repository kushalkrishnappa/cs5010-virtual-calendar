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
  public EventData(
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