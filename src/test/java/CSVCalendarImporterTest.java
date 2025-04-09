import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import dto.ImportResult;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import service.CSVCalendarImporter;

public class CSVCalendarImporterTest {

  private CSVCalendarImporter importer;
  private List<EventDTO> capturedEvents;
  private Consumer<EventDTO> eventConsumer;

  private static final String DATE_FORMAT = "MM/dd/yyyy";
  private static final String TIME_FORMAT = "hh:mm a";

  @Before
  public void setUp() {
    importer = new CSVCalendarImporter();
    capturedEvents = new ArrayList<>();
    eventConsumer = event -> capturedEvents.add(event);
  }

  private Reader createReader(String content) {
    return new StringReader(content);
  }

  @Test
  public void testImportSuccessfulAllFields() throws IOException {
    String csv =
        "Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,Private\n"
            + "Meeting,04/10/2025,02:00 PM,04/10/2025,03:00 PM,False,\"Discuss project details\",\"Conference Room 1\",False";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Meeting", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 4, 10, 14, 0), event.getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 15, 0), event.getEndTime());
    assertFalse(event.getIsAllDay());
    assertEquals("Discuss project details", event.getDescription());
    assertEquals("Conference Room 1", event.getLocation());
    assertTrue(event.getIsPublic());
  }

  @Test
  public void testImportSuccessfulOnlyRequiredFields() throws IOException {
    String csv = "Subject,Start Date\n" + "Simple Event,05/20/2025";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Simple Event", event.getSubject());
    assertEquals(LocalDate.of(2025, 5, 20).atStartOfDay(),
        event.getStartTime()); // defaults to start of day
    assertNull(event.getEndTime());
    assertTrue(event.getIsAllDay());
    assertNull(event.getDescription());
    assertNull(event.getLocation());
    assertFalse(event.getIsPublic());
  }

  @Test
  public void testImportSuccessfulDifferentHeaderOrder() throws IOException {
    String csv = "Start Date,Subject,Location\n" + "06/15/2025,Workshop,\"Building C, Room 3\"";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Workshop", event.getSubject());
    assertEquals(LocalDate.of(2025, 6, 15).atStartOfDay(), event.getStartTime());
    assertEquals("Building C, Room 3", event.getLocation());
  }

  @Test
  public void testImportSuccessfulAllDayEventTrue() throws IOException {
    String csv = "Subject,Start Date,All Day Event\n" + "Holiday,07/04/2025,True";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Holiday", event.getSubject());
    assertEquals(LocalDate.of(2025, 7, 4).atStartOfDay(), event.getStartTime());
    assertTrue(event.getIsAllDay());
  }

  @Test
  public void testImportSuccessfulAllDayEventImplicit() throws IOException {
    // if end date is exactly one day after start date and no times are given, it is all-day
    String csv = "Subject,Start Date,End Date\n"
        + "Vacation Start,08/01/2025,08/02/2025";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Vacation Start", event.getSubject());
    assertEquals(LocalDate.of(2025, 8, 1).atStartOfDay(), event.getStartTime());
    assertEquals(LocalDate.of(2025, 8, 2).atStartOfDay(), event.getEndTime());
    assertTrue(event.getIsAllDay());
  }

  @Test
  public void testImportSuccessfulNotAllDayEventImplicit() throws IOException {
    // if end date is more than one day after start date and no times are given, it is NOT all-day
    String csv = "Subject,Start Date,End Date\n"
        + "Long Event,09/10/2025,09/12/2025";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Long Event", event.getSubject());
    assertEquals(LocalDate.of(2025, 9, 10).atStartOfDay(), event.getStartTime());
    assertEquals(LocalDate.of(2025, 9, 12).atStartOfDay(), event.getEndTime());
    assertFalse(event.getIsAllDay());
  }

  @Test
  public void testImportSuccessfulPrivateEventTrue() throws IOException {
    String csv = "Subject,Start Date,Private\n" + "Personal Appointment,09/01/2025,True";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Personal Appointment", event.getSubject());
    assertFalse(event.getIsPublic());
  }

