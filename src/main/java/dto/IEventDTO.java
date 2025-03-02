package dto;

import java.time.LocalDateTime;


/**
 * The interface representing the getter methods of an event DTO. This will enforce the required
 * attributes to be instantiated in the EventDTO class.
 */
public interface IEventDTO {

  /**
   * Returns the subject of the EventDTO object.
   *
   * @return the subject of the event
   */
  String getSubject();

  /**
   * Returns the start time of the EventDTO object.
   *
   * @return the start time of the event
   */
  LocalDateTime getStartTime();

  /**
   * Returns the end time of the EventDTO object.
   *
   * @return the end time of the event
   */
  LocalDateTime getEndTime();

  /**
   * Returns the description of the EventDTO object.
   *
   * @return the description of the event
   */
  String getDescription();

  /**
   * Returns the location of the EventDTO object.
   *
   * @return the location of the event
   */
  String getLocation();

  /**
   * Returns whether the EventDTO representing an event is public or not.
   *
   * @return true if the event is public, false otherwise
   */
  Boolean isPublic();
}
