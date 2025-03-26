package exception;

/**
 * Thrown when no calendar is under use.
 */
public class CalendarNotSelectedException extends RuntimeException {

  /**
   * Instantiates a new Calendar not selected exception.
   *
   * @param message the message
   */
  public CalendarNotSelectedException(String message) {
    super(message);
  }
}
