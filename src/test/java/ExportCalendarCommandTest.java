import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import org.junit.Test;

public class ExportCalendarCommandTest extends AbstractCommandTest {

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
  public void validFileNameWithoutExtension() {
    mockModel.shouldThrowCalendarExportException = false;
    MockView mockView = new MockView("export cal filename\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertEquals("calApp> Calendar exported to file:\n"
            + "Return from exportToCSV\n"
            + "calApp> ",
        mockView.displayMessage.toString());
    assertTrue(mockModel.exportToCSVCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("filename.csv",
        mockModel.exportToCSVReceived.filename);
  }

  @Test
  public void validFileNameWithExtension() {
    mockModel.shouldThrowCalendarExportException = false;
    MockView mockView = new MockView("export cal filename.csv\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertEquals("calApp> Calendar exported to file:\n"
            + "Return from exportToCSV\n"
            + "calApp> ",
        mockView.displayMessage.toString());
    assertTrue(mockModel.exportToCSVCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("filename.csv",
        mockModel.exportToCSVReceived.filename);
  }
}
