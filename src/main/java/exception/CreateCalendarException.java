package exception;

/**
 * Thrown when there is error with creating a requested calendar.
 */
public class CreateCalendarException extends RuntimeException {

  /**
   * Instantiates a new Create calendar exception.
   *
   * @param message the message
   */
  public CreateCalendarException(String message) {
    super(message);
  }
}
