package exception;

/**
 * Thrown when invalid time zone is encountered.
 */
public class InvalidTimeZoneException extends RuntimeException {

  /**
   * Instantiates a new Invalid time zone exception.
   *
   * @param message the message
   */
  public InvalidTimeZoneException(String message) {
    super(message);
  }
}
