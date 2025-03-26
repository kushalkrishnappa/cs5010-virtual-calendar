import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This is a test class for the CopyEventCommand. It tests the parsing logic of the copy command.
 */
public class CopyEventCommandTest extends AbstractCommandTest {

  private final String fullCopyCommand =
      "Invalid command format: copy (event|events) (eventName on|on|between) "
          + "(sourceStartDateTime|sourceStartDateTime|<sourceStartDate> and <sourceEndDate>) "
          + "--target <targetCalendarName> to (targetStartDateTime|targetStartDate)";

  // test the invalid command format
  // valid command: `copy (event|events)`

  @Test
  public void testInvalidEmptyCommand() {
    assertEquals(fullCopyCommand, getErrorMessageWithInput("copy"));
  }

  @Test
  public void testInvalidCommandFormat() {
    assertEquals("Invalid command format: copy (event|events) ...",
        getErrorMessageWithInput("copy something"));
  }

  // test copy single event command - `copy event`

  @Test
  public void testInvalidCopySingleEventNoName() {
    assertEquals("Invalid command format: copy event <eventName> ...",
        getErrorMessageWithInput("copy event"));
  }

  @Test
  public void testInvalidCopySingleEventNoOn() {
    assertEquals(fullCopyCommand,
        getErrorMessageWithInput("copy event \"Meeting\""));
  }

  @Test
  public void testInvalidCopySingleEventWithKeywordNotOn() {
    assertEquals("Invalid command format: copy event <eventName> on ...",
        getErrorMessageWithInput("copy event \"Meeting\" something_but_on"));
  }

  @Test
  public void testInvalidCopySingleEventInvalidDateTime() {
    assertEquals("Invalid datetime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("copy event \"Meeting\" on 2025/04/01-12:00"));
  }

  @Test
  public void testInvalidCopySingleEventNoTarget() {
    assertEquals(fullCopyCommand,
        getErrorMessageWithInput("copy event \"Meeting\" on 2025-04-01T12:00"));
  }

  @Test
  public void testInvalidCopySingleEventWithIncorrectTargetFlag() {
    assertEquals("Invalid command format: Missing --target flag",
        getErrorMessageWithInput(
            "copy event \"Meeting\" on 2025-04-01T12:00 --invalid_target_flag"));
  }

  @Test
  public void testInvalidCopySingleEventNoTargetName() {
    assertEquals("Invalid command format: --target <calendarName> ...",
        getErrorMessageWithInput("copy event \"Meeting\" on 2025-04-01T12:00 --target"));
  }

  @Test
  public void testInvalidCopySingleEventNoTo() {
    assertEquals(fullCopyCommand,
        getErrorMessageWithInput("copy event \"Meeting\" on 2025-04-01T12:00 --target \"Work\""));
  }

  @Test
  public void testInvalidCopySingleEventWithKeywordNotTo() {
    assertEquals("Invalid command format: --target <calendarName> to ...",
        getErrorMessageWithInput(
            "copy event \"Meeting\" on 2025-04-01T12:00 --target \"Work\" something_but_to"));
  }

  @Test
  public void testInvalidCopySingleEventInvalidTargetDateTime() {
    assertEquals("Invalid datetime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput(
            "copy event \"Meeting\" on 2025-04-01T12:00 --target \"Work\" to 2025/04/08T14:00"));
  }

  // test copy multiple events command - `copy events`

  @Test
  public void testInvalidCopyMultipleEventsNoOn() {
    assertEquals(fullCopyCommand, getErrorMessageWithInput("copy events"));
  }

  @Test
  public void testInvalidCopyMultipleEventsWithKeywordNotOn() {
    assertEquals("Invalid command format: copy events (on|between) ...",
        getErrorMessageWithInput("copy events something_but_on"));
  }

  @Test
  public void testInvalidCopyEventsOnDateWithIncorrectTargetFlag() {
    assertEquals("Invalid command format: Missing --target flag",
        getErrorMessageWithInput(
            "copy events on 2025-04-01 --invalid_target_flag"));
  }

  @Test
  public void testInvalidCopyMultipleEventsWithKeywordNotBetween() {
    assertEquals("Invalid command format: copy events (on|between) ...",
        getErrorMessageWithInput("copy events \"Meeting\" something_but_between"));
  }

  @Test
  public void testInvalidCopyMultipleEventsNoDateTime() {
    assertEquals(fullCopyCommand, getErrorMessageWithInput("copy events on"));
  }

  @Test
  public void testInvalidCopyEventsOnDateInvalidDate() {
    assertEquals("Invalid date format: yyyy-MM-dd",
        getErrorMessageWithInput("copy events on 2025/04/01"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesInvalidStartDate() {
    assertEquals("Invalid date format: yyyy-MM-dd",
        getErrorMessageWithInput("copy events between 2025/04/01"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesNoAnd() {
    assertEquals(
        "Invalid command format: copy events between <startDateString> and <endDateString> ...",
        getErrorMessageWithInput("copy events between 2025-04-01 to"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesInvalidEndDate() {
    assertEquals("Invalid date format: yyyy-MM-dd",
        getErrorMessageWithInput("copy events between 2025-04-01 and 2025/04/07"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesNoTarget() {
    assertEquals(fullCopyCommand,
        getErrorMessageWithInput("copy events between 2025-04-01 and 2025-04-07"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesWithIncorrectTargetFlag() {
    assertEquals("Invalid command format: Missing --target flag",
        getErrorMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-07 --invalid_target_flag"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesNoTargetName() {
    assertEquals("Invalid command format: --target <calendarName> ...",
        getErrorMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-07 --target"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesNoTo() {
    assertEquals(fullCopyCommand,
        getErrorMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-07 --target \"Work\""));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesWithKeywordNotTo() {
    assertEquals("Invalid command format: --target <calendarName> to ...",
        getErrorMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-07 --target \"Work\" something_but_to"));
  }

  @Test
  public void testInvalidCopyEventsBetweenDatesInvalidTargetDate() {
    assertEquals("Invalid date format: yyyy-MM-dd",
        getErrorMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-07 --target \"Work\" to 2025/04/08"));
  }

  // execute command tests

  @Test
  public void testTargetCalendarNotFound() {
    mockModel.shouldThrowCalendarExportException = true;
    assertEquals("Target calendar not found: \"Work\"",
        getErrorMessageWithInput("copy event \"Meeting\" on 2025-04-01T12:00 "
            + "--target \"Work\" to 2025-04-08T14:00"));
  }

}
