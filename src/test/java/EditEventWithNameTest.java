import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDateTime;
import java.util.Set;
import model.CalendarDayOfWeek;
import model.CalendarModel;
import org.junit.Before;

public class EditEventWithNameTest {

  private CalendarModel model;

  @Before
  public void setUp() throws Exception {
    model = new CalendarModel();
  }

  private void createSimpleRecurringEventsUsingOccurrences() {
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
  }

  private void createSimpleRecurringEventsUsingUntilDate() {
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
  }
}
