package exception;

/**
 * ParseCommandException is a custom exception that is thrown when an error occurs while parsing the
 * command.
 */
public class ParseCommandException extends Exception {

  public ParseCommandException(String message) {
    super(message);
  }
}
