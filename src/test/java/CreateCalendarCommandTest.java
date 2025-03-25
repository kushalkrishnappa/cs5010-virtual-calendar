import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import org.junit.Test;

public class CreateCalendarCommandTest extends AbstractCommandTest {

  String fullInvalidCommand = "Invalid command format: create calendar "
      + "--name <calName> "
      + "--timezone area/location";

  @Test
  public void testInvalidEmptyCommand() {
    assertEquals("Invalid command format: create (calendar|event) ...",
        getErrorMessageWithInput("create"));
  }

  @Test
  public void testInvalidCommandWithoutCalendarName() {
    assertEquals("Please Specify calendarName and zoneId",
        getErrorMessageWithInput("create calendar"));
  }

  @Test
  public void testInvalidCommandWithoutZoneID() {
    assertEquals("Please Specify calendarName and zoneId",
        getErrorMessageWithInput("create calendar --name calendarName"));
  }

  @Test
  public void testInvalidCommandMissingName() {
    assertEquals("Invalid command format: create calendar --name <name>",
        getErrorMessageWithInput("create calendar --name"));
  }

  @Test
  public void testInvalidCommandMissingNameFlag() {
    assertEquals(fullInvalidCommand,
        getErrorMessageWithInput("create calendar calendarName --timezone Asia/Kolkata"));
  }

  @Test
  public void testInvalidCommandMissingTimeZoneFlag() {
    assertEquals(fullInvalidCommand,
        getErrorMessageWithInput("create calendar --name calendarName Asia/Kolkata"));
  }


  @Test
  public void testInvalidCommandNoName() {
    assertEquals(fullInvalidCommand,
        getErrorMessageWithInput("create calendar --name --timezone Asia/Kolkata"));
  }

  @Test
  public void testInvalidCommandNoTimezone() {
    assertEquals("Invalid command format: create (calendar|event) ...",
        getErrorMessageWithInput("create calendar --name calendarName --timezone"));
  }

  @Test
  public void testInvalidTimezone() {
    assertEquals("Invalid timezone specified for time zone InvalidTimeZone",
        getErrorMessageWithInput("create calendar --name calendarName --timezone InvalidTimeZone"));
  }

  @Test
  public void testInvalidTimezoneFormat() {
    assertEquals("Expected timezone in IANA TZ format: \"area/location\" ",
        getErrorMessageWithInput("create calendar --name calendarName --timezone +area/location"));
  }

  @Test
  public void testCalendarAlreadyExists() {
    // since `default` calendar is already created, we can use it to test
    assertEquals("Calendar with the provided name already exists",
        getErrorMessageWithInput("create calendar --name default --timezone Asia/Kolkata"));
  }

  @Test
  public void testValidCreateCalendarCommand() {
    String calendarName = "testCalendar";
    String timezone = "Asia/Kolkata";

    assertEquals("Created calendar " + calendarName,
        getDisplayMessageWithInput(
            "create calendar --name " + calendarName + " --timezone " + timezone));
  }

  @Test
  public void testCreateCalendarWithQuotedName() {
    String calendarName = "\"My Test Calendar\"";
    String timezone = "Europe/London";

    assertEquals("Created calendar My Test Calendar",
        getDisplayMessageWithInput(
            "create calendar --name " + calendarName + " --timezone " + timezone));
  }

  @Test
  public void testCreateMultipleCalendars() {
    // Create first calendar
    assertEquals("Created calendar calendar1",
        getDisplayMessageWithInput("create calendar --name calendar1 --timezone Asia/Kolkata"));

    // Create second calendar
    assertEquals("Created calendar calendar2",
        getDisplayMessageWithInput("create calendar --name calendar2 --timezone Europe/London"));

    // Create third calendar
    assertEquals("Created calendar calendar3",
        getDisplayMessageWithInput("create calendar --name calendar3 --timezone America/New_York"));
  }


  @Test
  public void testCreateAndUseCalendar() {

    String createCalendarCommand = "create calendar --name testCalendar --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "use calendar --name testCalendar";

    MockView mockView = new MockView(createCalendarCommand + "\n", true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockView.displayMessage.toString().contains("Created calendar testCalendar"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to testCalendar"));
  }

}
