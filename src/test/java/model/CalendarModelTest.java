package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class CalendarModelTest {

  private CalendarModel calendarModel;
  private EventDTO sampleSpannedSingleEventDTO;

  @Before
  public void setUp() {
    calendarModel = new CalendarModel();
    this.sampleSpannedSingleEventDTO = EventDTO.getBuilder()
        .setSubject("Sample Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
  }

  // Test createEvent Method in CalendarModel for Single Event

  @Test(expected = InvalidDateTimeRangeException.class)
  public void testCreateEventWithWithNullStartDateThrowsException() {
    EventDTO eventWithoutStartDate = EventDTO.getBuilder()
        .setSubject("Event Without Start Date")
        .setStartTime(null)
        .setEndTime(null)
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
    calendarModel.createEvent(eventWithoutStartDate, false);
  }

  @Test
  public void testCreateEventWithValidEventShouldReturnTrue() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    assertEquals(1, calendarModel.eventRepository.getAllEvents().size());
  }

  @Test(expected = EventConflictException.class)
  public void testCreateEventShouldReturnFalseOnConflict() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    calendarModel.createEvent(sampleSpannedSingleEventDTO, true);
  }

  @Test
  public void testValidCreateEventForAllDayEvent() {
    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("All Day Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(false)
        .setIsAllDay(true)
        .build();
    calendarModel.createEvent(allDayEvent, false);
    assertEquals(1, calendarModel.eventRepository.getAllEvents().size());
    assertTrue(calendarModel.eventRepository.getAllEvents().get(0).getIsAllDay());
    assertFalse(calendarModel.eventRepository.getAllEvents().get(0).getIsRecurring());
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 12, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 13, 0, 0)));
  }

  @Test
  public void testValidCreateEventForSpannedRecurringEventWithOccurrence() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
    assertEquals(5, calendarModel.eventRepository.getAllEvents().size());
    assertFalse(calendarModel.isBusy(LocalDateTime.of(2025, 3, 11, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 12, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 15, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 17, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 19, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 22, 0, 0)));
    assertFalse(calendarModel.isBusy(LocalDateTime.of(2025, 3, 24, 0, 0)));
  }

  @Test
  public void testValidCreateEventForRecurringEventWithUntil() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(null)
        .setUntilDate(LocalDateTime.of(2025, 3, 19, 0, 0))
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
    assertEquals(4, calendarModel.eventRepository.getAllEvents().size());
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 12, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 15, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 17, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 19, 0, 0)));
    assertFalse(calendarModel.isBusy(LocalDateTime.of(2025, 3, 22, 0, 0)));
  }

  @Test
  public void testValidCreateEventForRecurringAllDayEventWithOccurrence() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(true)
        .setIsAllDay(true)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
    assertEquals(5, calendarModel.eventRepository.getAllEvents().size());
    assertTrue(calendarModel.eventRepository.getAllEvents().get(0).getIsAllDay());
    assertTrue(calendarModel.eventRepository.getAllEvents().get(0).getIsRecurring());
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 12, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 13, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 15, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 17, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 19, 0, 0)));
    assertTrue(calendarModel.isBusy(LocalDateTime.of(2025, 3, 22, 0, 0)));
    assertFalse(calendarModel.isBusy(LocalDateTime.of(2025, 3, 24, 0, 0)));
  }

  @Test(expected = EventConflictException.class)
  public void testCreateAllDayRecurringEventHasConflict() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false); // 2025/3/12T00:00
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO conflictingEvent = EventDTO.getBuilder()
        .setSubject("Conflicting Event")
        .setStartTime(LocalDateTime.of(2025, 3, 10, 0, 30))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(true)
        .setIsAllDay(true)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(conflictingEvent, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringEventSpanningOverDayThrowException() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 23, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 13, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringEventWithoutRepeatDaysThrowException() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(null)
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateRecurringEventWithoutOccurrenceOrUntilThrowException() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(null)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUntilDateCannotBeBeforeStartDate() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(null)
        .setUntilDate(LocalDateTime.of(2025, 3, 11, 0, 0))
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);
  }

  // Test editEvent Method in CalendarModel for Single Event

  @Test
  public void testEditSingleSpannedEvent() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    EventDTO eventDTOWithEditedParam = EventDTO.getBuilder()
        .setSubject("\"Edited Sample Event\"")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 3, 0))
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();

    calendarModel.editEvent(
        "Sample Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        eventDTOWithEditedParam
        );

    assertEquals(1, calendarModel.eventRepository.getAllEvents().size());
    EventDTO editedEvent = calendarModel.eventRepository.getEvent(
        "\"Edited Sample Event\"",
        LocalDateTime.of(2025, 3, 12, 2, 0),
        LocalDateTime.of(2025, 3, 12, 3, 0)
    );

    assertEquals("\"Edited Sample Event\"", editedEvent.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 12, 2, 0), editedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 12, 3, 0), editedEvent.getEndTime());
  }

  @Test
  public void testEditSpannedRecurringEvents() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurringEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurringEvent, false);

    EventDTO editedEvent = EventDTO.getBuilder()
        .setSubject("Edited Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 3, 0))
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();

    calendarModel.editEvent(
        "Recurring Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        editedEvent
    );

    assertEquals(5, calendarModel.eventRepository.getAllEvents().size());
    EventDTO editedEvent1 = calendarModel.eventRepository.getEvent(
        "Edited Recurring Event",
        LocalDateTime.of(2025, 3, 12, 2, 0),
        LocalDateTime.of(2025, 3, 12, 3, 0)
    );

    assertEquals("Edited Recurring Event", editedEvent1.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 12, 2, 0), editedEvent1.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 12, 3, 0), editedEvent1.getEndTime());
  }

  @Test
  public void testEditSingleAllDayEvent() {
    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("All Day Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(false)
        .setIsAllDay(true)
        .build();
    calendarModel.createEvent(allDayEvent, false);

    EventDTO event = calendarModel.eventRepository.getEvent("All Day Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 13, 0, 0)
    );

    EventDTO editedEvent = EventDTO.getBuilder()
        .setSubject("Edited All Day Event")
        .setStartTime(LocalDateTime.of(2025, 3, 13, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();

    calendarModel.editEvent(
        "All Day Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 13, 0, 0),
        editedEvent
    );

    assertEquals(1, calendarModel.eventRepository.getAllEvents().size());
    EventDTO editedEvent1 = calendarModel.eventRepository.getEvent(
        "Edited All Day Event",
        LocalDateTime.of(2025, 3, 13, 0, 0),
        LocalDateTime.of(2025, 3, 14, 0, 0)
    );

    assertEquals("Edited All Day Event", editedEvent1.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 13, 0, 0), editedEvent1.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), editedEvent1.getEndTime());
  }

  // Test exportToCSV Method in CalendarModel

  @Test(expected = CalendarExportException.class)
  public void testExportToCSVWithNoEventsShouldRaiseException() {
    calendarModel.exportToCSV("target/export_empty_events.csv");
  }

  @Test
  public void testExportToCSVWithoutExtAddsCSVExt() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    String validFileWithCSVExt = calendarModel.exportToCSV("target/without_csv_ext");
    assertTrue(validFileWithCSVExt.endsWith(".csv"));
  }

  @Test
  public void testExportToCSVCreatesCSVFile() throws CalendarExportException, IOException {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    String fileName = calendarModel.exportToCSV("target/export_valid_csv.csv");
    // assert it is csv file
    assertTrue(fileName.endsWith(".csv"));
    File test_exported_csv_file = new File(fileName);
    // check if file is created
    assertTrue(test_exported_csv_file.exists());
  }

  @Test
  public void testExportToCSVFormattingForStoringEvents() throws IOException {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    EventDTO eventContainingCharToEscape = EventDTO.getBuilder()
        .setSubject("Event \"with new line\n")
        .setStartTime(LocalDateTime.parse("2025-03-12T00:00:00"))
        .setEndTime(LocalDateTime.parse("2025-03-12T01:00:00"))
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
    calendarModel.createEvent(eventContainingCharToEscape, false);

    String fileName = calendarModel.exportToCSV("target/test_formatting_csv.csv");
    File test_exported_csv_file = new File(fileName);
    BufferedReader reader = new BufferedReader(new FileReader(test_exported_csv_file));

    // check the contents in the file are loaded as expected for sampleSpannedSingleEventDTO
    assertEquals("Subject,Start Date,Start Time,End Date,"
        + "End Time,All Day Event,Description,Location,Private", reader.readLine());
    assertEquals("\"Sample Event\",03/12/2025,12:00 AM"
        + ",03/12/2025,01:00 AM,False,,,True", reader.readLine());

    // check the contents in the file are loaded as expected for eventContainingCharToEscape
    assertEquals("\"Event with new line\",03/12/2025,12:00 AM"
        + ",03/12/2025,01:00 AM,False,,,True", reader.readLine());
    test_exported_csv_file.delete();
  }

  @Test(expected = CalendarExportException.class)
  public void testInvalidFileThrowsAnException() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
    calendarModel.exportToCSV("///oot/invalid_file_name.csv");
  }

  // Test isBusy Method in CalendarModel

  @Test
  public void testIsBusyWhenNoEventsShouldReturnFalse() {
    assertFalse("No events should result in not busy",
        calendarModel.isBusy(LocalDateTime.of(2025,
            3, 15, 10, 0)));
  }

  @Test
  public void testIsBusyWithEventsShouldReturnTrue() {
    EventDTO isBusyEvent = EventDTO.getBuilder()
        .setSubject("Is Busy Meeting")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 1, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 2, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();

    calendarModel.createEvent(isBusyEvent, false);

    // should return true on exact start time of event
    assertTrue(calendarModel.isBusy(
        LocalDateTime.of(2025, 3, 12, 1, 0)
    ));

    // should return true on exact end time of event
    assertTrue(calendarModel.isBusy(
        LocalDateTime.of(2025, 3, 12, 2, 0)
    ));

    // should return turn in between the start and end time of event
    assertTrue(calendarModel.isBusy(
        LocalDateTime.of(2025, 3, 12, 1, 30)
    ));

    // should return false on outside of start and end time of event
    assertFalse(calendarModel.isBusy(
        LocalDateTime.of(2025, 3, 12, 2, 1)
    ));
    assertFalse(calendarModel.isBusy(
        LocalDateTime.of(2025, 3, 12, 0, 59)
    ));
  }

}
