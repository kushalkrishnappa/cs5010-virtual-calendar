package exception;

/**
 * DisplayException is a custom exception that is thrown when an error occurs while displaying the
 * calendar.
 */
public class DisplayException extends RuntimeException {

  public DisplayException(String message) {
    super(message);
  }
}
