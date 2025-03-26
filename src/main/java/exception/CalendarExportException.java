package exception;

/**
 * CalendarExportException is a custom exception thrown when an error occurs while exporting the
 * calendar.
 */
public class CalendarExportException extends RuntimeException {

  /**
   * Instantiates a new Calendar export exception.
   *
   * @param message the message
   */
  public CalendarExportException(String message) {
    super(message);
  }
}
