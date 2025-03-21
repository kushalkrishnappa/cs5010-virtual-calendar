import controller.CalendarController;
import controller.ControllerMode;
import java.util.function.Supplier;
import model.IModel;
import org.junit.Before;

/**
 * This is an abstract class for the command tests.
 */
public abstract class AbstractCommandTest {

  CalendarController controller;
  MockModel mockModel;
  Supplier<IModel> mockModelFactory;

  @Before
  public void setUp() {
    mockModel = new MockModel();
    mockModelFactory = () -> mockModel;
  }

  String getDisplayMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getDisplayMessage();
  }

  String getErrorMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getErrorMessage();
  }
}
