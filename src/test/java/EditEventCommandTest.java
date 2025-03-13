import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import model.CalendarDayOfWeek;
import org.junit.Test;

public class EditEventCommandTest extends AbstractCommandTest {

  @Test
  public void invalidEmptyCommand() {
    assertEquals("Invalid command format: edit (event|events) <property> <eventName> "
            + "[from <startDateTime> [to <endDateTime>] with] "
            + "<newPropertyValue>",
        getErrorMessageWithInput("edit"));
  }

  @Test
  public void invalidEventName() {
    assertEquals("Invalid command format: edit (event|events) ...",
        getErrorMessageWithInput("edit eventName"));
  }

  @Test
  public void invalidSpannedPropertyName() {
    assertEquals("Invalid property name",
        getErrorMessageWithInput("edit event age"));
  }

  @Test
  public void invalidSpannedName() {
    assertEquals("Invalid command format: "
            + "edit event <property> <eventName> ...",
        getErrorMessageWithInput("edit event name "));
  }

  @Test
  public void invalidSpannedNameDuring() {
    assertEquals("Invalid command format: edit event <property> <eventName> from ...",
        getErrorMessageWithInput("edit event name \"event name\" during"));
  }

  @Test
  public void invalidSpannedStartDateTime() {
    assertEquals("Invalid startDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("edit event name eventName from 2025/10/12T10:00"));
  }

  @Test
  public void invalidSpannedStartDateTimeUptill() {
    assertEquals("Invalid command format: edit event <property> <eventName> "
            + "from <dateStringTtimeString> to ...",
        getErrorMessageWithInput("edit event name eventName from 2025-10-12T10:00 uptill"));
  }

  @Test
  public void invalidSpannedEndDateTime() {
    assertEquals("Invalid endDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("edit event name eventName "
            + "from 2025-10-12T10:00 to 2025/10/12T10:00"));
  }

  @Test
  public void invalidSpannedEndDateTimeTo() {
    assertEquals("Invalid command format: edit event <property> <eventName> "
            + "from <dateStringTtimeString> to <dateStringTtimeString> with ...",
        getErrorMessageWithInput("edit event name eventName "
            + "from 2025-10-12T10:00 to 2025-10-12T10:00 to"));
  }

  @Test
  public void invalidSpannedPropertyDateValue() {
    assertEquals("Invalid update Date format provided",
        getErrorMessageWithInput("edit event startDateTime eventName "
            + "from 2025-10-12T10:00 to 2025-10-12T10:00 with 2025-12-12T12"));
  }

  @Test
  public void invalidRecurringEventName() {
    assertEquals("Invalid command format: edit events <property> <eventName> ...",
        getErrorMessageWithInput("edit events name "));
  }

  @Test
  public void invalidRecurringEventDuring() {
    assertEquals("Invalid command format: edit events <property> <eventName> "
            + "(from|with) ...",
        getErrorMessageWithInput("edit events name eventName during"));
  }

