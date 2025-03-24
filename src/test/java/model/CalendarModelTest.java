package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import exception.InvalidEventDetailsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CalendarModel.
 */
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

  @Test(expected = InvalidEventDetailsException.class)
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

    int eventsEdited = calendarModel.editEvent(
        "Sample Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        eventDTOWithEditedParam
    );
    assertEquals(1, eventsEdited);
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

  @Test
  public void testEditRecurringAllDayEvent() {
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

    EventDTO editedEvent = EventDTO.getBuilder()
        .setSubject("Edited Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 14, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();

    calendarModel.editEvent(
        "Recurring Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 13, 0, 0),
        editedEvent
    );

    assertEquals(5, calendarModel.eventRepository.getAllEvents().size());
    EventDTO editedEvent1 = calendarModel.eventRepository.getEvent(
        "Edited Recurring Event",
        LocalDateTime.of(2025, 3, 14, 0, 0),
        LocalDateTime.of(2025, 3, 15, 0, 0)
    );

    assertEquals("Edited Recurring Event", editedEvent1.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), editedEvent1.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 15, 0, 0), editedEvent1.getEndTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventThrowExceptionIfEventNotPresent() {
    calendarModel.editEvent("Not Present Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        sampleSpannedSingleEventDTO);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventThrowExceptionIfUpdatadeDtoIsNull() {
    calendarModel.editEvent(
        "Sample Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        null
    );
  }

  @Test
  public void testEditSimpleDetailsForEvent() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, true);
    EventDTO editedEventDTO = EventDTO.getBuilder()
        .setSubject("Edited Sample Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 3, 0))
        .setDescription("Sample Description")
        .setIsPublic(true)
        .setLocation("Boston")
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
    calendarModel.editEvent(
        "Sample Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        editedEventDTO
    );

    EventDTO editedEvent = calendarModel.eventRepository.getEvent(
        "Edited Sample Event",
        LocalDateTime.of(2025, 3, 12, 2, 0),
        LocalDateTime.of(2025, 3, 12, 3, 0)
    );

    assertEquals("Edited Sample Event", editedEvent.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 12, 2, 0), editedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 12, 3, 0), editedEvent.getEndTime());
    assertEquals("Sample Description", editedEvent.getDescription());
    assertEquals("Boston", editedEvent.getLocation());
    assertTrue(editedEvent.getIsPublic());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditRecurringDetailsForExistingRecurringEventThrowsException() {
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
        .setIsRecurring(null)
        .setIsAllDay(false)
        .build();
    calendarModel.editEvent(
        "Recurring Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        editedEvent
    );
  }

  @Test()
  public void testEditAndSetRecurringDetailsForExistingSimpleEvent() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, true);
    RecurringDetailsDTO recurringDetailsDTO = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO editedEventDTO = EventDTO.getBuilder()
        .setSubject("Edited Simple Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 12, 3, 0))
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetailsDTO)
        .build();
    Integer eventsEdited = calendarModel.editEvent(
        "Sample Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        LocalDateTime.of(2025, 3, 12, 1, 0),
        editedEventDTO
    );
    EventDTO editedEvent = calendarModel.eventRepository.getEvent(
        "Edited Simple Event",
        LocalDateTime.of(2025, 3, 12, 2, 0),
        LocalDateTime.of(2025, 3, 12, 3, 0)
    );
    assertEquals("Edited Simple Event", editedEvent.getSubject());
    assertEquals(LocalDateTime.of(2025, 3, 12, 2, 0),
        editedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 3, 12, 3, 0),
        editedEvent.getEndTime());
  }

  // Test getEventsOnDate Method in CalendarModel

  @Test
  public void testGetEventsOnDate() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, true);
    List<EventDTO> eventsOnDate = calendarModel.getEventsOnDate(LocalDate.of(2025, 3, 12));
    assertEquals(1, eventsOnDate.size());
    assertEquals("Sample Event", eventsOnDate.get(0).getSubject());
  }

  // Test getEventsInRange Method in CalendarModel

  @Test
  public void testGetEventsInRange() {
    calendarModel.createEvent(sampleSpannedSingleEventDTO, true);
    EventDTO anotherSampleEvent = EventDTO.getBuilder()
        .setSubject("Another Sample Event")
        .setStartTime(LocalDateTime.of(2025, 3, 13, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 13, 3, 0))
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
    calendarModel.createEvent(anotherSampleEvent, false);
    List<EventDTO> eventsInRange = calendarModel.getEventsInRange(
        LocalDateTime.of(2025, 3, 11, 0, 0),
        LocalDateTime.of(2025, 3, 14, 1, 0)
    );
    assertEquals(2, eventsInRange.size());
  }

  // Test Editing Events by Name

  @Test(expected = IllegalArgumentException.class)
  public void testRecurringAllDayEventWithNoEventsToEditShouldRaiseExceptions() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO editedEvent = EventDTO.getBuilder()
        .setSubject("Edited Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 14, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(recurringDetails)
        .build();

    calendarModel.editEvent(
        "Not Present Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        null,
        editedEvent
    );
  }

  @Test
  public void testAllDayEventWithRecurrenceSetAndEndTimeNull() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(2)
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

    EventDTO editedEvent = EventDTO.getBuilder()
        .setSubject("Edited Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 14, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setDescription("Sample Description")
        .setLocation("Boston")
        .setIsPublic(true)
        .setIsRecurring(false)
        .setIsAllDay(false)
        .setRecurringDetails(null)
        .build();

    calendarModel.editEvent(
        "Recurring Event",
        LocalDateTime.of(2025, 3, 12, 0, 0),
        null,
        editedEvent
    );

    assertEquals(2, calendarModel.eventRepository.getAllEvents().size());
  }

  @Test
  public void testEditAllDayEventWithRecurrenceChangeWillCreateNewEvents() {
    RecurringDetailsDTO recurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(null)
        .build();
    EventDTO recurrenceEvent = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 12, 0, 0))
        .setEndTime(null) // null end time for all day event
        .setIsRecurring(true)
        .setIsAllDay(true)
        .setRecurringDetails(recurringDetails)
        .build();
    calendarModel.createEvent(recurrenceEvent, false);

    RecurringDetailsDTO editedRecurringDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.S, CalendarDayOfWeek.M, CalendarDayOfWeek.W))
        .setOccurrences(3)
        .setUntilDate(null)
        .build();
    EventDTO editedEventDTO = EventDTO.getBuilder()
        .setSubject("Edited Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 3, 15, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 3, 15, 23, 59))
        .setIsRecurring(true)
        .setIsAllDay(true)
        .setRecurringDetails(recurringDetails)
        .build();

    calendarModel.editEvent(
        "Recurring Event",
        LocalDateTime.of(2025, 3, 15, 0, 0),
        null,
        editedEventDTO
    );

    assertEquals(6, calendarModel.eventRepository.getAllEvents().size());
  }

  // Test exportToCSV Method in CalendarModel

