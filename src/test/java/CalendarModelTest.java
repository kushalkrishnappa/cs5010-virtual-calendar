import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is a test suite for the CalendarController class.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CreateEventTest.class,
    EditEventWithKeyTest.class,
    EditEventWithNameTest.class})
public class CalendarModelTest {
  // the class remains empty
}
