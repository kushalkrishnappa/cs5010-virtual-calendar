import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import exception.CalendarExportException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.CalendarModel;
import org.junit.Test;
import service.ICalendarExporter;

/**
 * Test class for ExportCalendar.
 */
public class ExportCalendarTest {

  /**
   * Mock implementation of ICalendarExporter for testing purposes.
   */
  public class MockCalendarExporter implements ICalendarExporter {

    boolean exportCalled;
    List<EventDTO> events;

    MockCalendarExporter() {
      exportCalled = false;
      events = new ArrayList<EventDTO>();
    }

    @Override
    public String export(List<EventDTO> events) {
      exportCalled = true;
      this.events.addAll(events);
      return "Export Called";
    }
  }

  @Test
  public void testEmptyExport() {
    MockCalendarExporter exporter = new MockCalendarExporter();
    CalendarModel model = new CalendarModel();
    assertThrows(CalendarExportException.class,
        () -> model.exportEventsWithExporter(exporter));
    assertFalse(exporter.exportCalled);
  }

  @Test
  public void testExport() {
    MockCalendarExporter exporter = new MockCalendarExporter();
    CalendarModel model = new CalendarModel();
    List<EventDTO> events = Arrays.asList(
        EventDTO.getBuilder()
            .setSubject("Test Subject")
            .setDescription("Test Description")
            .setLocation("Test Location")
            .setStartTime(LocalDateTime.of(2025, 12, 12, 12, 12))
            .setEndTime(LocalDateTime.of(2025, 12, 12, 12, 12))
            .setIsAllDay(false)
            .setIsPublic(true)
            .setIsRecurring(false)
            .build(),
        EventDTO.getBuilder()
            .setSubject("Test Subject2")
            .setDescription("Test Description2")
            .setLocation("Test Location2")
            .setStartTime(LocalDateTime.of(2025, 5, 15, 15, 15))
            .setEndTime(LocalDateTime.of(2025, 5, 16, 16, 16))
            .setIsAllDay(false)
            .setIsPublic(true)
            .setIsRecurring(false)
            .build()
    );
    events.forEach(eventDTO -> model.createEvent(eventDTO, false));
    assertEquals("Export Called", model.exportEventsWithExporter(exporter));
    assertTrue(exporter.exportCalled);
    assertEquals(2, exporter.events.size());
    exporter.events.forEach(event -> assertTrue(events.contains(event)));
  }
}
