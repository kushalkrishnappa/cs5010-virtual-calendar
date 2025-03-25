import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import dto.EventDTO;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

public class EventDTOTest {

  private EventDTO event1;
  private EventDTO event2;
  private EventDTO event3;
  private EventDTO event4;

  @Before
  public void setUp() {
    event1 = EventDTO.getBuilder()
        .setSubject("Test Subject")
        .setDescription("Test Description")
        .setStartTime(LocalDateTime.of(2015, 12, 12, 1, 45, 56, 90))
        .setEndTime(LocalDateTime.of(1987, 10, 23, 11, 12, 34, 100))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .setIsPublic(true)
        .setLocation("Test Location")
        .build();

    event2 = EventDTO.getBuilder()
        .setSubject("Test Subject")
        .setDescription("Test Description")
        .setStartTime(LocalDateTime.of(2015, 12, 12, 1, 45, 56, 90))
        .setEndTime(LocalDateTime.of(1987, 10, 23, 11, 12, 34, 100))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .setIsPublic(true)
        .setLocation("Test Location")
        .build();

    event3 = EventDTO.getBuilder()
        .setSubject("Test Subject")
        .setDescription("Test Description")
        .setStartTime(LocalDateTime.of(2015, 12, 12, 1, 45, 56, 90))
        .setEndTime(LocalDateTime.of(1987, 10, 23, 11, 12, 34, 100))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .setIsPublic(true)
        .setLocation("Test Location")
        .build();

    event4 = EventDTO.getBuilder()
        .setSubject("Test Subject new")
        .setDescription("Test Description")
        .setStartTime(LocalDateTime.of(2015, 12, 12, 1, 45, 56, 90))
        .setEndTime(LocalDateTime.of(1987, 10, 23, 11, 12, 34, 100))
        .setIsAllDay(true)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .setIsPublic(true)
        .setLocation("Test Location")
        .build();
  }

  @Test
  public void fieldsShouldBeCorrectlySet() {
    assertEquals(event1.getSubject(), "Test Subject");
    assertEquals(event1.getDescription(), "Test Description");
    assertEquals(event1.getStartTime(), LocalDateTime.of(2015, 12, 12, 1, 45, 56, 90));
    assertEquals(event1.getEndTime(), LocalDateTime.of(1987, 10, 23, 11, 12, 34, 100));
    assertEquals(event1.getIsAllDay(), true);
    assertEquals(event1.getIsRecurring(), false);
    assertEquals(event1.getRecurringDetails(), null);
    assertEquals(event1.getLocation(), "Test Location");
    assertEquals(event1.getIsPublic(), true);
  }

  @Test
  public void testEquality() {
    assertEquals(event1, event1); // Reflexive
    assertEquals(event1, event2); // Symmetric
    assertEquals(event2, event1);
    assertEquals(event2, event3); // Transitive
    assertEquals(event1, event3);

    assertNotEquals(event1, event4); // Negative
    assertNotEquals(event2, event4);
    assertNotEquals(event3, event4);
    assertNotEquals(event4, new Object());

  }

  @Test
  public void testHashCode() {
    assertEquals(event1.hashCode(), event1.hashCode()); // Reflexive
    assertEquals(event1.hashCode(), event2.hashCode()); // Symmetric
    assertEquals(event2.hashCode(), event1.hashCode());
    assertEquals(event2.hashCode(), event3.hashCode()); // Transitive
    assertEquals(event1.hashCode(), event3.hashCode());

    assertNotEquals(event1.hashCode(), event4.hashCode()); // Negative
    assertNotEquals(event2.hashCode(), event4.hashCode());
    assertNotEquals(event3.hashCode(), event4.hashCode());
  }
}
