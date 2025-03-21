import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import model.IModel;

/**
 * MockModel class implements IModel and provides a mock model for testing.
 */
class MockModel implements IModel {

  boolean createEventCalled;
  boolean editEventCalled;
  boolean getEventsOnDateCalled;
  boolean getEventsInRangeCalled;
  boolean exportToCSVCalled;
  boolean isBusyCalled;
  boolean getAllEventsCalled;
  CreateEvent createEventReceived;
  EditEvent editEventReceived;
  GetEventsOnDate getEventsOnDateReceived;
  GetEventsInRange getEventsInRangeReceived;
  ExportToCSV exportToCSVReceived;
  IsBusy isBusyReceived;
  boolean shouldThrowEventConflictException;
  boolean shouldThrowCalendarExportException;
  boolean shouldThrowIllegalArgumentException;
  Boolean setIsBusyReturn;
  Integer setEditEventReturn;
  List<EventDTO> setGetEventsOnDate;
  List<EventDTO> setGetEventsInRange;

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
    if (shouldThrowEventConflictException) {
      throw new EventConflictException("Event conflict");
    }
    if (shouldThrowIllegalArgumentException) {
      throw new IllegalArgumentException("Illegal argument");
    }
    createEventCalled = true;
    createEventReceived = new CreateEvent(eventDTO, autoDecline);
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
    return setGetEventsOnDate;
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
    return setGetEventsInRange;
  }

  @Override
  public List<EventDTO> getAllEvents() {
    getAllEventsCalled = true;
    return List.of();
  }

  class ExportToCSV {

    String filename;

    ExportToCSV(String filename) {
      this.filename = filename;
    }
  }

  @Override
  public String exportToCSV(String fileName) throws CalendarExportException {
    if (shouldThrowCalendarExportException) {
      throw new CalendarExportException("Calendar export failed");
    }
    exportToCSVCalled = true;
    exportToCSVReceived = new ExportToCSV(fileName);
    return "Return from exportToCSV";
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
