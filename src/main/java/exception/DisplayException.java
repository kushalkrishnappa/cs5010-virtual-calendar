package exception;

/**
 * DisplayException is a custom exception thrown when an error occurs while displaying the
 * calendar.
 */
public class DisplayException extends RuntimeException {

  /**
   * Instantiates a new Display exception.
   *
   * @param message the message
   */
  public DisplayException(String message) {
    super(message);
  }
}
