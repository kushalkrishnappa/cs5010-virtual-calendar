import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import model.CalendarDayOfWeek;
import org.junit.Test;

/**
 * Test class for PrintEventsCommand.
 */
public class PrintEventsCommandTest extends AbstractCommandTest {

  @Test
  public void invalidEmptyCommand() {
    assertEquals("Invalid command format: print events "
            + "(on <dateTime>|from <startDateTime> to <endDateTime>)",
        getErrorMessageWithInput("print"));
  }

  @Test
  public void invalidEvent() {
    assertEquals("Invalid command format: print events ...",
        getErrorMessageWithInput("print event "));
  }

  @Test
  public void invalidEventDuring() {
    assertEquals("Invalid command format: print events (on|from) ...",
        getErrorMessageWithInput("print events during on|from"));
  }

  @Test
  public void invalidOnDate() {
    assertEquals("Invalid onDate format: yyyy-MM-dd",
        getErrorMessageWithInput("print events on 2025/10/21"));
  }

  @Test
  public void invalidStartDateTime() {
    assertEquals("Invalid startDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("print events from 2025/10/21T00:00:00"));
  }

  @Test
  public void invalidStartDateTimeDuring() {
    assertEquals("Invalid command format: print events from <dateStringTtimeString> to ...",
        getErrorMessageWithInput("print events from 2025-10-21T00:00 during"));
  }

  @Test
  public void invalidEndDateTime() {
    assertEquals("Invalid endDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("print events from 2025-10-21T00:00 to 2025-1021T00:00:00"));
  }

  @Test
  public void getEventsOnDate() {
    MockView mockView = new MockView("print events on 2025-10-21");
    mockModel.setGetEventsOnDate = generateEvents();
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals(LocalDate.of(2025, 10, 21),
        mockModel.getEventsOnDateReceived.date);
    assertEquals(generateEventsRepresentation(),
        mockView.displayMessage.toString());
  }

  @Test
  public void getEventsInRange() {
    MockView mockView = new MockView("print events from 2025-10-21T09:00 to "
        + "2025-10-22T09:30");
    mockModel.setGetEventsInRange = generateEvents();
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.createEventCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.isBusyCalled);
    assertEquals(LocalDateTime.of(2025, 10, 21, 9, 0),
        mockModel.getEventsInRangeReceived.start);
    assertEquals(LocalDateTime.of(2025, 10, 22, 9, 30),
        mockModel.getEventsInRangeReceived.end);
    assertEquals(generateEventsRepresentation(),
        mockView.displayMessage.toString());
  }

  private List<EventDTO> generateEvents() {
    return Arrays.asList(
        EventDTO.getBuilder()
            .setSubject("SpannedEvent")
            .setStartTime(LocalDateTime.of(2025, 10, 21, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 10, 22, 11, 0))
            .setIsAllDay(false)
            .setIsRecurring(false)
            .setRecurringDetails(null)
            .setDescription("SpannedEvent description")
            .setLocation("SpannedEvent location")
            .setIsPublic(true)
            .build(),
        EventDTO.getBuilder()
            .setSubject("SpannedRecurringEvent")
            .setStartTime(LocalDateTime.of(2025, 10, 21, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 10, 21, 11, 0))
            .setIsAllDay(false)
            .setIsRecurring(true)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setRepeatDays(
                        new HashSet<>(Arrays.asList(CalendarDayOfWeek.W, CalendarDayOfWeek.M)))
                    .setOccurrences(10)
                    .build()
            )
            .setDescription("SpannedRecurringEvent description")
            .setLocation("SpannedRecurringEvent location")
            .setIsPublic(false)
            .build(),
        EventDTO.getBuilder()
            .setSubject("AllDayEvent")
            .setStartTime(LocalDateTime.of(2025, 10, 21, 10, 0))
            .setEndTime(null)
            .setIsAllDay(true)
            .setIsRecurring(false)
            .setRecurringDetails(null)
            .setDescription("AllDayEvent description")
            .setLocation("AllDayEvent location")
            .setIsPublic(false)
            .build(),
        EventDTO.getBuilder()
            .setSubject("AllDayRecurringEvent")
            .setStartTime(LocalDateTime.of(2025, 10, 21, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 10, 21, 11, 0))
            .setIsAllDay(true)
            .setIsRecurring(true)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setRepeatDays(
                        new HashSet<>(Arrays.asList(CalendarDayOfWeek.W, CalendarDayOfWeek.M)))
                    .setOccurrences(10)
                    .build()
            )
            .setDescription("AllDayRecurringEvent description")
            .setLocation("AllDayRecurringEvent location")
            .setIsPublic(false)
            .build()
    );
  }

  private String generateEventsRepresentation() {
    return "calApp> "
        + "[2025-10-21] [2025-10-21 10:00 - 2025-10-22 11:00] [Not Recurring] "
        + "SpannedEvent || SpannedEvent location\n"
        + "[2025-10-21] [2025-10-21 10:00 - 2025-10-21 11:00] [Recurring]     "
        + "SpannedRecurringEvent || SpannedRecurringEvent location\n"
        + "[2025-10-21] [ALL DAY EVENT]                       [Not Recurring] "
        + "AllDayEvent || AllDayEvent location\n"
        + "[2025-10-21] [ALL DAY EVENT]                       [Recurring]     "
        + "AllDayRecurringEvent || AllDayRecurringEvent location\n"
        + "calApp> ";
  }
}
