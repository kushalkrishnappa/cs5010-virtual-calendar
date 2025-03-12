import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.IModel;

class MockModel implements IModel {

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
  Boolean setIsBusyReturn;

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

  class createEvent {

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

  class editEvent {

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

  class getEventsOnDate {

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

  class getEventsInRange {

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

  class exportToCSV {

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
    return "Return from exportToCSV";
  }

  class isBusy {

    LocalDateTime dateTime;

    isBusy(LocalDateTime dateTime) {
      this.dateTime = dateTime;
    }
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    isBusyCalled = true;
    isBusyReceived = new isBusy(dateTime);
    return setIsBusyReturn;
  }
}
