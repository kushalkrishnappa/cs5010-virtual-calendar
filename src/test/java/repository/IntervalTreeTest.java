package repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for IntervalTree.
 */
public class IntervalTreeTest {

  EventDTO event1;
  EventDTO event2;
  EventDTO event3;
  EventDTO event4;
  EventDTO event5;
  EventDTO event6;
  EventDTO event7;
  EventDTO event8;
  EventDTO event9;
  EventDTO event10;

  private IntervalTree tree;

  @Before
  public void setUp() {
    tree = new IntervalTree();
    event1 = EventDTO.getBuilder()
        .setSubject("event1")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 12))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 18, 12))
        .build();
    event7 = EventDTO.getBuilder()
        .setSubject("event7")
        .setStartTime(LocalDateTime.of(2025, 1, 1, 12, 12))
        .setEndTime(LocalDateTime.of(2025, 1, 1, 19, 45))
        .build();
    event3 = EventDTO.getBuilder()
        .setSubject("event3")
        .setStartTime(LocalDateTime.of(2025, 1, 4, 6, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 4, 10, 45))
        .build();
    event6 = EventDTO.getBuilder()
        .setSubject("event6")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 0, 0))
        .build();
    event5 = EventDTO.getBuilder()
        .setSubject("event5")
        .setStartTime(LocalDateTime.of(2025, 1, 6, 0, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 6, 19, 45))
        .build();
    event4 = EventDTO.getBuilder()
        .setSubject("event4")
        .setStartTime(LocalDateTime.of(2025, 1, 8, 12, 55))
        .setEndTime(LocalDateTime.of(2025, 1, 8, 12, 55))
        .build();
    event2 = EventDTO.getBuilder()
        .setSubject("event2")
        .setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 10, 14, 30))
        .build();
    event8 = EventDTO.getBuilder()
        .setSubject("event8")
        .setStartTime(LocalDateTime.of(2025, 1, 19, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 19, 14, 30))
        .build();
    event9 = EventDTO.getBuilder()
        .setSubject("event9")
        .setStartTime(LocalDateTime.of(2025, 1, 19, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 19, 14, 30))
        .build();
    event10 = EventDTO.getBuilder()
        .setSubject("event10")
        .setStartTime(LocalDateTime.of(2025, 1, 7, 9, 0))
        .setEndTime(LocalDateTime.of(2025, 1, 7, 15, 30))
        .build();
  }

  @Test
  public void testBalance() {

    assertTrue(tree.insert(event1));
    assertTrue(tree.insert(event2));
    assertTrue(tree.insert(event3));
    assertTrue(tree.insert(event4));
    assertTrue(tree.insert(event5));
    assertTrue(tree.insert(event6));
    assertTrue(tree.insert(event7));
    assertTrue(tree.insert(event8));
    assertTrue(tree.insert(event9));
    assertTrue(tree.insert(event10));

    /*
      Expected State:
      [e4]
      [e3]               [e2]
      [e1,e7] [e5,e6]    [] [e8,e9]
      [] []   [] [e10]      [] []
                 [] []
     */
    List<EventDTO> allEvents = tree.getAllEvents();
    assertEquals(event1, allEvents.get(0));
    assertEquals(event7, allEvents.get(1));
    assertEquals(event3, allEvents.get(2));
    assertEquals(event5, allEvents.get(3));
    assertEquals(event6, allEvents.get(4));
    assertEquals(event10, allEvents.get(5));
    assertEquals(event4, allEvents.get(6));
    assertEquals(event2, allEvents.get(7));
    assertEquals(event8, allEvents.get(8));
    assertEquals(event9, allEvents.get(9));
  }

  @Test
  public void testOverlapAtTime() {
    assertTrue(tree.insert(event1));
    assertTrue(tree.insert(event2));
    assertTrue(tree.insert(event3));
    assertTrue(tree.insert(event4));
    assertTrue(tree.insert(event5));
    assertTrue(tree.insert(event6));
    assertTrue(tree.insert(event7));
    assertTrue(tree.insert(event8));
    assertTrue(tree.insert(event9));
    assertTrue(tree.insert(event10));

    List<EventDTO> eventDTOS = tree.searchOverlappingPoint(LocalDateTime.of(2026, 1, 1, 12, 12));
    assertEquals(0, eventDTOS.size());
    eventDTOS = tree.searchOverlappingPoint(LocalDateTime.of(2025, 1, 1, 12, 30));
    assertEquals(2, eventDTOS.size());
    assertTrue(eventDTOS.containsAll(List.of(event1, event7)));

    eventDTOS = tree.searchOverlappingPoint(LocalDateTime.of(2025, 1, 7, 9, 0));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));
  }

  @Test
  public void testOverlapInterval() {
    assertTrue(tree.insert(event1));
    assertTrue(tree.insert(event2));
    assertTrue(tree.insert(event3));
    assertTrue(tree.insert(event4));
    assertTrue(tree.insert(event5));
    assertTrue(tree.insert(event6));
    assertTrue(tree.insert(event7));
    assertTrue(tree.insert(event8));
    assertTrue(tree.insert(event9));
    assertTrue(tree.insert(event10));

    List<EventDTO> eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2026, 1, 1, 12, 12),
        LocalDateTime.of(2026, 1, 1, 12, 12));
    assertEquals(0, eventDTOS.size());

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 1, 12, 30),
        LocalDateTime.of(2025, 1, 1, 13, 30));
    assertEquals(2, eventDTOS.size());
    assertTrue(eventDTOS.containsAll(List.of(event1, event7)));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 6, 0, 0),
        LocalDateTime.of(2025, 1, 7, 10, 0)
    );
    assertEquals(3, eventDTOS.size());
    assertTrue(eventDTOS.containsAll(List.of(event5, event6, event10)));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 10, 0),
        LocalDateTime.of(2025, 1, 19, 12, 0));
    assertEquals(3, eventDTOS.size());
    assertTrue(eventDTOS.containsAll(List.of(event10, event4, event2)));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 8, 0),
        LocalDateTime.of(2025, 1, 7, 9, 0));
    assertEquals(0, eventDTOS.size());

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 8, 0),
        LocalDateTime.of(2025, 1, 7, 9, 1));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 15, 30),
        LocalDateTime.of(2025, 1, 7, 19, 0));
    assertEquals(0, eventDTOS.size());

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 15, 29),
        LocalDateTime.of(2025, 1, 7, 19, 0));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 8, 0),
        LocalDateTime.of(2025, 1, 7, 19, 0));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 10, 0),
        LocalDateTime.of(2025, 1, 7, 11, 0));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 7, 9, 0),
        LocalDateTime.of(2025, 1, 7, 11, 0));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event10));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 1, 19, 30),
        LocalDateTime.of(2025, 1, 1, 19, 50));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event7));

    eventDTOS = tree.searchOverlapping(
        LocalDateTime.of(2025, 1, 8, 12, 55),
        LocalDateTime.of(2025, 1, 9, 12, 55));
    assertEquals(1, eventDTOS.size());
    assertTrue(eventDTOS.contains(event4));


  }

  @Test
  public void testGetByName() {
    assertTrue(tree.insert(event1));
    assertTrue(tree.insert(event1));
    assertTrue(tree.insert(event2));
    assertTrue(tree.insert(event3));
    assertTrue(tree.insert(event4));
    assertTrue(tree.insert(event5));
    assertTrue(tree.insert(event6));
    assertTrue(tree.insert(event7));
    assertTrue(tree.insert(event8));
    assertTrue(tree.insert(event9));
    assertTrue(tree.insert(event10));

    List<EventDTO> events = tree.findByName("event1");
    assertEquals(2, events.size());
    assertEquals(event1, events.get(0));
    assertEquals(event1, events.get(1));

    events = tree.findByName("event8");
    assertEquals(1, events.size());
    assertEquals(event8, events.get(0));

    events = tree.findByName("event9");
    assertEquals(1, events.size());
    assertEquals(event9, events.get(0));

    events = tree.findByName("event10");
    assertEquals(1, events.size());
    assertEquals(event10, events.get(0));
  }
}
