import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import dto.RecurringDetailsDTO;
import java.time.LocalDateTime;
import java.util.Set;
import model.CalendarDayOfWeek;
import org.junit.Before;
import org.junit.Test;

public class RecurringDetailsDTOTest {

  RecurringDetailsDTO recurDetails1;
  RecurringDetailsDTO recurDetails2;
  RecurringDetailsDTO recurDetails3;
  RecurringDetailsDTO recurDetails4;

  @Before
  public void setUp() {
    recurDetails1 = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.T, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(LocalDateTime.of(2020, 1, 1, 12, 0, 45, 100))
        .build();

    recurDetails2 = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.T, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(LocalDateTime.of(2020, 1, 1, 12, 0, 45, 100))
        .build();

    recurDetails3 = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.T, CalendarDayOfWeek.W))
        .setOccurrences(5)
        .setUntilDate(LocalDateTime.of(2020, 1, 1, 12, 0, 45, 100))
        .build();

    recurDetails4 = RecurringDetailsDTO.getBuilder()
        .setRepeatDays(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.T, CalendarDayOfWeek.W))
        .setOccurrences(7)
        .setUntilDate(LocalDateTime.of(2020, 1, 1, 12, 0, 45, 100))
        .build();
  }

  @Test
  public void fieldsShouldBeCorrectlySet() {
    assertEquals(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.T, CalendarDayOfWeek.W),
        recurDetails1.getRepeatDays());
    assertEquals(Integer.valueOf(5), recurDetails1.getOccurrences());
    assertEquals(LocalDateTime.of(2020, 1, 1, 12, 0, 45, 100), recurDetails1.getUntilDate());
  }

  @Test
  public void testEquality() {
    assertEquals(recurDetails1, recurDetails1); // Reflexive
    assertEquals(recurDetails1, recurDetails2); // Symmetric
    assertEquals(recurDetails2, recurDetails1);
    assertEquals(recurDetails2, recurDetails3); // Transitive
    assertEquals(recurDetails1, recurDetails3);

    assertNotEquals(recurDetails1, recurDetails4); // Negative
    assertNotEquals(recurDetails2, recurDetails4);
    assertNotEquals(recurDetails3, recurDetails4);
    assertNotEquals(recurDetails4, new Object());

  }

  @Test
  public void testHashCode() {
    assertEquals(recurDetails1.hashCode(), recurDetails1.hashCode()); // Reflexive
    assertEquals(recurDetails1.hashCode(), recurDetails2.hashCode()); // Symmetric
    assertEquals(recurDetails2.hashCode(), recurDetails1.hashCode());
    assertEquals(recurDetails2.hashCode(), recurDetails3.hashCode()); // Transitive
    assertEquals(recurDetails1.hashCode(), recurDetails3.hashCode());

    assertNotEquals(recurDetails1.hashCode(), recurDetails4.hashCode()); // Negative
    assertNotEquals(recurDetails2.hashCode(), recurDetails4.hashCode());
    assertNotEquals(recurDetails3.hashCode(), recurDetails4.hashCode());
  }
}