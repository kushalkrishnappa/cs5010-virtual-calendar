package exception;

/**
 * InvalidDateTimeRangeException is a custom exception that is thrown when an error occurs due to an
 * invalid date time range.
 */
public class InvalidDateTimeRangeException extends RuntimeException {

  public InvalidDateTimeRangeException(String message) {
    super(message);
  }
}
