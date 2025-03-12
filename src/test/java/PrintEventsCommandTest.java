import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrintEventsCommandTest extends AbstractCommandTest {

  @Test
  public void invalidEmptyCommand() {
    assertEquals("Invalid command format: print events "
            + "(on <dateTime>|from <startDateTime> to <endDateTime>)",
        getErrorMessageWithInput("print"));
  }

  @Test
  public void invalidEvent() {
    assertEquals("Invalid command format: print events ...",
        getErrorMessageWithInput("print event "));
  }

  @Test
  public void invalidEventDuring() {
    assertEquals("Invalid command format: print events (on|from) ...",
        getErrorMessageWithInput("print events during on|from"));
  }

  @Test
  public void invalidOnDate() {
    assertEquals("Invalid onDate format: yyyy-MM-dd",
        getErrorMessageWithInput("print events on 2025/10/21"));
  }

  @Test
  public void invalidStartDateTime() {
    assertEquals("Invalid startDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("print events from 2025/10/21T00:00:00"));
  }

  @Test
  public void invalidStartDateTimeDuring() {
    assertEquals("Invalid command format: print events from <dateStringTtimeString> to ...",
        getErrorMessageWithInput("print events from 2025-10-21T00:00 during"));
  }

  @Test
  public void invalidEndDateTime() {
    assertEquals("Invalid endDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("print events from 2025-10-21T00:00 to 2025-1021T00:00:00"));
  }

}
