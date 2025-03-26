import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import dto.EventDTO;
import dto.RecurringDetailsDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import model.CalendarDayOfWeek;
import org.junit.Test;

public class EditCalendarCommandTest extends AbstractCommandTest {

  @Test
  public void testInvalidEmptyCommand() {
    assertEquals("Please specify calendar name and property to update",
        getErrorMessageWithInput("edit calendar"));
  }

  @Test
  public void testInvalidCommandMissingNameFlag() {
    assertEquals("Invalid command format: edit calendar --name <calendar name> "
            + "--property <property name> <new property value>",
        getErrorMessageWithInput("edit calendar defaultCalendar --property timezone Asia/Tokyo"));
  }

  @Test
  public void testInvalidCommandMissingPropertyFlag() {
    assertEquals("Invalid command format: edit calendar --name <calendar name> "
            + "--property <property name> <new property value>",
        getErrorMessageWithInput("edit calendar --name default timezone Asia/Tokyo"));
  }

  @Test
  public void testInvalidCommandWithoutName() {
    assertEquals("Invalid command format: create calendar --name <name>",
        getErrorMessageWithInput("edit calendar --name"));
  }

  @Test
  public void testInvalidPropertyName() {
    assertEquals("Invalid property name",
        getErrorMessageWithInput(
            "edit calendar --name defaultCalendar --property invalidProperty value"));
  }

  @Test
  public void testEditCalendarThatDoesNotExist() {
    String command = "edit calendar --name nonExistentCalendar --property timezone Asia/Tokyo";
    assertEquals("Calendar with the provided name doesn't exists",
        getErrorMessageWithInput(command));
  }

  @Test
  public void testEditCalendarTimeZone() {
    String command = "create calendar --name test_calendar --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "edit calendar --name test_calendar --property timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name test_calendar";

    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockView.displayMessage.toString().contains("Created calendar test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to test_calendar"));
  }

  @Test
  public void testEditCalendarName() {
    String command = "create calendar --name test_calendar --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "edit calendar --name test_calendar --property name newCalendarName"
        + System.lineSeparator()
        + "use calendar --name newCalendarName";

    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockView.displayMessage.toString().contains("Created calendar test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to newCalendarName"));
  }

  @Test
  public void testEditBothCalendarProperties() {
    String command = "create calendar --name \"Calendar To Edit\" --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "edit calendar --name \"Calendar To Edit\" "
        + "--property timezone America/New_York "
        + "--property name \"Edited Calendar Name\""
        + System.lineSeparator()
        + "use calendar --name \"Edited Calendar Name\"";

    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockView.displayMessage.toString().contains("Created calendar Calendar To Edit"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to Edited Calendar Name"));
  }

  @Test
  public void testEditTimezoneForCalendarWithRecurringEventUntilShiftsTime() {
    String command = "create calendar --name \"test_calendar\" --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "use calendar --name \"test_calendar\""
        + System.lineSeparator()
        + "edit calendar --name \"test_calendar\" --property timezone America/New_York";

    List<EventDTO> eventsToReturnFromGetAll = new ArrayList<>();
    EventDTO recurringEventUntilDate = EventDTO.getBuilder()
        .setSubject("Event1")
        .setStartTime(LocalDateTime.of(2025, 4, 11, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 11, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(
            RecurringDetailsDTO.getBuilder()
                .setRepeatDays(new HashSet<>(List.of(CalendarDayOfWeek.M)))
                .setOccurrences(null)
                .setUntilDate(LocalDateTime.of(2025, 4, 21, 12, 0))
                .build()
        )
        .build();
    eventsToReturnFromGetAll.add(recurringEventUntilDate);

    mockModel.setGetAllEvents = eventsToReturnFromGetAll;
    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockModel.getAllEventsCalled);
    assertTrue(mockModel.createEventCalled);
    // check for timezone shift on editing the timezone property
    assertEquals("2025-04-11T02:30", mockModel.targetStartDateTime.toString());
    assertTrue(mockModel.eventIsRecurring);
    assertTrue(mockView.displayMessage.toString().contains("Created calendar test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
  }

  @Test
  public void testEditTimezoneForCalendarWithRecurringEventOccurrenceShiftsTime() {
    String command = "create calendar --name \"test_calendar\" --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "use calendar --name \"test_calendar\""
        + System.lineSeparator()
        + "edit calendar --name \"test_calendar\" --property timezone America/New_York";

    List<EventDTO> eventsToReturnFromGetAll = new ArrayList<>();
    EventDTO recurringEventOccurrences = EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(LocalDateTime.of(2025, 4, 11, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 11, 13, 0))
        .setIsAllDay(false)
        .setIsRecurring(true)
        .setRecurringDetails(
            RecurringDetailsDTO.getBuilder()
                .setRepeatDays(new HashSet<>(List.of(CalendarDayOfWeek.M)))
                .setOccurrences(5)
                .setUntilDate(null)
                .build()
        )
        .build();
    eventsToReturnFromGetAll.add(recurringEventOccurrences);

    mockModel.setGetAllEvents = eventsToReturnFromGetAll;
    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockModel.getAllEventsCalled);
    assertTrue(mockModel.createEventCalled);
    // check for timezone shift on editing the timezone property
    assertEquals("2025-04-11T02:30", mockModel.targetStartDateTime.toString());
    assertTrue(mockModel.eventIsRecurring);
    assertTrue(mockView.displayMessage.toString().contains("Created calendar test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Switched to test_calendar"));
    assertTrue(mockView.displayMessage.toString().contains("Calendar updated successfully"));
  }

  @Test
  public void testEditTimezoneForCalendarWithSimpleEventsShiftsTime() {
    String command = "create calendar --name \"test_calendar\" --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "use calendar --name \"test_calendar\""
        + System.lineSeparator()
        + "edit calendar --name \"test_calendar\" --property timezone America/New_York";

    List<EventDTO> eventsToReturnFromGetAll = new ArrayList<>();
    EventDTO simpleEvent = EventDTO.getBuilder()
        .setSubject("Simple Event")
        .setStartTime(LocalDateTime.of(2025, 4, 11, 2, 0))
        .setEndTime(LocalDateTime.of(2025, 4, 11, 3, 0))
        .setIsAllDay(false)
        .setIsRecurring(false)
        .build();
    eventsToReturnFromGetAll.add(simpleEvent);

    mockModel.setGetAllEvents = eventsToReturnFromGetAll;
    MockView mockView = new MockView(command, true);
    controller = new CalendarController(mockModelFactory, mockView, ControllerMode.INTERACTIVE);
    controller.run();

    assertTrue(mockModel.getAllEventsCalled);
    assertTrue(mockModel.createEventCalled);
    assertEquals("2025-04-10T16:30", mockModel.targetStartDateTime.toString());
  }
}
