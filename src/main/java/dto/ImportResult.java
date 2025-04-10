package dto;

/**
 * The Class is used by the {@link service.ICalendarImporter} to represent the result of importing
 * events from a reader.
 */
public class ImportResult {

  private final int successCount;
  private final int totalCount;
  private final String message; // Optional message for errors or summary

  /**
   * Instantiates a new Import result.
   *
   * @param successCount the success count
   * @param totalCount   the total count
   * @param message      the message
   */
  public ImportResult(int successCount, int totalCount, String message) {
    this.successCount = successCount;
    this.totalCount = totalCount;
    this.message = message;
  }

  /**
   * Gets the successfully processed events.
   *
   * @return the success count
   */
  public int getSuccessCount() {
    return successCount;
  }

  /**
   * Gets the total processed events.
   *
   * @return the total count
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * Gets the message sent by the importer.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Generates a standard summary message.
   *
   * @return a string summarizing the import results
   */
  public String generateSummary() {
    if (message != null && !message.isEmpty()) {
      return message;
    }
    if (totalCount == 0) {
      return "The selected file was empty or contained no valid data rows.";
    }
    return String.format("Successfully imported %d out of %d records.", successCount, totalCount);
  }

}
