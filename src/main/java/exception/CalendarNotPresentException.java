package exception;

/**
 * Thrown when the requested calendar does not exist.
 */
public class CalendarNotPresentException extends RuntimeException {

  /**
   * Instantiates a new Calendar not present exception.
   *
   * @param message the message
   */
  public CalendarNotPresentException(String message) {
    super(message);
  }
}
