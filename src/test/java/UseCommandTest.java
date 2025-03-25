import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for the use command.
 */
public class UseCommandTest extends AbstractCommandTest {

  @Test
  public void testEmptyUseCommand() {
    assertEquals("Invalid command format: use calendar --name <calendar_name> ...",
        getErrorMessageWithInput("use calendar"));
  }

  @Test
  public void testInvalidUseCommandWithoutCalendayKeyword() {
    assertEquals("Invalid command format: use calendar ...",
        getErrorMessageWithInput("use something"));
  }

  @Test
  public void testInvalidUseCommandWithoutNameFlag() {
    assertEquals("Invalid command format: use calendar --name ...",
        getErrorMessageWithInput("use calendar --something"));
  }

  @Test
  public void testInvalidUseCommandWithoutNameValue() {
    assertEquals("Invalid command format: use calendar --name <calendar_name> ...",
        getErrorMessageWithInput("use calendar --name"));
  }

  @Test
  public void testUseCommandWhenTheCalendatDoesNotExist() {
    assertEquals("Calendar with the provided name doesn't exists",
        getErrorMessageWithInput("use calendar --name \"no_calendar\""));
  }

}
