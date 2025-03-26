import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import exception.EventConflictException;
import exception.InvalidDateTimeRangeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.CalendarDayOfWeek;
import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;

public class EditEventWithNameTest {

  private CalendarModel model;

  private List<EventDTO> simpleRecurringExpectedEvents;
  private EventDTO simpleRecurringEvent;
  private RecurringDetailsDTO simpleRecurEventRecurDetails;


  private EventDTO expectedSimpleEvent;
  private EventDTO expectedAllDayEvent;


  @Before
  public void setUp() throws Exception {
    model = new CalendarModel();
    simpleRecurringExpectedEvents = new ArrayList<>();
    simpleRecurEventRecurDetails = null;
    simpleRecurringEvent = null;
    expectedSimpleEvent = null;
    expectedAllDayEvent = null;
  }

  private void createSimpleRecurringEventsUsingOccurrences() {
    simpleRecurEventRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(5)
        .build();
    simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .build();
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    model.createEvent(simpleRecurringEvent, false);
  }

  private void createSimpleRecurringEventsUsingUntilDate() {
    simpleRecurEventRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setUntilDate(LocalDateTime.of(2025, 1, 12, 12, 0, 0))
        .build();
    simpleRecurringEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .build();
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 3, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 3, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    simpleRecurringExpectedEvents.add(EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 13, 0, 0))
        .setIsRecurring(true)
        .setRecurringDetails(simpleRecurEventRecurDetails)
        .setIsAllDay(false)
        .setIsPublic(false)
        .build()
    );
    model.createEvent(simpleRecurringEvent, false);
  }

  private void createSimpleEventWithSameNameInRecurringRange() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 9, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 13, 0, 0))
        .build();
    expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 9, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 9, 13, 0, 0))
        .setIsRecurring(false)
        .setIsPublic(false)
        .setIsAllDay(false)
        .build();
    model.createEvent(simpleEvent, false);
  }

  private void createAllDayEventWithSameNameInRecurringRange() {
    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 11, 12, 0, 0))
        .build();
    expectedAllDayEvent = EventDTO.getBuilder()
        .setSubject("simple Recurring")
        .setStartTime(LocalDateTime.of(2025, 1, 11, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 12, 0, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(allDayEvent, false);
  }


  @Test(expected = IllegalArgumentException.class)
  public void updateUnknownEvent() {
    model.editEvent("Unknown Event", null, null, EventDTO.getBuilder().build());
  }

  @Test
  public void updateAllOccurrenceRecurring() {
    createSimpleRecurringEventsUsingOccurrences();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .build();
    assertEquals(Integer.valueOf(5),
        model.editEvent("simple Recurring", null, null, updateRequest));
    List<EventDTO> expectedUpdatedEvents = simpleRecurringExpectedEvents.stream().map(event ->
        EventDTO.getBuilder()
            .setSubject("simple Recurring updated")
            .setStartTime(event.getStartTime())
            .setEndTime(event.getEndTime())
            .setDescription("Updated Description")
            .setLocation("Updated Location")
            .setIsPublic(true)
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(simpleRecurEventRecurDetails)
            .build()).collect(Collectors.toList());
    assertEquals(expectedUpdatedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(expectedUpdatedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(expectedUpdatedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(expectedUpdatedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(expectedUpdatedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }

  @Test
  public void updateAllUntilRecurring() {
    createSimpleRecurringEventsUsingUntilDate();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .build();
    assertEquals(Integer.valueOf(5),
        model.editEvent("simple Recurring", null, null, updateRequest));
    List<EventDTO> expectedUpdatedEvents = simpleRecurringExpectedEvents.stream().map(event ->
        EventDTO.getBuilder()
            .setSubject("simple Recurring updated")
            .setStartTime(event.getStartTime())
            .setEndTime(event.getEndTime())
            .setDescription("Updated Description")
            .setLocation("Updated Location")
            .setIsPublic(true)
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(simpleRecurEventRecurDetails)
            .build()).collect(Collectors.toList());
    assertEquals(expectedUpdatedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(expectedUpdatedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(expectedUpdatedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(expectedUpdatedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(expectedUpdatedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }

  @Test
  public void updatePartOccurrenceRecurring() {
    createSimpleRecurringEventsUsingOccurrences();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .build();
    assertEquals(Integer.valueOf(2),
        model.editEvent("simple Recurring", LocalDateTime.of(2025, 1, 8, 0, 0), null,
            updateRequest));
    List<EventDTO> expectedUpdatedEvents = simpleRecurringExpectedEvents.stream().map(event ->
        EventDTO.getBuilder()
            .setSubject("simple Recurring updated")
            .setStartTime(event.getStartTime())
            .setEndTime(event.getEndTime())
            .setDescription("Updated Description")
            .setLocation("Updated Location")
            .setIsPublic(true)
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(simpleRecurEventRecurDetails)
            .build()).collect(Collectors.toList());
    assertEquals(simpleRecurringExpectedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(simpleRecurringExpectedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(simpleRecurringExpectedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(expectedUpdatedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(expectedUpdatedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }

  @Test
  public void updatePartUntilRecurring() {
    createSimpleRecurringEventsUsingUntilDate();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .build();
    assertEquals(Integer.valueOf(2),
        model.editEvent("simple Recurring", LocalDateTime.of(2025, 1, 8, 0, 0), null,
            updateRequest));
    List<EventDTO> expectedUpdatedEvents = simpleRecurringExpectedEvents.stream().map(event ->
        EventDTO.getBuilder()
            .setSubject("simple Recurring updated")
            .setStartTime(event.getStartTime())
            .setEndTime(event.getEndTime())
            .setDescription("Updated Description")
            .setLocation("Updated Location")
            .setIsPublic(true)
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(simpleRecurEventRecurDetails)
            .build()).collect(Collectors.toList());
    assertEquals(simpleRecurringExpectedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(simpleRecurringExpectedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(simpleRecurringExpectedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(expectedUpdatedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(expectedUpdatedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }

  @Test
  public void updateIncreaseOccurrenceCount() {
    createSimpleRecurringEventsUsingOccurrences();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();

    RecurringDetailsDTO updateRequestRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(6)
        .build();

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRequestRecurDetails)
        .build();

    assertEquals(Integer.valueOf(5),
        model.editEvent("simple Recurring", null, null, updateRequest));

    List<EventDTO> expectedUpdatedEvents = simpleRecurringExpectedEvents.stream().map(event ->
        EventDTO.getBuilder()
            .setSubject("simple Recurring updated")
            .setStartTime(event.getStartTime())
            .setEndTime(event.getEndTime())
            .setDescription("Updated Description")
            .setLocation("Updated Location")
            .setIsPublic(true)
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(updateRequestRecurDetails)
            .build()).collect(Collectors.toList());

    assertEquals(expectedUpdatedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(expectedUpdatedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(expectedUpdatedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(expectedUpdatedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(expectedUpdatedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
    assertEquals(EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setStartTime(LocalDateTime.of(2025, 1, 13, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 13, 13, 0, 0))
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(updateRequestRecurDetails)
        .build(), model.getAllEvents().get(7));
  }

  @Test
  public void updateConflictWithIncreaseOccurrenceCount() {
    createSimpleRecurringEventsUsingOccurrences();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();

    model.createEvent(EventDTO.getBuilder()
        .setSubject("conflict event")
        .setStartTime(LocalDateTime.of(2025, 1, 13, 10, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 13, 13, 0, 0))
        .build(), false);

    RecurringDetailsDTO updateRequestRecurDetails = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F))
        .setOccurrences(6)
        .build();

    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setRecurringDetails(updateRequestRecurDetails)
        .build();

    assertThrows(EventConflictException.class,
        () -> model.editEvent("simple Recurring", null, null, updateRequest));

    assertEquals(simpleRecurringExpectedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(simpleRecurringExpectedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(simpleRecurringExpectedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(simpleRecurringExpectedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(simpleRecurringExpectedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }

  @Test
  public void updateWithEndTimeBeforeStartTime() {
    createSimpleRecurringEventsUsingOccurrences();
    createSimpleEventWithSameNameInRecurringRange();
    createAllDayEventWithSameNameInRecurringRange();
    EventDTO updateRequest = EventDTO.getBuilder()
        .setSubject("simple Recurring updated")
        .setDescription("Updated Description")
        .setLocation("Updated Location")
        .setIsPublic(true)
        .setEndTime(LocalDateTime.of(2025, 1, 1, 10, 0, 0))
        .build();
    assertThrows(InvalidDateTimeRangeException.class,
        () -> model.editEvent("simple Recurring", null, null, updateRequest));

    assertEquals(simpleRecurringExpectedEvents.get(0), model.getAllEvents().get(0));
    assertEquals(simpleRecurringExpectedEvents.get(1), model.getAllEvents().get(1));
    assertEquals(simpleRecurringExpectedEvents.get(2), model.getAllEvents().get(2));
    assertEquals(simpleRecurringExpectedEvents.get(3), model.getAllEvents().get(3));
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(4));
    assertEquals(simpleRecurringExpectedEvents.get(4), model.getAllEvents().get(5));
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(6));
  }


}