//  @Test(expected = CalendarExportException.class)
//  public void testExportToCSVWithNoEventsShouldRaiseException() {
//    calendarModel.exportToCSV("target/export_empty_events.csv");
//  }
//
//  @Test
//  public void testExportToCSVWithoutExtAddsCSVExt() {
//    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
//    String validFileWithCSVExt = calendarModel.exportToCSV("target/without_csv_ext");
//    assertTrue(validFileWithCSVExt.endsWith(".csv"));
//  }
//
//  @Test
//  public void testExportToCSVCreatesCSVFile() throws CalendarExportException, IOException {
//    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
//    String fileName = calendarModel.exportToCSV("target/export_valid_csv.csv");
//    // assert it is csv file
//    assertTrue(fileName.endsWith(".csv"));
//    File test_exported_csv_file = new File(fileName);
//    // check if file is created
//    assertTrue(test_exported_csv_file.exists());
//  }
//
//  @Test
//  public void testExportToCSVFormattingForStoringEvents() throws IOException {
//    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
//    EventDTO eventContainingCharToEscape = EventDTO.getBuilder()
//        .setSubject("Event \"with new line\n")
//        .setStartTime(LocalDateTime.parse("2025-03-12T00:00:00"))
//        .setEndTime(LocalDateTime.parse("2025-03-12T01:00:00"))
//        .setIsRecurring(false)
//        .setIsAllDay(false)
//        .build();
//    calendarModel.createEvent(eventContainingCharToEscape, false);
//
//    String fileName = calendarModel.exportToCSV("target/test_formatting_csv.csv");
//    File test_exported_csv_file = new File(fileName);
//    BufferedReader reader = new BufferedReader(new FileReader(test_exported_csv_file));
//
//    // check the contents in the file are loaded as expected for sampleSpannedSingleEventDTO
//    assertEquals("Subject,Start Date,Start Time,End Date,"
//        + "End Time,All Day Event,Description,Location,Private", reader.readLine());
//    assertEquals("\"Sample Event\",03/12/2025,12:00 AM"
//        + ",03/12/2025,01:00 AM,False,,,True", reader.readLine());
//
//    // check the contents in the file are loaded as expected for eventContainingCharToEscape
//    assertEquals("\"Event with new line\",03/12/2025,12:00 AM"
//        + ",03/12/2025,01:00 AM,False,,,True", reader.readLine());
//    test_exported_csv_file.delete();
//  }
//
//  @Test(expected = CalendarExportException.class)
//  public void testInvalidFileThrowsAnException() {
//    calendarModel.createEvent(sampleSpannedSingleEventDTO, false);
//    calendarModel.exportToCSV("///oot/invalid_file_name.csv");
//  }

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
