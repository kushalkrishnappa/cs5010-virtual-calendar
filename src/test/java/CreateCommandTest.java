import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import model.CalendarDayOfWeek;
import org.junit.Test;

public class CreateCommandTest extends AbstractCommandTest {

  @Test
  public void emptyInput() {
    assertEquals("calApp> ", getDisplayMessageWithInput(""));
  }

  @Test
  public void afterExit() {
    assertEquals("Bye...",
        getDisplayMessageWithInput("exit\n"));
  }

  @Test
  public void emptyCommand() {
    assertEquals("calApp> ",
        getDisplayMessageWithInput(""));
  }

  @Test
  public void invalidCommand() {
    assertEquals("Unknown command", getErrorMessageWithInput("invalidCommand"));
  }

  @Test
  public void invalidCreateMissingTokens() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> "
            + "(on <dateTime>| from <startDateTime> to <endDateTime>) "
            + "[repeats <weekdays> (for <N> times| until <untilDateTime>)]",
        getErrorMessageWithInput("create "));
  }

  @Test
  public void invalidCreateEvend() {
    assertEquals("Invalid command format: create event ...",
        getErrorMessageWithInput("create evend"));
  }

  @Test
  public void invalidCreateEvent() {
    assertEquals("Invalid command format: create event <eventName> ...",
        getErrorMessageWithInput("create event "));
  }

  @Test
  public void invalidCreateEventAutoDecline() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> ...",
        getErrorMessageWithInput("create event --autoDecline "));
  }

  @Test
  public void invalidCreateEventNameNotFromOrOn() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> (from|on) ...",
        getErrorMessageWithInput("create event eventName since"));
  }

  @Test
  public void invalidCreateEventNameFrom() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> "
            + "(on <dateTime>| from <startDateTime> to <endDateTime>) "
            + "[repeats <weekdays> (for <N> times| until <untilDateTime>)]",
        getErrorMessageWithInput("create event eventName from "));
  }

  @Test
  public void invalidCreateEventNameFromStartTime() {
    assertEquals("Invalid startDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("create event eventName from 2025/10/21-12:00 "));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeTo() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> from "
            + "<startDateTime> to ...",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 uptill "));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeToEndTime() {
    assertEquals("Invalid endDateTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-1021T12:54"));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeToEndTimeRepeat() {
    assertEquals("Invalid command format: create event <eventName> "
            + "(on <dateTime> |from <startDateTime> to <endDateTime>) repeats ...",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 reoccurs"));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDays() {
    assertEquals("Invalid week days specification: Expected combination of "
            + "MTWRFSU",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats ABCD "));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDaysUptill() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> from "
            + "<startDateTime> to <dateDateTime> repeats <weekdays> (for|until) ... ",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats TRU uptill"));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDaysForOccur() {
    assertEquals("Invalid occurrences format: Expected integer",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats TRU for may "));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDaysForNegativeOccur() {
    assertEquals("Occurrences must be positive",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats TRU for 0 "));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDaysForOccurNumber() {
    assertEquals("Invalid command format: create event [--autoDecline] <eventName> from "
            + "<startDateTime> to <dateDateTime> repeats <weekdays> for <N> times",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats TRU for 10 number"));
  }

  @Test
  public void invalidCreateEventNameFromStartTimeRepeatWeekDaysUntil() {
    assertEquals("Invalid untilTime format: yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("create event eventName from 2025-10-21T12:00 to "
            + "2025-10-21T12:54 repeats TRU until 2025-1021T12:54"));
  }

  @Test
  public void invalidCreateEventNameOnDateTime() {
    assertEquals("Invalid Start Date format: expecting yyyy-MM-dd'T'HH:mm",
        getErrorMessageWithInput("create event eventName on 2025/10/21"));
  }

  @Test
  public void invalidCreateEventNameOnDate() {
    assertEquals("Invalid date format: yyyy-MM-dd",
        getErrorMessageWithInput("create event eventName on 2025/10/21 repeats TRU for 10 times"));
  }

  @Test
  public void invalidCreateEventNameOnDateRepeatWeekDaysForOccurNumber() {
    assertEquals("Invalid command format: create event <eventName> on <dateString> "
            + "repeats <weekdays> for <occurrences> times",
        getErrorMessageWithInput("create event eventName on 2025-10-21 "
            + "repeats TRU for 10 number"));
  }

  @Test
  public void invalidCreateEventNameOnDateRepeatWeekDaysUptill() {
    assertEquals("Invalid command format: create event <eventName> on <dateTime> "
            + "repeats <weekdays> (for|until) ...",
        getErrorMessageWithInput("create event eventName on 2025-10-21 "
            + "repeats TRU uptill 10 number"));
  }

  @Test
  public void invalidCreateEventNameOnDateRepeatWeekDaysUntilDate() {
    assertEquals("Invalid untilTime format: yyyy-MM-dd",
        getErrorMessageWithInput("create event eventName on 2025-10-21 "
            + "repeats TRU until 2025-1021T12:54"));
  }

  @Test
  public void spannedEvent() {
    MockView mockView = new MockView(
        "create event --autoDecline \"event name\" "
            + "from 2025-04-01T12:00 to 2025-04-01T13:00\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertTrue(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T12:00"))
            .setEndTime(LocalDateTime.parse("2025-04-01T13:00"))
            .setIsRecurring(false)
            .setIsAllDay(false)
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }

  @Test
  public void spannedRecurringForOccurEvent() {
    MockView mockView = new MockView(
        "create event --autoDecline \"event name\" "
            + "from 2025-04-01T12:00 to 2025-04-01T13:00 repeats MTU for 10 times\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertTrue(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T12:00"))
            .setEndTime(LocalDateTime.parse("2025-04-01T13:00"))
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setOccurrences(10)
                    .setRepeatDays(new HashSet<>(
                        Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.T,
                            CalendarDayOfWeek.U)))
                    .build()
            )
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }

  @Test
  public void spannedRecurringUntilDateEvent() {
    MockView mockView = new MockView(
        "create event --autoDecline \"event name\" "
            + "from 2025-04-01T12:00 to 2025-04-01T13:00 repeats MTU until 2025-05-10T12:00\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertTrue(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T12:00"))
            .setEndTime(LocalDateTime.parse("2025-04-01T13:00"))
            .setIsRecurring(true)
            .setIsAllDay(false)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setUntilDate(LocalDateTime.parse("2025-05-10T12:00"))
                    .setRepeatDays(new HashSet<>(
                        Arrays.asList(CalendarDayOfWeek.M, CalendarDayOfWeek.T,
                            CalendarDayOfWeek.U)))
                    .build()
            )
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }

  @Test
  public void allDayEvent() {
    MockView mockView = new MockView(
        "create event --autoDecline \"event name\" "
            + "on 2025-04-01T13:00\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertTrue(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T00:00"))
            .setEndTime(null)
            .setIsRecurring(false)
            .setIsAllDay(true)
            .setRecurringDetails(null)
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }

  @Test
  public void allDayRecurringForOccurEvent() {
    MockView mockView = new MockView(
        "create event \"event name\" "
            + "on 2025-04-01 repeats WRFS for 10 times\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertFalse(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T00:00"))
            .setEndTime(null)
            .setIsRecurring(true)
            .setIsAllDay(true)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setOccurrences(10)
                    .setRepeatDays(new HashSet<>(
                        Arrays.asList(CalendarDayOfWeek.W, CalendarDayOfWeek.R,
                            CalendarDayOfWeek.F, CalendarDayOfWeek.S)))
                    .build()
            )
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }

  @Test
  public void allDayRecurringUntilDateEvent() {
    MockView mockView = new MockView(
        "create event \"event name\" "
            + "on 2025-04-01 repeats WRFS until 2025-05-19\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertTrue(mockModel.createEventCalled);
    assertFalse(mockModel.exportToCSVCalled);
    assertFalse(mockModel.editEventCalled);
    assertFalse(mockModel.getEventsInRangeCalled);
    assertFalse(mockModel.getEventsOnDateCalled);
    assertFalse(mockModel.isBusyCalled);
    assertFalse(mockModel.createEventReceived.autoDecline);
    assertEquals(
        EventDTO.getBuilder()
            .setSubject("event name")
            .setStartTime(LocalDateTime.parse("2025-04-01T00:00"))
            .setEndTime(null)
            .setIsRecurring(true)
            .setIsAllDay(true)
            .setRecurringDetails(
                RecurringDetailsDTO.getBuilder()
                    .setUntilDate(LocalDateTime.parse("2025-05-19T00:00"))
                    .setRepeatDays(new HashSet<>(
                        Arrays.asList(CalendarDayOfWeek.W, CalendarDayOfWeek.R,
                            CalendarDayOfWeek.F, CalendarDayOfWeek.S)))
                    .build()
            )
            .build(),
        mockModel.createEventReceived.eventDTO);
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }
}
