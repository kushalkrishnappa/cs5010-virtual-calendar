import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.IModel;
import service.ICalendarExporter;

/**
 * MockModel class implements IModel and provides a mock model for testing.
 */
class MockModel implements IModel {

  // flags to track the method calls from the controller
  boolean createEventCalled;
  boolean editEventCalled;
  boolean getEventsOnDateCalled;
  boolean getEventsInRangeCalled;
  boolean exportEventsWithExporterCalled;
  boolean isBusyCalled;
  boolean getAllEventsCalled;

  // class to store the parameters received in the method calls
  CreateEvent createEventReceived;
  EditEvent editEventReceived;
  GetEventsOnDate getEventsOnDateReceived;
  GetEventsInRange getEventsInRangeReceived;
  String exportEventsWithExporterReceived;
  IsBusy isBusyReceived;

  // flags to control the exceptions thrown from the model
  boolean shouldThrowEventConflictException;
  boolean shouldThrowCalendarExportException;
  boolean shouldThrowIllegalArgumentException;

  // flags to control the return values from the model
  Integer setEditEventReturn;
  boolean setIsBusyReturn;
  List<EventDTO> setGetEventsInRange;
  List<EventDTO> setGetEventsOnDate;

  // flags to control the event counts on the date and in the range
  Integer eventsOnDateCount;
  Integer eventsInRangeCount;

  // flags to control the event properties
  boolean eventIsAllDay;
  boolean eventIsRecurring;

  // flags required for copy tests
  String eventNameFilter;
  String targetCalendarName;
  LocalDateTime targetStartDateTime;
  LocalDate targetStartDate;
  Integer isBestEffortCopy;

  MockModel() {
    createEventCalled = false;
    editEventCalled = false;
    getEventsOnDateCalled = false;
    getEventsInRangeCalled = false;
    exportEventsWithExporterCalled = false;
    isBusyCalled = false;
    getAllEventsCalled = false;

    createEventReceived = null;
    editEventReceived = null;
    getEventsOnDateReceived = null;
    getEventsInRangeReceived = null;
    exportEventsWithExporterReceived = null;
    isBusyReceived = null;

    shouldThrowEventConflictException = false;
    shouldThrowCalendarExportException = false;
    shouldThrowIllegalArgumentException = false;

    setEditEventReturn = null;
    setIsBusyReturn = false;
    setGetEventsInRange = null;
    setGetEventsOnDate = null;

    eventsOnDateCount = 0;
    eventsInRangeCount = 0;

    eventIsAllDay = false;
    eventIsRecurring = false;

    eventNameFilter = null;
    targetCalendarName = null;
    targetStartDateTime = null;
    targetStartDate = null;
    isBestEffortCopy = 0;
  }

  class CreateEvent {

    EventDTO eventDTO;
    Boolean autoDecline;

    CreateEvent(EventDTO eventDTO, Boolean autoDecline) {
      this.eventDTO = eventDTO;
      this.autoDecline = autoDecline;
    }
  }

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException {
    createEventCalled = true;
    createEventReceived = new CreateEvent(eventDTO, autoDecline);

    // if event is present, set the event details for copy tests
    if (eventDTO != null) {
      eventIsAllDay = eventDTO.getIsAllDay();
      eventIsRecurring = eventDTO.getIsRecurring();
      if (targetCalendarName == null && eventDTO.getSubject() != null) {
        targetCalendarName = "default"; // Default value if not set
      }
      if (targetStartDateTime == null && eventDTO.getStartTime() != null) {
        targetStartDateTime = eventDTO.getStartTime();
      }
      if (targetStartDate == null && eventDTO.getStartTime() != null) {
        targetStartDate = eventDTO.getStartTime().toLocalDate();
      }
    }

    // check for set exceptions and raise accordingly
    if (shouldThrowEventConflictException) {
      if (isBestEffortCopy > 0) {
        isBestEffortCopy--;
      } else {
        throw new EventConflictException("Event conflict");
      }
    }
    if (shouldThrowIllegalArgumentException) {
      throw new IllegalArgumentException("Illegal argument");
    }
  }

  class EditEvent {

    String name;

    LocalDateTime startTime;
    LocalDateTime endTime;
    EventDTO parametersToUpdate;

    EditEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
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
    editEventCalled = true;
    editEventReceived = new EditEvent(name, startTime, endTime, parametersToUpdate);
    if (shouldThrowEventConflictException) {
      throw new EventConflictException("Event conflict thrown by MockModel");
    }
    if (shouldThrowIllegalArgumentException) {
      throw new IllegalArgumentException("Illegal argument thrown by MockModel");
    }
    return Objects.nonNull(setEditEventReturn) ? setEditEventReturn : 1;
  }

  class GetEventsOnDate {

    LocalDate date;

    GetEventsOnDate(LocalDate date) {
      this.date = date;
    }
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    getEventsOnDateCalled = true;
    getEventsOnDateReceived = new GetEventsOnDate(date);

    // if mock events are provided, return them
    if (!Objects.isNull(setGetEventsOnDate)) {
      return setGetEventsOnDate;
    }

    // if events are not provided, return empty list
    return List.of();
  }

  class GetEventsInRange {

    LocalDateTime start;
    LocalDateTime end;

    GetEventsInRange(LocalDateTime start, LocalDateTime end) {
      this.start = start;
      this.end = end;
    }
  }

  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    getEventsInRangeCalled = true;
    getEventsInRangeReceived = new GetEventsInRange(start, end);

    // if mock events are provided, return them
    if (!Objects.isNull(setGetEventsInRange)) {
      if (!Objects.isNull(eventNameFilter)) {
        List<EventDTO> filteredEvents = new ArrayList<>(setGetEventsInRange);
        filteredEvents.removeIf(event -> !event.getSubject().equals(eventNameFilter));
        return filteredEvents;
      }
      return setGetEventsInRange;
    }

    // if events are not provided, return empty list
    return List.of();

  }

  @Override
  public List<EventDTO> getAllEvents() {
    getAllEventsCalled = true;
    return List.of();
  }

  @Override
  public String exportEventsWithExporter(ICalendarExporter exporter) {
    exportEventsWithExporterCalled = true;
    if (Objects.isNull(exportEventsWithExporterReceived)) {
      throw new CalendarExportException("No events to export");
    }
    return exportEventsWithExporterReceived;
  }

  class IsBusy {

    LocalDateTime dateTime;

    IsBusy(LocalDateTime dateTime) {
      this.dateTime = dateTime;
    }
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    isBusyCalled = true;
    isBusyReceived = new IsBusy(dateTime);
    return setIsBusyReturn;
  }
}
