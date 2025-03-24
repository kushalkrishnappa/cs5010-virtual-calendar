package exception;

public class InvalidEventDetailsException extends RuntimeException {

  public InvalidEventDetailsException(String message) {
    super(message);
  }
}