  @Test
  public void invalidRecurringEventStartDateTime() {
    assertEquals("Invalid startDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("edit events startDateTime eventName from 2025/10/12T10:00"));
  }

  @Test
  public void invalidRecurringEventStartDateTimeTo() {
    assertEquals("Invalid command format: edit events <property> <eventName> "
            + "from <dateStringTtimeString> with ...",
        getErrorMessageWithInput("edit events name eventName from 2025-10-12T10:00 to"));
  }

  @Test
  public void invalidRecurringEventPropertyValue() {
    assertEquals("Invalid command format: "
            + "edit events <property> <eventName> "
            + "[from <dateStringTtimeString>] with <propertyValue>",
        getErrorMessageWithInput("edit events name eventName from 2025-10-12T10:00 with"));
  }

  @Test
  public void modelThrowsConflictException() {
    mockModel.shouldThrowEventConflictException = true;
    assertEquals("Event conflict thrown by MockModel",
        getErrorMessageWithInput("edit events name eventName from 2025-10-12T10:00 "
            + "with updatedEventName"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
  }

  @Test
  public void modelThrowsIllegalArgumentException() {
    mockModel.shouldThrowIllegalArgumentException = true;
    assertEquals("Illegal argument thrown by MockModel",
        getErrorMessageWithInput("edit events name eventName from 2025-10-12T10:00 "
            + "with updatedEventName"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
  }

  @Test
  public void validSingleEventUpdateName() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event name eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with updatedEventName"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setSubject("updatedEventName")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateStartDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event startDateTime eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateEndDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event endDateTime eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setEndTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateDescription() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event description eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with \"Anirudh wrote this test suite\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setDescription("Anirudh wrote this test suite")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateLocation() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event location eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with \"123 West Street\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setLocation("123 West Street")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateIsPublicTrue() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event isPublic eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with true"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(true)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventUpdateIsPublicFalse() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event isPublic eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with false"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(false)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventEditUpdateOccurrences() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event occurrences eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with 10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setOccurrences(10)
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventEditUpdateUntilDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event untilDateTime eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with 2025-10-12T23:00"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setUntilDate(LocalDateTime.parse("2025-10-12T23:00"))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validSingleEventEditUpdateWeekDays() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit event weekdays eventName from 2025-10-12T10:00 "
            + "to 2025-11-12T10:10 with MWF"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertEquals(LocalDateTime.parse("2025-11-12T10:10:00"),
        mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setRepeatDays(new HashSet<>(
                    Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F)))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateName() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events name eventName from 2025-10-12T10:00 "
            + "with updatedEventName"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setSubject("updatedEventName")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateStartDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events startDateTime eventName from 2025-10-12T10:00 "
            + "with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateEndDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events endDateTime eventName from 2025-10-12T10:00 "
            + "with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setEndTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateDescription() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events description eventName from 2025-10-12T10:00 "
            + "with \"Anirudh wrote this test suite\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setDescription("Anirudh wrote this test suite")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateLocation() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events location eventName from 2025-10-12T10:00 "
            + "with \"123 West Street\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setLocation("123 West Street")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateIsPublicTrue() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events isPublic eventName from 2025-10-12T10:00 "
            + "with true"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(true)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventUpdateIsPublicFalse() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events isPublic eventName from 2025-10-12T10:00 "
            + "with false"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(false)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventEditUpdateOccurrences() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events occurrences eventName from 2025-10-12T10:00 "
            + "with 10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setOccurrences(10)
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventEditUpdateUntilDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events untilDateTime eventName from 2025-10-12T10:00 "
            + "with 2025-10-12T23:00"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setUntilDate(LocalDateTime.parse("2025-10-12T23:00"))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringFromEventEditUpdateWeekDays() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events weekdays eventName from 2025-10-12T10:00 "
            + "with MWF"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertEquals(LocalDateTime.parse("2025-10-12T10:00:00"),
        mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setRepeatDays(new HashSet<>(
                    Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F)))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateName() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events name eventName "
            + "with updatedEventName"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setSubject("updatedEventName")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateStartDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events startDateTime eventName "
            + "with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setStartTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateEndDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events endDateTime eventName "
            + "with 2025-12-12T10:10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setEndTime(LocalDateTime.parse("2025-12-12T10:10:00"))
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateDescription() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events description eventName "
            + "with \"Anirudh wrote this test suite\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setDescription("Anirudh wrote this test suite")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateLocation() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events location eventName "
            + "with \"123 West Street\""));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setLocation("123 West Street")
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateIsPublicTrue() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events isPublic eventName "
            + "with true"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(true)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventUpdateIsPublicFalse() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events isPublic eventName "
            + "with false"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsPublic(false)
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventEditUpdateOccurrences() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events occurrences eventName "
            + "with 10"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setOccurrences(10)
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventEditUpdateUntilDateTime() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events untilDateTime eventName "
            + "with 2025-10-12T23:00"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setUntilDate(LocalDateTime.parse("2025-10-12T23:00"))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void validRecurringEventEditUpdateWeekDays() {
    assertEquals("Successfully updated event(s)",
        getDisplayMessageWithInput("edit events weekdays eventName "
            + "with MWF"));
    assertTrue(mockModel.editEventCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals("eventName",
        mockModel.editEventReceived.name);
    assertNull(mockModel.editEventReceived.startTime);
    assertNull(mockModel.editEventReceived.endTime);
    assertEquals(EventDTO.getBuilder()
            .setIsRecurring(true)
            .setRecurringDetails(RecurringDetailsDTO.getBuilder()
                .setRepeatDays(new HashSet<>(
                    Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.W, CalendarDayOfWeek.F)))
                .build())
            .build(),
        mockModel.editEventReceived.parametersToUpdate);
  }

  @Test
  public void noEventsUpdated() {
    mockModel.setEditEventReturn=0;
    assertEquals("No events were updated",
        getDisplayMessageWithInput("edit events name eventName with updatedEvent"));
  }
}
