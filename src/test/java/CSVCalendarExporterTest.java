import static org.junit.Assert.assertEquals;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import service.CSVCalendarExporter;

public class CSVCalendarExporterTest {

  private List<EventDTO> events;
  private CSVCalendarExporter exporter;

  @Before
  public void setUp() {
    events = new ArrayList<EventDTO>();
    exporter = new CSVCalendarExporter();
  }

  @Test
  public void testCSVExporter() {
    populateEvents();
    assertEquals(
        "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,Private"
            + System.lineSeparator()
            + "\"Test Subject\",01/01/2020,12:00 AM,01/01/2020,12:00 PM,False,\"Test Description\",\"Test Location\",True"
            + System.lineSeparator()
            + "\"Test All Day\",01/01/2020,,01/02/2020,,True,\"Test Description\",\"Test Location\",False"
            + System.lineSeparator()
            + ",01/01/2020,,01/02/2020,,True,,,False"
            + System.lineSeparator(),
        exporter.export(events));
  }

  private void populateEvents() {
    // simple event
    events.add(EventDTO.getBuilder()
        .setSubject("Test Subject")
        .setDescription("Test Description")
        .setLocation("Test Location")
        .setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2020, 1, 1, 12, 0))
        .setIsAllDay(false)
        .setIsPublic(false)
        .build());
    // all day event
    events.add(EventDTO.getBuilder()
        .setSubject("Test All\" Day")
        .setDescription("Test\" Description")
        .setLocation("Test\" Location")
        .setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2020, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsPublic(true)
        .build());
    // empty fields event
    events.add(EventDTO.getBuilder()
        .setSubject("")
        .setDescription("")
        .setLocation("")
        .setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2020, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsPublic(true)
        .build());
  }
}