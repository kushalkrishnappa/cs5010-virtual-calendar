package repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for InMemoryEventRepository.
 */
public class InMemoryEventRepositoryTest {

  private InMemoryEventRepository repository;

  @Before
  public void setUp() {
    repository = new InMemoryEventRepository();
  }

  @Test
  public void testInsertNullEvent() {
    assertFalse(repository.insertEvent(null));
  }

  @Test
  public void testInsertEvent() {
    assertTrue(repository.insertEvent(EventDTO.getBuilder()
        .setSubject("Test")
        .setStartTime(LocalDateTime.of(2025, 10, 12, 12, 12))
        .setEndTime(LocalDateTime.of(2025, 10, 12, 12, 12))
        .build()));
  }

  @Test
  public void testDeleteNonExistentEvent() {
    assertFalse(repository.deleteEvent("Non Existent Event",
        LocalDateTime.of(2025, 10, 12, 12, 30),
        LocalDateTime.of(2025, 10, 12, 12, 30)));
  }

  @Test
  public void testDeleteExistingEvent() {
    assertTrue(repository.insertEvent(EventDTO.getBuilder()
        .setSubject("Test")
        .setStartTime(LocalDateTime.of(2025, 10, 12, 0, 12))
        .setEndTime(LocalDateTime.of(2025, 10, 13, 0, 12))
        .build()));
    assertTrue(repository.deleteEvent("Test",
        LocalDateTime.of(2025, 10, 12, 0, 12),
        LocalDateTime.of(2025, 10, 13, 0, 12)));
  }
}