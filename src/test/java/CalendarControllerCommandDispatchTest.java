import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is a test suite for the CalendarController class.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CreateCommandTest.class,
    EditEventCommandTest.class,
    PrintEventsCommandTest.class,
    ShowStatusCommandTest.class,
    ExportCalendarCommandTest.class,})
public class CalendarControllerCommandDispatchTest {
  // the class remains empty.
}
