import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import java.io.File;

/**
 * This is a test class for the ExportCalendarCommand class.
 */
public class ExportCalendarCommandTest extends AbstractCommandTest {

  private String testDir;
  private File testFile;

  @Before
  public void setUp() {
    super.setUp();
    testDir = "target/test_output/";
    new File(testDir).mkdirs();
  }

  @After
  public void tearDown() {
    // Clean up test files
    File directory = new File(testDir);
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.getName().endsWith(".csv")) {
            file.delete();
          }
        }
      }
    }
  }

  @Test
  public void invalidExport() {
    assertEquals("Invalid command format: export cal <filename(.csv)>",
        getErrorMessageWithInput("export "));
  }

  @Test
  public void invalidExportCalendar() {
    assertEquals("Invalid command format: export cal ...",
        getErrorMessageWithInput("export calendar"));
  }

  @Test
  public void invalidFileName() {
    assertEquals("Filename must end with .csv or specified without extension. Found: .txt",
        getErrorMessageWithInput("export cal filename.txt"));
  }

  @Test
  public void testValidExportWithoutExtension() {
    MockView mockView = new MockView("export cal " + testDir + "no_extension\n");
    mockModel.exportEventsWithExporterReceived = testDir + "CSV data without extension";
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockModel.exportEventsWithExporterCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);

    // Verify the output message indicates success
    assertTrue(mockView.displayMessage.toString().contains("Calendar exported to file:"));

    // Check if the file exists
    testFile = new File(testDir + "no_extension.csv");
    // Uncomment the following line to check file existence if your test harness actually creates files
    assertTrue(testFile.exists());
  }

  @Test
  public void testValidExportWithExtension() {
    MockView mockView = new MockView("export cal " + testDir + "with_extension.csv\n");
    mockModel.exportEventsWithExporterReceived = testDir + "CSV data with extension";
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockModel.exportEventsWithExporterCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);

    // Verify the output message indicates success
    assertTrue(mockView.displayMessage.toString().contains("Calendar exported to file:"));

    // Check if the file exists
    testFile = new File(testDir + "with_extension.csv");
    // Uncomment the following line to check file existence if your test harness actually creates files
    assertTrue(testFile.exists());
  }

  @Test
  public void testExportWithNoEvents() {
    // Setup mock model to throw CalendarExportException when there are no events
    mockModel.shouldThrowCalendarExportException = true;
    assertEquals("No events to export",
        getErrorMessageWithInput("export cal " + testDir + "empty_calendar.csv"));
  }
}
