package exception;

/**
 * Thrown when the details of an event is invalid or in conflict with the expectations.
 */
public class InvalidEventDetailsException extends RuntimeException {

  /**
   * Instantiates a new Invalid event details exception.
   *
   * @param message the message
   */
  public InvalidEventDetailsException(String message) {
    super(message);
  }
}
