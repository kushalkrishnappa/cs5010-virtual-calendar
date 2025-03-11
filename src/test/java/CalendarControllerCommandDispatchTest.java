import static org.junit.Assert.assertEquals;

import controller.CalendarController;
import controller.ControllerMode;
import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.IModel;
import org.junit.Before;
import org.junit.Test;
import view.IView;

public class CalendarControllerCommandDispatchTest {

  private CalendarController controller;
  private MockModel mockModel;

  // Mock Model
  private static class MockModel implements IModel {

    boolean createEventCalled;
    boolean editEventCalled;
    boolean getEventsOnDateCalled;
    boolean getEventsInRangeCalled;
    boolean exportToCSVCalled;
    boolean isBusyCalled;
    createEvent createEventReceived;
    editEvent editEventReceived;
    getEventsOnDate getEventsOnDateReceived;
    getEventsInRange getEventsInRangeReceived;
    exportToCSV exportToCSVReceived;
    isBusy isBusyReceived;
    boolean shouldThrowEventConflictException;
    boolean shouldThrowCalendarExportException;
    boolean shouldThrowIllegalArgumentException;

    MockModel() {
      createEventCalled = false;
      editEventCalled = false;
      getEventsOnDateCalled = false;
      getEventsInRangeCalled = false;
      exportToCSVCalled = false;
      isBusyCalled = false;

      createEventReceived = null;
      editEventReceived = null;
      getEventsOnDateReceived = null;
      getEventsInRangeReceived = null;
      exportToCSVReceived = null;
      isBusyReceived = null;

      shouldThrowEventConflictException = false;
      shouldThrowCalendarExportException = false;
      shouldThrowIllegalArgumentException = false;
    }

    static class createEvent {

      EventDTO eventDTO;
      Boolean autoDecline;

      createEvent(EventDTO eventDTO, Boolean autoDecline) {
        this.eventDTO = eventDTO;
        this.autoDecline = autoDecline;
      }
    }

    @Override
    public void createEvent(EventDTO eventDTO, boolean autoDecline)
        throws EventConflictException, IllegalArgumentException {
      if (shouldThrowEventConflictException) {
        throw new EventConflictException("Event conflict");
      }
      if (shouldThrowIllegalArgumentException) {
        throw new IllegalArgumentException("Illegal argument");
      }
      createEventCalled = true;
      createEventReceived = new createEvent(eventDTO, autoDecline);
    }

    static class editEvent {

      String name;
      LocalDateTime startTime;
      LocalDateTime endTime;
      EventDTO parametersToUpdate;

      editEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
          EventDTO parametersToUpdate) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.parametersToUpdate = parametersToUpdate;
      }
    }

    @Override
    public Integer editEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
        EventDTO parametersToUpdate) throws EventConflictException, IllegalArgumentException {
      if (shouldThrowEventConflictException) {
        throw new EventConflictException("Event conflict");
      }
      if (shouldThrowIllegalArgumentException) {
        throw new IllegalArgumentException("Illegal argument");
      }
      editEventCalled = true;
      editEventReceived = new editEvent(name, startTime, endTime, parametersToUpdate);
      return 0;
    }

    static class getEventsOnDate {

      LocalDate date;

      getEventsOnDate(LocalDate date) {
        this.date = date;
      }
    }

    @Override
    public List<EventDTO> getEventsOnDate(LocalDate date) {
      getEventsOnDateCalled = true;
      getEventsOnDateReceived = new getEventsOnDate(date);
      return List.of();
    }

    static class getEventsInRange {

      LocalDateTime start;
      LocalDateTime end;

      getEventsInRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
      }
    }

    @Override
    public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
      getEventsInRangeCalled = true;
      getEventsInRangeReceived = new getEventsInRange(start, end);
      return List.of();
    }

    static class exportToCSV {

      String filename;

      exportToCSV(String filename) {
        this.filename = filename;
      }
    }

    @Override
    public String exportToCSV(String fileName) throws CalendarExportException {
      if (shouldThrowCalendarExportException) {
        throw new CalendarExportException("Calendar export failed");
      }
      exportToCSVCalled = true;
      exportToCSVReceived = new exportToCSV(fileName);
      return "";
    }

    static class isBusy {

      LocalDateTime dateTime;

      isBusy(LocalDateTime dateTime) {
        this.dateTime = dateTime;
      }
    }

    @Override
    public Boolean isBusy(LocalDateTime dateTime) {
      isBusyCalled = true;
      isBusyReceived = new isBusy(dateTime);
      return null;
    }
  }

  // Mock View
  private static class MockView implements IView {

    Readable inputStream;
    StringBuilder displayMessage;
    StringBuilder displayErrorMessage;

    MockView(String inputStream) {
      this.inputStream = new StringReader(inputStream);
      displayMessage = new StringBuilder();
      displayErrorMessage = new StringBuilder();
    }


    @Override
    public void displayMessage(String output) {
      displayMessage.append(output);
    }


    @Override
    public void displayError(String error) {
      displayErrorMessage.append(error);
    }

    @Override
    public Readable getInputStream() {
      return inputStream;
    }

    String getDisplayMessage() {
      String s = displayMessage.toString().split("\n")[0];
      return s.startsWith("calApp>") ? s.substring(8) : s;
    }

    String getErrorMessage() {
      String s = displayErrorMessage.toString().split("\n")[0];
      return s.startsWith("calApp>") ? s.substring(8) : s;
    }
  }

  @Before
  public void setUp() {
    mockModel = new MockModel();
  }

  private String getDisplayMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getDisplayMessage();
  }

  private String getErrorMessageWithInput(String input) {
    MockView mockView = new MockView(input + "\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    return mockView.getErrorMessage();
  }

  @Test
  public void emptyInput() {
    assertEquals("calApp> ", getDisplayMessageWithInput(""));
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
  public void spannedEvent() {
    MockView mockView = new MockView(
        "create event --autoDecline \"event name\" from 2025-04-01T12:00 to 2025-04-01T13:00\n");
    controller = new CalendarController(mockModel, mockView, ControllerMode.INTERACTIVE);
    controller.run();
    assertEquals("Successfully created event event name", mockView.getDisplayMessage());
  }
}
