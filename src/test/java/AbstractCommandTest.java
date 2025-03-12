import controller.CalendarController;
import controller.ControllerMode;
import org.junit.Before;

public abstract class AbstractCommandTest {
  CalendarController controller;
  MockModel mockModel;

  @Before
  public void setUp() {
    mockModel = new MockModel();
  }

  String getDisplayMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getDisplayMessage();
  }

  String getErrorMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getErrorMessage();
  }
}
