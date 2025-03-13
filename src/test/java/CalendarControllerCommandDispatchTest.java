import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CreateCommandTest.class,
    EditEventCommandTest.class,
    PrintEventsCommandTest.class,
    ShowStatusCommandTest.class,
    ExportCalendarCommandTest.class,})
public class CalendarControllerCommandDispatchTest {

}