  @Test
  public void testImportSuccessfulQuotedFieldsWithCommasAndQuotes() throws IOException {
    String csv = "Subject,Start Date,Location,Description\n"
        + "\"Review, Final\",10/10/2025,\"Main St, Bldg A\",\"Notes contain \"\"quotes\"\" inside\"";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Review, Final", event.getSubject());
    assertEquals(LocalDate.of(2025, 10, 10).atStartOfDay(), event.getStartTime());
    assertEquals("Main St, Bldg A", event.getLocation());
    assertEquals("Notes contain \"quotes\" inside", event.getDescription());
  }

  @Test
  public void testImportSuccessfulMultipleEventsSkipEmptyLines() throws IOException {
    String csv =
        "Subject,Start Date,Start Time\n"
            + "Event 1,11/01/2025,09:00 AM\n"
            + "\n" // empty line should be skipped
            + "Event 2,11/02/2025,10:30 AM\n";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(2, result.getSuccessCount());
    assertEquals(2, result.getTotalCount());
    assertEquals(2, capturedEvents.size());
    assertEquals("Event 1", capturedEvents.get(0).getSubject());
    assertEquals("Event 2", capturedEvents.get(1).getSubject());
  }

  @Test
  public void testImportFailureEmptyFile() throws IOException {
    String csv = "";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }

  @Test
  public void testImportFailureOnlyEmptyLine() throws IOException {
    String csv = "\n";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }


  @Test
  public void testImportFailureMissingRequiredHeaderSubject() throws IOException {
    String csv = "Start Date,Location\n" + "01/01/2026,Somewhere";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }

  @Test
  public void testImportFailureMissingRequiredHeaderStartDate() throws IOException {
    String csv = "Subject,Location\n" + "My Event,Somewhere";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }

  @Test
  public void testImportFailureInvalidHeader() throws IOException {
    String csv = "Subject,Start Date,Invalid Column\n" + "Event,01/01/2026,Data";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }

  @Test
  public void testImportPartialSuccessMismatchedColumns() throws IOException {
    String csv =
        "Subject,Start Date,Location\n"
            + "Event 1,03/01/2026,Office\n"
            + "Event 2,03/02/2026\n" // missing location column data
            + "Event 3,03/03/2026,Home";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(2, result.getSuccessCount());
    assertEquals(3, result.getTotalCount());
    assertEquals(2, capturedEvents.size());
    assertEquals("Event 1", capturedEvents.get(0).getSubject());
    assertEquals("Event 3", capturedEvents.get(1).getSubject());
  }

  @Test
  public void testImportPartialSuccessInvalidDateFormat() throws IOException {
    String csv =
        "Subject,Start Date,Start Time\n"
            + "Event 1,12/01/2025,11:00 AM\n"
            + "Event 2,INVALID-DATE,01:00 PM\n" // invalid date format
            + "Event 3,12/03/2025,02:00 PM";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(2,
        result.getSuccessCount());
    assertEquals(3, result.getTotalCount());
    assertEquals(2, capturedEvents.size());
    assertEquals("Event 1", capturedEvents.get(0).getSubject());
    assertEquals("Event 3", capturedEvents.get(1).getSubject());
  }

  @Test
  public void testImportPartialSuccessInvalidTimeFormat() throws IOException {
    String csv =
        "Subject,Start Date,Start Time\n"
            + "Event 1,12/10/2025,09:00 AM\n"
            + "Event 2,12/11/2025,INVALID-TIME\n" // invalid time format
            + "Event 3,12/12/2025,10:00 AM";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(3, result.getSuccessCount());
    assertEquals(3, result.getTotalCount());
    assertEquals(3, capturedEvents.size());
    assertEquals("Event 1", capturedEvents.get(0).getSubject());
    assertEquals(LocalTime.of(9, 0), capturedEvents.get(0).getStartTime().toLocalTime());
    assertEquals("Event 2", capturedEvents.get(1).getSubject());
    assertEquals(LocalTime.MIDNIGHT,
        capturedEvents.get(1).getStartTime().toLocalTime());
    assertEquals("Event 3", capturedEvents.get(2).getSubject());
    assertEquals(LocalTime.of(10, 0), capturedEvents.get(2).getStartTime().toLocalTime());
  }


