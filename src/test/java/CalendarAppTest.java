import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a test class for the CalendarApp class.
 */
public class CalendarAppTest {

  private ByteArrayOutputStream errorStream;
  private ByteArrayOutputStream outputStream;

  @Before
  public void setUp() {
    errorStream = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errorStream));

    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @Test
  public void testNoArgumentsPassed() {
    CalendarApp.main(new String[0]);
    assertEquals("Usage: java CalendarApp.java --mode [interactive | headless filepath]\n",
        errorStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testModeFlagNotSetHeadless() {
    CalendarApp.main(new String[]{"headless", "filename.txt"});
    assertEquals("Usage: java CalendarApp.java --mode [interactive | headless filepath]\n",
        errorStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testModeFlagNotSetInteractive() {
    CalendarApp.main(new String[]{"interactive", "mode"});
    assertEquals("Usage: java CalendarApp.java --mode [interactive | headless filepath]\n",
        errorStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testInvalidModeName() {
    CalendarApp.main(new String[]{"--mode", "ui"});
    assertEquals("Invalid mode: ui\n",
        errorStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testStartInteractiveMode() {
    System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
    CalendarApp.main(new String[]{"--mode", "interactive"});
    assertEquals("Starting calendar in INTERACTIVE mode...\n"
            + "calApp [No calendar in use]> Bye...\n",
        outputStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testStartHeadlessMode() {
    CalendarApp.main(
        new String[]{"--mode", "headless", "src/test/resources/headlessExitCommand.txt"});
    assertEquals("Starting calendar in HEADLESS mode...\n",
        outputStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testStartHeadlessWithoutFilepath() {
    CalendarApp.main(
        new String[]{"--mode", "headless"});
    assertEquals("Usage: java CalendarApp.java --mode headless filepath\n",
        errorStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testStartInteractiveModeCaseInsensitive() {
    System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
    CalendarApp.main(new String[]{"--mode", "InTeraCtiVe"});
    assertEquals("Starting calendar in INTERACTIVE mode...\n"
            + "calApp [No calendar in use]> Bye...\n",
        outputStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testStartHeadlessModeCaseInsensitive() {
    CalendarApp.main(
        new String[]{"--mode", "hEadlEss", "src/test/resources/headlessExitCommand.txt"});
    assertEquals("Starting calendar in HEADLESS mode...\n",
        outputStream.toString().replace("\r\n", "\n"));
  }

  @Test
  public void testHeadlessFileNotFound() {
    String fileName = "src/test/resources/NoSuchFileExists.txt";
    CalendarApp.main(new String[]{"--mode", "hEadlEss", fileName});
    assertEquals("File not found: " + fileName + "\n",
        errorStream.toString().replace("\r\n", "\n"));
  }
}