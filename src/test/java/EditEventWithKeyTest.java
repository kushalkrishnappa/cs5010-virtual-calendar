import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import java.time.LocalDateTime;
import java.util.Set;
import model.CalendarDayOfWeek;
import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for EditEventWithKey.
 */
public class EditEventWithKeyTest {

  private CalendarModel model;

  @Before
  public void setUp() throws Exception {
    model = new CalendarModel();
  }


  // Edit Events
  @Test(expected = IllegalArgumentException.class)
  public void nullEditDetails() {
    model.editEvent("Test Event", LocalDateTime.of(2025, 1, 2, 12, 0), null, null);
  }

  // Edit Events With Key
  @Test(expected = IllegalArgumentException.class)
  public void editUnknownEvent() {
    model.editEvent("Unknown Event", LocalDateTime.of(2025, 1, 2, 12, 0), null,
        EventDTO.getBuilder()
            .setSubject("Test Subject")
            .build());
  }

  @Test
  public void editSimpleDetailsOfSimpleEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    EventDTO expectedUpdateEvent = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 12, 0),
        LocalDateTime.of(2025, 1, 1, 13, 0),
        updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent, model.getAllEvents().get(0));
  }

  @Test
  public void editSimpleDetailsOfAllDayEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 15, 0, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    EventDTO expectedUpdateEvent = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 15, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 16, 0, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .setIsAllDay(true)
        .setIsRecurring(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 2, 0, 0),
        updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent, model.getAllEvents().get(0));
  }

  @Test
  public void editEndTimeOfAllDayEvent() {
    // convert all day event to simple event
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setEndTime(LocalDateTime.of(2025, 1, 15, 12, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    EventDTO expectedUpdateEvent = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 15, 12, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 2, 0, 0),
        updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent, model.getAllEvents().get(0));
  }

  @Test
  public void editStartTimeOfAllDayEvent() {
    // convert all day event to simple event
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 15, 12, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    EventDTO expectedUpdateEvent = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 15, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 16, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .setIsAllDay(true)
        .setIsRecurring(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 2, 0, 0),
        updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent, model.getAllEvents().get(0));
  }

  // conflict in editing simple event
  @Test
  public void editSimpleEventToConflictWithItself() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 30, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 30, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    EventDTO expectedUpdateEvent = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 30, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 30, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 12, 0),
        LocalDateTime.of(2025, 1, 1, 13, 0),
        updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent, model.getAllEvents().get(0));
  }

  @Test
  public void editSimpleEventToConflictWithAnotherEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 15, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 16, 0))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 15, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 16, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 15, 30, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 16, 30, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    assertThrows(EventConflictException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  // conflict in editing all-day event
  @Test
  public void editAllDayEventToConflictWithAnotherEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 15, 0))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 1, 30, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    assertThrows(EventConflictException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 0, 0),
            LocalDateTime.of(2025, 1, 2, 0, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  @Test
  public void editSimpleEventToRecurringEventUsingOccurrence() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();
    EventDTO expectedUpdateEvent1 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent2 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent3 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent4 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent5 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 12, 0),
        LocalDateTime.of(2025, 1, 1, 13, 0),
        updateRequest));
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent1, model.getAllEvents().get(0));
    assertEquals(expectedUpdateEvent2, model.getAllEvents().get(1));
    assertEquals(expectedUpdateEvent3, model.getAllEvents().get(2));
    assertEquals(expectedUpdateEvent4, model.getAllEvents().get(3));
    assertEquals(expectedUpdateEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void editSimpleEventToRecurringEventUsingUntilDate() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0))
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();
    EventDTO expectedUpdateEvent1 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent2 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent3 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent4 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent5 = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("simple Event",
        LocalDateTime.of(2025, 1, 1, 12, 0),
        LocalDateTime.of(2025, 1, 1, 13, 0),
        updateRequest));
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent1, model.getAllEvents().get(0));
    assertEquals(expectedUpdateEvent2, model.getAllEvents().get(1));
    assertEquals(expectedUpdateEvent3, model.getAllEvents().get(2));
    assertEquals(expectedUpdateEvent4, model.getAllEvents().get(3));
    assertEquals(expectedUpdateEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void editAllDayEventToRecurringEventUsingOccurrence() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();
    EventDTO expectedUpdateEvent1 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent2 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent3 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent4 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent5 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("all day Event",
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 2, 0, 0),
        updateRequest));
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent1, model.getAllEvents().get(0));
    assertEquals(expectedUpdateEvent2, model.getAllEvents().get(1));
    assertEquals(expectedUpdateEvent3, model.getAllEvents().get(2));
    assertEquals(expectedUpdateEvent4, model.getAllEvents().get(3));
    assertEquals(expectedUpdateEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void editAllDayEventToRecurringEventUsingUntilDate() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 0, 0))
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();
    EventDTO expectedUpdateEvent1 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent2 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent3 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent4 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    EventDTO expectedUpdateEvent5 = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 11, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRecurDetails)
        .setIsPublic(false)
        .build();
    assertEquals(Integer.valueOf(1), model.editEvent("all day Event",
        LocalDateTime.of(2025, 1, 1, 0, 0),
        LocalDateTime.of(2025, 1, 2, 0, 0),
        updateRequest));
    assertEquals(5, model.getAllEvents().size());
    assertEquals(expectedUpdateEvent1, model.getAllEvents().get(0));
    assertEquals(expectedUpdateEvent2, model.getAllEvents().get(1));
    assertEquals(expectedUpdateEvent3, model.getAllEvents().get(2));
    assertEquals(expectedUpdateEvent4, model.getAllEvents().get(3));
    assertEquals(expectedUpdateEvent5, model.getAllEvents().get(4));
  }

  @Test
  public void editSimpleEventToRecurringConflictEventUsingOccurrence() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 30))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 30))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 30))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 30))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();

    assertThrows(EventConflictException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  @Test
  public void editSimpleEventToRecurringConflictEventUsingUntilDate() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 30))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 30))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 30))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 30))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0))
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();

    assertThrows(EventConflictException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  @Test
  public void editAllDayEventToRecurringConflictEventUsingOccurrence() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 15, 0))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();

    assertThrows(EventConflictException.class,
        () -> model.editEvent("all day Event",
            LocalDateTime.of(2025, 1, 1, 0, 0),
            LocalDateTime.of(2025, 1, 2, 0, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  @Test
  public void editAllDayEventToRecurringConflictEventUsingUntilDate() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 2, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO conflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 15, 0))
        .build();
    EventDTO expectedConflictEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(conflictEvent, false);
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 15, 0))
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();

    assertThrows(EventConflictException.class,
        () -> model.editEvent("all day Event",
            LocalDateTime.of(2025, 1, 1, 0, 0),
            LocalDateTime.of(2025, 1, 2, 0, 0),
            updateRequest));
    assertEquals(2, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
    assertEquals(expectedConflictEvent, model.getAllEvents().get(1));
  }

  @Test
  public void editSimpleDetailsOfSimpleEventWithEndTimeBeforeStartTime() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("updated Subject")
        .setEndTime(LocalDateTime.of(2025, 1, 1, 11, 0, 0))
        .setLocation("updated Location")
        .setDescription("updated Description")
        .setIsPublic(true)
        .build();
    assertThrows(InvalidDateTimeRangeException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0),
            updateRequest));
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
  }

  @Test
  public void specifyBothOccurrenceAndUntilDateForUpdate() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));

    RecurringDetailsDTO updateRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(
            Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .setUntilDate(LocalDateTime.of(2025, 1, 30, 12, 0))
        .build();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setIsRecurring(true)
        .setRecurringDetails(
            updateRecurDetails
        )
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> model.editEvent("simple Event",
            LocalDateTime.of(2025, 1, 1, 12, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0),
            updateRequest));

    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
  }
}