  @Test
  public void testImportPartialSuccessConsumerThrowsException() throws IOException {
    String csv =
        "Subject,Start Date\n"
            + "Good Event 1,01/01/2027\n"
            + "Bad Event,01/02/2027\n"
            + "Good Event 2,01/03/2027";

    Consumer<EventDTO> pickyConsumer = event -> {
      if (event.getSubject().equals("Bad Event")) {
        throw new IllegalArgumentException("Simulated consumer error");
      }
      capturedEvents.add(event);
    };

    ImportResult result = importer.importEvents(createReader(csv), pickyConsumer);

    assertEquals(2, result.getSuccessCount());
    assertEquals(3, result.getTotalCount());
    assertEquals(2, capturedEvents.size());
    assertEquals("Good Event 1", capturedEvents.get(0).getSubject());
    assertEquals("Good Event 2", capturedEvents.get(1).getSubject());
  }

  @Test
  public void testImportSuccessOnlyHeaderRow() throws IOException {
    String csv = "Subject,Start Date,Location";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(0, result.getSuccessCount());
    assertEquals(0, result.getTotalCount());
    assertTrue(capturedEvents.isEmpty());
  }

  @Test
  public void testImportSuccessCaseInsensitiveBoolean() throws IOException {
    String csv = "Subject,Start Date,All Day Event,Private\n"
        + "Case Test,05/05/2026,tRuE,fAlSe";
    ImportResult result = importer.importEvents(createReader(csv),
        eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertTrue(event.getIsAllDay());
    assertTrue(event.getIsPublic());
  }

  @Test
  public void testImportSuccessNonBooleanFalse() throws IOException {
    String csv = "Subject,Start Date,All Day Event,Private\n"
        + "Non Boolean,06/06/2026,Maybe,Yes";
    ImportResult result = importer.importEvents(createReader(csv),
        eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertFalse(event.getIsAllDay());
    assertTrue(event.getIsPublic());
  }

  @Test
  public void testImportSuccessfulEndDateNoEndTime() throws IOException {
    String csv = "Subject,Start Date,Start Time,End Date\n"
        + "Multi-day Event,07/15/2025,10:00 AM,07/17/2025";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Multi-day Event", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 7, 15, 10, 0), event.getStartTime());
    assertEquals(LocalDateTime.of(2025, 7, 17, 0, 0), event.getEndTime());
    assertFalse(event.getIsAllDay());
  }

  @Test
  public void testImportSuccessfulEndTimeNoEndDate() throws IOException {
    String csv = "Subject,Start Date,Start Time,End Time\n"
        + "Same Day Event,08/20/2025,01:00 PM,04:00 PM";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Same Day Event", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 8, 20, 13, 0), event.getStartTime());
    assertEquals(LocalDateTime.of(2025, 8, 20, 16, 0), event.getEndTime());
    assertFalse(event.getIsAllDay());
  }

  @Test
  public void testImportNoAllDaySpecified() throws IOException {
    String csv =
        "Subject,Start Date,Start Time,End Date,End Time\n"
            + "Meeting,04/10/2025,02:00 PM,04/10/2025,03:00 PM";
    ImportResult result = importer.importEvents(createReader(csv), eventConsumer);

    assertEquals(1, result.getSuccessCount());
    assertEquals(1, result.getTotalCount());
    assertEquals(1, capturedEvents.size());

    EventDTO event = capturedEvents.get(0);
    assertEquals("Meeting", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 4, 10, 14, 0), event.getStartTime());
    assertEquals(LocalDateTime.of(2025, 4, 10, 15, 0), event.getEndTime());
    assertFalse(event.getIsAllDay());
    assertNull(event.getDescription());
    assertNull(event.getLocation());
    assertFalse(event.getIsPublic());
  }

}