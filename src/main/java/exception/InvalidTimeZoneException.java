package exception;

public class InvalidTimeZoneException extends RuntimeException {

  public InvalidTimeZoneException(String message) {
    super(message);
  }
}
