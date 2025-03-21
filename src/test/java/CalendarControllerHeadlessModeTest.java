import static org.junit.Assert.assertEquals;

import controller.CalendarController;
import controller.ControllerMode;
import java.util.function.Supplier;
import model.IModel;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a test class for the CalendarController class in headless mode.
 */
public class CalendarControllerHeadlessModeTest {
  MockModel mockModel;
  Supplier<IModel> mockModelFactory;
  CalendarController controller;

  @Before
  public void setUp() {
    mockModel = new MockModel();
    mockModelFactory = () -> mockModel;
  }

  @Test
  public void emptyFile() {
    MockView mockView = new MockView("");
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.HEADLESS);
    controller.run();
    assertEquals("exit command was not specified in the passed file\n",
        mockView.displayErrorMessage.toString());
    assertEquals("",
        mockView.displayMessage.toString());
  }

  @Test
  public void errorInCommand() {
    MockView mockView = new MockView("invalid command");
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.HEADLESS);
    controller.run();
    assertEquals("Unknown command\n",
        mockView.displayErrorMessage.toString());
    assertEquals("",
        mockView.displayMessage.toString());
  }

  @Test
  public void exitCommandNotSpecifiedInFile() {
    mockModel.setIsBusyReturn = true;
    MockView mockView = new MockView("show status on 2025-10-21T12:00\n");
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.HEADLESS);
    controller.run();
    assertEquals("exit command was not specified in the passed file\n",
        mockView.displayErrorMessage.toString());
    assertEquals("",
        mockView.displayMessage.toString());
  }
}
