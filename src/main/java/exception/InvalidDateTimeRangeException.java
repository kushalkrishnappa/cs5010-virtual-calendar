package exception;

/**
 * InvalidDateTimeRangeException is a custom exception thrown when an error occurs due to an invalid
 * date time range.
 */
public class InvalidDateTimeRangeException extends RuntimeException {

  /**
   * Instantiates a new Invalid date time range exception.
   *
   * @param message the message
   */
  public InvalidDateTimeRangeException(String message) {
    super(message);
  }
}
