import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import org.junit.Test;

public class EditCalendarCommandTest extends AbstractCommandTest {

  @Test
  public void testInvalidEmptyCommand() {
    assertEquals("Please specify calendar name and property to update",
        getErrorMessageWithInput("edit calendar"));
  }

  @Test
  public void testInvalidCommandMissingNameFlag() {
    assertEquals("Please specify calendar name and property to update",
        getErrorMessageWithInput("edit calendar defaultCalendar --property timezone Asia/Tokyo"));
  }

  @Test
  public void testInvalidCommandMissingPropertyFlag() {
    assertEquals("Please specify calendar name and property to update",
        getErrorMessageWithInput("edit calendar --name default timezone Asia/Tokyo"));
  }

  @Test
  public void testInvalidCommandWithoutName() {
    assertEquals("Invalid command format: create calendar --name <name>",
        getErrorMessageWithInput("edit calendar --name"));
  }

  @Test
  public void testInvalidPropertyName() {
    assertEquals("Invalid property name",
        getErrorMessageWithInput("edit calendar --name defaultCalendar --property invalidProperty value"));
  }

  @Test
  public void testEditCalendarNameWithQuotes() {
    String command = "create calendar --name \"defaultCalendar\" --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "edit calendar --name \"defaultCalendar\" --property name \"My Updated Calendar\""
        + System.lineSeparator()
        + "use calendar --name \"My Updated Calendar\"";

    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockView.displayMessage.toString().contains("Created calendar defaultCalendar"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to My Updated Calendar"));
  }

}
