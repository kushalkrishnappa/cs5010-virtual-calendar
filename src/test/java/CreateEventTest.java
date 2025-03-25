import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import exception.InvalidEventDetailsException;
import java.time.LocalDateTime;
import java.util.Set;
import model.CalendarDayOfWeek;
import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;

public class CreateEventTest {

  private CalendarModel model;

  @Before
  public void setUp() throws Exception {
    model = new CalendarModel();
  }

  @Test
  public void emptyModel() {
    assertEquals(0, model.getAllEvents().size());
  }

  @Test
  public void createSimpleEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
  }

  @Test
  public void createAllDayEvent() {
    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .build();
    EventDTO expectedAllDayEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(allDayEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(0));
  }

  @Test
  public void createRecurringSimpleEventWithOccurrence() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO expectedEvent1 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent2 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent3 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent4 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent5 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleRecurringEvent, false);
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedEvent1, model.getAllEvents().get(0));
    assertEquals(expectedEvent2, model.getAllEvents().get(1));
    assertEquals(expectedEvent3, model.getAllEvents().get(2));
    assertEquals(expectedEvent4, model.getAllEvents().get(3));
    assertEquals(expectedEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void createRecurringSimpleEventWithUntilDate() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0, 0))
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO expectedEvent1 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent2 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent3 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent4 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent5 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleRecurringEvent, false);
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedEvent1, model.getAllEvents().get(0));
    assertEquals(expectedEvent2, model.getAllEvents().get(1));
    assertEquals(expectedEvent3, model.getAllEvents().get(2));
    assertEquals(expectedEvent4, model.getAllEvents().get(3));
    assertEquals(expectedEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void createRecurringAllDayEventWithOccurrence() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO expectedEvent1 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent2 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent3 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent4 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent5 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleRecurringEvent, false);
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedEvent1, model.getAllEvents().get(0));
    assertEquals(expectedEvent2, model.getAllEvents().get(1));
    assertEquals(expectedEvent3, model.getAllEvents().get(2));
    assertEquals(expectedEvent4, model.getAllEvents().get(3));
    assertEquals(expectedEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void createRecurringAllDayEventWithUntilDate() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0, 0))
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO expectedEvent1 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent2 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent3 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent4 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    EventDTO expectedEvent5 = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 0, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .setIsAllDay(true)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleRecurringEvent, false);
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedEvent1, model.getAllEvents().get(0));
    assertEquals(expectedEvent2, model.getAllEvents().get(1));
    assertEquals(expectedEvent3, model.getAllEvents().get(2));
    assertEquals(expectedEvent4, model.getAllEvents().get(3));
    assertEquals(expectedEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void createConflictRecurringSimpleEventWithOccurrence() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO conflictingEvent = EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 8, 11, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 12, 30, 0))
        .build();
    model.createEvent(conflictingEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertThrows(EventConflictException.class,
        () -> model.createEvent(simpleRecurringEvent, false));
    assertEquals(1, model.getAllEvents().size());
  }

  @Test
  public void createConflictRecurringSimpleEventWithUntilDate() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0, 0))
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO conflictingEvent = EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 8, 11, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 12, 30, 0))
        .build();
    model.createEvent(conflictingEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertThrows(EventConflictException.class,
        () -> model.createEvent(simpleRecurringEvent, false));
    assertEquals(1, model.getAllEvents().size());
  }

  @Test
  public void createConflictRecurringAllDayEventWithOccurrence() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO conflictingEvent = EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 8, 11, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 12, 30, 0))
        .build();
    model.createEvent(conflictingEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertThrows(EventConflictException.class,
        () -> model.createEvent(simpleRecurringEvent, false));
    assertEquals(1, model.getAllEvents().size());
  }

  @Test
  public void createConflictRecurringAllDayEventWithUntilDate() {
    RecurringDetailsDTO recurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0, 0))
        .build();
    EventDTO simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(recurDetails)
        .build();
    EventDTO conflictingEvent = EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 8, 11, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 12, 30, 0))
        .build();
    model.createEvent(conflictingEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertThrows(EventConflictException.class,
        () -> model.createEvent(simpleRecurringEvent, false));
    assertEquals(1, model.getAllEvents().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createNullEvent() {
    model.createEvent(null, false);
  }

  @Test(expected = InvalidDateTimeRangeException.class)
  public void createNullStartTime() {
    model.createEvent(EventDTO.getBuilder().build(), false);
  }

  @Test(expected = InvalidDateTimeRangeException.class)
  public void createNullEndTimeForNonAllDayEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setIsAllDay(false)
        .build(), false);
  }

  @Test(expected = InvalidDateTimeRangeException.class)
  public void createStartTimeAfterEndTimeEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 12, 11, 13, 0))
        .setEndTime(LocalDateTime.of(2025, 12, 11, 12, 0))
        .build(), false);
  }

  @Test(expected = InvalidEventDetailsException.class)
  public void createNullRecurringDetailsForRecurringEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsRecurring(true)
        .build(), false);
  }

  @Test(expected = InvalidEventDetailsException.class)
  public void createRecurringEventSpanningAcrossDays() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 1, 0))
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder().build())
        .build(), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createNullRepeatDayRecurringEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 12, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 12, 1, 13, 0))
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder().build())
        .build(), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createNullOccurrenceDayAndUntilTimeRecurringEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 12, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 12, 1, 13, 0))
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder()
            .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
            .build())
        .build(), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createWithBothOccurrenceDayAndUntilTimeRecurringEvent() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 12, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 12, 1, 13, 0))
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder()
            .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
            .setOccurrences(5)
            .setUntilDate(LocalDateTime.of(2026, 12, 1, 12, 0))
            .build())
        .build(), false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createRecurrenceEventWithUntilDateBeforeStartTime() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 12, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 12, 1, 13, 0))
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder()
            .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
            .setOccurrences(5)
            .setUntilDate(LocalDateTime.of(2025, 11, 1, 12, 0))
            .build())
        .build(), false);
  }

  @Test
  public void createSimpleEventWithConflictOverlapCase() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 12, 0))
        .build(), true);
    assertThrows(EventConflictException.class,
        () -> model.createEvent(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 1))
            .setEndTime(LocalDateTime.of(2025, 1, 10, 12, 30))
            .build(), true));
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 1))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 12, 30))
        .build(), false);
    assertEquals(2, model.getAllEvents().size());
  }

  @Test
  public void createSimpleEventWithConflictLeftOverlapCase() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 12, 0))
        .build(), true);
    assertThrows(EventConflictException.class,
        () -> model.createEvent(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0))
            .setEndTime(LocalDateTime.of(2025, 1, 10, 12, 30))
            .build(), true));
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 12, 30))
        .build(), false);
    assertEquals(2, model.getAllEvents().size());
  }

  @Test
  public void createSimpleEventWithConflictRightOverlapCase() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 12, 0))
        .build(), true);
    assertThrows(EventConflictException.class,
        () -> model.createEvent(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.of(2025, 1, 11, 11, 0))
            .setEndTime(LocalDateTime.of(2025, 1, 11, 14, 30))
            .build(), true));
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 11, 11, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 14, 30))
        .build(), false);
    assertEquals(2, model.getAllEvents().size());
  }

  @Test
  public void createSimpleEventWithConflictMaskCase() {
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 12, 0))
        .build(), true);
    assertThrows(EventConflictException.class,
        () -> model.createEvent(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0))
            .setEndTime(LocalDateTime.of(2025, 1, 11, 14, 30))
            .build(), true));
    model.createEvent(EventDTO.getBuilder()
        .setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 14, 30))
        .build(), false);
    assertEquals(2, model.getAllEvents().size());
  }

}
