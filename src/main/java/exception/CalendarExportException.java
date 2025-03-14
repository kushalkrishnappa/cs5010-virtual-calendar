package exception;

/**
 * CalendarExportException is a custom exception that is thrown when an error occurs while exporting
 * the calendar.
 */
public class CalendarExportException extends RuntimeException {

  public CalendarExportException(String message) {
    super(message);
  }
}
