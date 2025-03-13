import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShowStatusCommandTest extends AbstractCommandTest {

  @Test
  public void invalidShowStatuses() {
    assertEquals("Invalid command format: show status ...",
        getErrorMessageWithInput("show statuses"));
  }

  @Test
  public void invalidShowStatusDuring() {
    assertEquals("Invalid command format: show status on ...",
        getErrorMessageWithInput("show status during"));
  }

  @Test
  public void invalidDateTime() {
    assertEquals("Invalid dateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("show status on 2025/12/29T12:00"));
  }

  @Test
  public void invalidShow() {
    assertEquals("Invalid command format: show status on <dateTime>",
        getErrorMessageWithInput("show status"));
  }

  @Test
  public void validShow() {
    mockModel.setIsBusyReturn = true;
    assertEquals("Busy at 2025-10-29T12:00",
        getDisplayMessageWithInput("show status on 2025-10-29T12:00"));
    mockModel.setIsBusyReturn = false;
    assertEquals("Available at 2025-10-29T12:00",
        getDisplayMessageWithInput("show status on 2025-10-29T12:00"));
  }
}
