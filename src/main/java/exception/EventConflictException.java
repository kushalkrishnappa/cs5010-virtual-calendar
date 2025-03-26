package exception;

/**
 * EventConflictException is a custom exception thrown when an error occurs due to a conflict in
 * events.
 */
public class EventConflictException extends RuntimeException {

  /**
   * Instantiates a new Event conflict exception.
   *
   * @param message the message
   */
  public EventConflictException(String message) {
    super(message);
  }
}
