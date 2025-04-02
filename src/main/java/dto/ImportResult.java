package dto;

public class ImportResult {

  private final int successCount;
  private final int totalCount;
  private final String message; // Optional message for errors or summary

  public ImportResult(int successCount, int totalCount, String message) {
    this.successCount = successCount;
    this.totalCount = totalCount;
    this.message = message;
  }

  public int getSuccessCount() {
    return successCount;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public String getMessage() {
    return message;
  }

  /**
   * Generates a standard summary message.
   *
   * @return A string summarizing the import results.
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
