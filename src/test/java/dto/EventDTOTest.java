package dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This class tests the EventDTO class.
 */
public class EventDTOTest {

  /**
   * Test the EventDTO builder return EventDTO object and does not return null.
   */
  @Test
  public void testEventDTOBuilderDoesNotReturnNull() {
    IEventDTO event = EventDTO.getBuilder().build();
    assertNotNull(event);
    assertEquals(EventDTO.class.toString(), event.getClass().toString());
  }

  /**
   * Test the getBuilder method in the EventDTO class. It should return an empty EventDTO object.
   */
  @Test
  public void testGetBuilderWithoutAttributesReturnsEmptyEventDTO() {
    IEventDTO emptyEvent = EventDTO.getBuilder().build();
    assertNull(emptyEvent.getSubject());
    assertNull(emptyEvent.getStartTime());
    assertNull(emptyEvent.getEndTime());
    assertNull(emptyEvent.getDescription());
    assertNull(emptyEvent.getLocation());
    assertNull(emptyEvent.isPublic());
  }

  /**
   * Test the EventDTO object is created with the given attributes on using builder.
   */
  @Test
  public void testGetBuilderWithAttributesReturnsEventDTO() {
    IEventDTO event = EventDTO.getBuilder()
        .setSubject("Subject")
        .setStartTime(null)
        .setEndTime(null)
        .setDescription("Description")
        .setLocation("Location")
        .setIsPublic(true)
        .build();
    assertNotNull(event);
    assertEquals("Subject", event.getSubject());
    assertNull(event.getStartTime());
    assertNull(event.getEndTime());
    assertEquals("Description", event.getDescription());
    assertEquals("Location", event.getLocation());
    assertTrue(event.isPublic());
  }

}
