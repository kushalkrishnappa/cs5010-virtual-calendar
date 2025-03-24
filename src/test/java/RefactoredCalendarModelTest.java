import static org.junit.Assert.assertEquals;

import dto.EventDTO;
import java.time.LocalDateTime;
import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;

public class RefactoredCalendarModelTest {

  private CalendarModel model;

  @Before
  public void setUp() throws Exception {
    model = new CalendarModel();
  }

  @Test
  public void testEmptyModel() {
    assertEquals(0, model.getAllEvents().size());
  }

  @Test
  public void testCreateSimpleEvent() {
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 01, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 01, 1, 13, 0, 0))
        .build();
    EventDTO expectedSimpleEvent = EventDTO.getBuilder()
        .setSubject("simple Event")
        .setStartTime(LocalDateTime.of(2025, 01, 1, 12, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 01, 1, 13, 0, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(simpleEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedSimpleEvent, model.getAllEvents().get(0));
  }

  @Test
  public void testCreateAllDayEvent() {
    EventDTO allDayEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 01, 1, 12, 0, 0))
        .build();
    EventDTO expectedAllDayEvent = EventDTO.getBuilder()
        .setSubject("all day Event")
        .setStartTime(LocalDateTime.of(2025, 01, 1, 0, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 01, 2, 0, 0, 0))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setIsPublic(false)
        .build();
    model.createEvent(allDayEvent, false);
    assertEquals(1, model.getAllEvents().size());
    assertEquals(expectedAllDayEvent, model.getAllEvents().get(0));
  }

}
