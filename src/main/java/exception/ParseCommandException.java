package exception;

/**
 * ParseCommandException is a custom exception thrown when an error occurs while parsing the
 * command.
 */
public class ParseCommandException extends Exception {

  /**
   * Instantiates a new Parse command exception.
   *
   * @param message the message
   */
  public ParseCommandException(String message) {
    super(message);
  }
}
