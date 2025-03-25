import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import model.CalendarDayOfWeek;
import org.junit.Before;
import org.junit.Test;

/**
 * This class represents a test suite for the ExtendedCopyEventCommand class. It tests the
 * functionality of copying events between calendars.
 */
public class ExtendedCopyEventCommandTest extends AbstractCommandTest {

  @Before
  @Override
  public void setUp() {
    super.setUp();
    setupTestCalendar();
  }

  private void setupTestCalendar() {
    getDisplayMessageWithInput("create calendar --name test_calendar --timezone Asia/Kolkata");
    getDisplayMessageWithInput("use calendar --name test_calendar");
  }

  @Test
  public void testCopySingleEventNotFound() {
    mockModel.setGetEventsInRange = new ArrayList<>(); // set empty list to simulate no events
    mockModel.eventNameFilter = "Non-existent Meeting";

    assertEquals("No events were copied to default",
        getDisplayMessageWithInput(
            "copy event \"Non-existent Meeting\" on 2025-04-01T12:00 "
                + "--target default to 2025-04-08T14:00"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.createEventCalled);
  }

  @Test
  public void testCopySingleSimpleEvent() {
    // create a single event to copy
    List<EventDTO> eventsToReturn = new ArrayList<>();
    EventDTO event = EventDTO.getBuilder()
        .setSubject("Simple Meeting")
        .setStartTime(LocalDateTime.of(2025, 4, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    eventsToReturn.add(event);

    // set the mock model to return this event when queried
    mockModel.setGetEventsInRange = eventsToReturn;
    mockModel.eventNameFilter = "Simple Meeting";

    assertEquals("Successfully copied 1 event(s) to default",
        getDisplayMessageWithInput(
            "copy event \"Simple Meeting\" on 2025-04-01T12:00 "
                + "--target default to 2025-04-08T14:00"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.exportEventsWithExporterCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("default", mockModel.targetCalendarName);
    assertEquals(LocalDateTime.of(2025, 4, 8, 14, 0),
        mockModel.targetStartDateTime);
  }

  @Test
  public void testCopySingleEventWithConflict() {
    // create a single event to copy
    List<EventDTO> eventsToReturn = new ArrayList<>();
    EventDTO event = EventDTO.getBuilder()
        .setSubject("Meeting")
        .setStartTime(LocalDateTime.of(2025, 4, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    eventsToReturn.add(event);

    // set the mock model to return this event when queried
    mockModel.setGetEventsInRange = eventsToReturn;
    mockModel.eventNameFilter = "Meeting";
    mockModel.shouldThrowEventConflictException = true;

    String input = "copy event Meeting on 2025-04-01T12:00 --target default to 2025-04-01T12:30";

    String expectedOutput = "No events were copied to default\n"
        + "The following events were not copied due to conflicts:\n"
        + "- Event Conflict: Meeting on 2025-04-01T12:30";

    String actualOutput = getDisplayMessageWithInput(input);

    assertEquals(expectedOutput, actualOutput);
    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
    assertEquals("default", mockModel.targetCalendarName);
    assertEquals(LocalDateTime.of(2025, 4, 1, 12, 30),
        mockModel.targetStartDateTime);
  }

  @Test
  public void testCopyAllDayEvent() {
    // create an all-day event to copy
    List<EventDTO> eventsToReturn = new ArrayList<>();
    EventDTO event = EventDTO.getBuilder()
        .setSubject("All Day Meeting")
        .setStartTime(LocalDateTime.of(2025, 4, 1, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .build();
    eventsToReturn.add(event);

    // set the mock model to return this event when queried
    mockModel.setGetEventsInRange = eventsToReturn;
    mockModel.eventIsAllDay = true;
    mockModel.eventNameFilter = "All Day Meeting";

    assertEquals("Successfully copied 1 event(s) to default",
        getDisplayMessageWithInput(
            "copy event \"All Day Meeting\" on 2025-04-01T00:00 "
                + "--target default to 2025-04-08T00:00"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
  }

  @Test
  public void testCopyEventsOnDate() {
    // create multiple events to copy on the date
    List<EventDTO> eventsToReturn = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      EventDTO event = EventDTO.getBuilder()
          .setSubject("Event " + (i + 1))
          .setStartTime(LocalDateTime.of(2025, 4, 1, 8 + i, 0))
          .setEndTime(LocalDateTime.of(2025, 4, 1, 9 + i, 0))
          .setIsAllDay(false)
          .setIsRecurring(false)
          .build();
      eventsToReturn.add(event);
    }
    mockModel.setGetEventsOnDate = eventsToReturn;
    mockModel.eventsOnDateCount = 3;

    assertEquals("Successfully copied 3 event(s) to default",
        getDisplayMessageWithInput("copy events on 2025-04-01 --target default to 2025-04-08"));

    assertTrue(mockModel.getEventsOnDateCalled);
    assertTrue(mockModel.createEventCalled);
    // check that the target date is set correctly
    assertEquals(LocalDate.of(2025, 4, 8), mockModel.targetStartDate);
  }

  @Test
  public void testCopyEventsOnDateNotFound() {
    // create empty list to mimic no events on date
    mockModel.setGetEventsOnDate = new ArrayList<>();
    mockModel.eventsOnDateCount = 0;

    assertEquals("No events were copied to default",
        getDisplayMessageWithInput("copy events on 2025-04-01 --target default to 2025-04-08"));

    assertTrue(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.createEventCalled);
  }

  @Test
  public void testCopyEventsOnDateWithOneConflict() {
    // create multiple events to copy that will have one conflict
    List<EventDTO> eventsToReturn = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      EventDTO event = EventDTO.getBuilder()
          .setSubject("Event " + (i + 1))
          .setStartTime(LocalDateTime.of(2025, 4, 1 + i,
              8 + i, 0))
          .setEndTime(LocalDateTime.of(2025, 4, 1 + i,
              9 + i, 0))
          .setIsAllDay(false)
          .setIsRecurring(false)
          .build();
      eventsToReturn.add(event);
    }

    mockModel.setGetEventsOnDate = eventsToReturn;
    mockModel.shouldThrowEventConflictException = true;
    mockModel.isBestEffortCopy = 2;

    assertEquals("Successfully copied 2 event(s) to default\n"
            + "The following events were not copied due to conflicts:\n"
            + "- Event Conflict: Event 3 on 2025-04-03T10:00",
        getDisplayMessageWithInput("copy events on 2025-04-01 --target default to 2025-04-03"));

    assertTrue(mockModel.getEventsOnDateCalled);
    assertTrue(mockModel.createEventCalled);
  }

  @Test
  public void testCopyEventsBetweenDates() {
    // create events to copy between the dates
    List<EventDTO> eventsToReturn = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      EventDTO event = EventDTO.getBuilder()
          .setSubject("Event " + (i + 1))
          .setStartTime(LocalDateTime.of(2025, 4, 1 + i,
              8 + i, 0))
          .setIsAllDay(true)
          .setIsRecurring(false)
          .build();
      eventsToReturn.add(event);
    }
    mockModel.setGetEventsInRange = eventsToReturn;

    assertEquals("Successfully copied 3 event(s) to default",
        getDisplayMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-05 --target default to 2025-04-08"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
  }

  @Test
  public void testCopyEventsBetweenDatesNotFound() {
    // no events to copy between the dates, set empty list
    mockModel.setGetEventsInRange = new ArrayList<>();

    assertEquals("No events were copied to default",
        getDisplayMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-05 --target default to 2025-04-08"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.createEventCalled);
  }

  @Test
  public void testCopyEventsBetweenDatesWithMultipleConflicts() {
    // create multiple events to copy that will have multiple conflicts
    List<EventDTO> eventsToReturn = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      EventDTO event = EventDTO.getBuilder()
          .setSubject("Event " + (i + 1))
          .setStartTime(LocalDateTime.of(2025, 4, 1 + i, 8 + i, 0))
          .setEndTime(LocalDateTime.of(2025, 4, 1 + i, 9 + i, 0))
          .setIsAllDay(i % 2 == 0) // create 1 all-day events for every 2 spanned events
          .setIsRecurring(false)
          .build();
      eventsToReturn.add(event);
    }

    // set the mock model to return this event when queried
    mockModel.setGetEventsInRange = eventsToReturn;
    mockModel.shouldThrowEventConflictException = true;
    mockModel.isBestEffortCopy = 3; // 3 events will be copied successfully

    assertEquals(
        "Successfully copied 3 event(s) to default\n"
            + "The following events were not copied due to conflicts:\n"
            + "- Event Conflict: Event 4 on 2025-04-08T11:00\n"
            + "- Event Conflict: Event 5 on 2025-04-08T12:00",
        getDisplayMessageWithInput(
            "copy events between 2025-04-01 and 2025-04-05 --target default to 2025-04-08"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
  }

  @Test
  public void testCopyRecurringEvent() {
    // create a recurring event to copy
    List<EventDTO> eventsToReturn = new ArrayList<>();
    EventDTO event = EventDTO.getBuilder()
        .setSubject("Weekly Meeting")
        .setStartTime(LocalDateTime.of(2025, 4, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder()
            .setRepeatDays(new HashSet<>(Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.W)))
            .setOccurrences(5)
            .build())
        .build();
    eventsToReturn.add(event);

    // set the mock model to return this event when queried
    mockModel.setGetEventsInRange = eventsToReturn;
    mockModel.eventNameFilter = "Weekly Meeting";
    mockModel.eventIsRecurring = true;

    assertEquals("Successfully copied 1 event(s) to default",
        getDisplayMessageWithInput(
            "copy event \"Weekly Meeting\" on 2025-04-01T12:00 "
                + "--target default to 2025-04-08T14:00"));

    assertTrue(mockModel.getEventsInRangeCalled);
    assertTrue(mockModel.createEventCalled);
    // recurring details should be reset (event copied is not recurring)
    assertFalse(mockModel.eventIsRecurring);
  }
}
