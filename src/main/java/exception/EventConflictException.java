package exception;

/**
 * EventConflictException is a custom exception that is thrown when an error occurs due to a
 * conflict in events.
 */
public class EventConflictException extends RuntimeException {

  public EventConflictException(String message) {
    super(message);
  }
}
