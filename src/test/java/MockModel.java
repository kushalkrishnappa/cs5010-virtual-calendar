import dto.EventDTO;
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

  boolean createEventCalled;
  boolean editEventCalled;
  boolean getEventsOnDateCalled;
  boolean getEventsInRangeCalled;
  boolean exportEventsWithExporterCalled;
  boolean isBusyCalled;
  boolean getAllEventsCalled;
  CreateEvent createEventReceived;
  EditEvent editEventReceived;
  GetEventsOnDate getEventsOnDateReceived;
  GetEventsInRange getEventsInRangeReceived;
  ExportEventsWithExporter exportEventsWithExporterReceived;
  IsBusy isBusyReceived;
  boolean shouldThrowEventConflictException;
  boolean shouldThrowCalendarExportException;
  boolean shouldThrowIllegalArgumentException;
  Boolean setIsBusyReturn;
  Integer setEditEventReturn;

  List<EventDTO> setGetEventsOnDate;
  List<EventDTO> setGetEventsInRange;

  Integer eventsOnDateCount = 0;
  Integer eventsInRangeCount = 0;
  String eventNameFilter;
  String targerCalendarName;
  LocalDateTime targetStartDateTime;
  LocalDate targetStartDate;

  boolean eventIsAllDay = false;
  boolean eventIsRecurring = false;

  MockModel() {
    createEventCalled = false;
    editEventCalled = false;
    getEventsOnDateCalled = false;
    getEventsInRangeCalled = false;
    isBusyCalled = false;

    createEventReceived = null;
    editEventReceived = null;
    getEventsOnDateReceived = null;
    getEventsInRangeReceived = null;
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

    if (eventDTO != null) {
      eventIsAllDay = eventDTO.getIsAllDay();
      eventIsRecurring = eventDTO.getIsRecurring();

      // Store target calendar name if available (for copy tests)
      if (targerCalendarName == null && eventDTO.getSubject() != null) {
        targerCalendarName = "Work"; // Default value if not set
      }

      // Store target datetime if available (for copy tests)
      if (targetStartDateTime == null && eventDTO.getStartTime() != null) {
        targetStartDateTime = eventDTO.getStartTime();
      }

      // Store target date if available (for copy tests)
      if (targetStartDate == null && eventDTO.getStartTime() != null) {
        targetStartDate = eventDTO.getStartTime().toLocalDate();
      }
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

    // Return mock events if provided
    if (!Objects.isNull(setGetEventsOnDate)) {
      return setGetEventsOnDate;
    }

    List<EventDTO> events = new ArrayList<>();
    for (int i = 0; i < eventsOnDateCount; i++) {
      EventDTO event = EventDTO.getBuilder()
          .setSubject("Event " + (i+1))
          .setStartTime(date.atTime(8 + i, 0))
          .setEndTime(date.atTime(9 + i, 0))
          .setIsAllDay(i % 3 == 0)
          .setIsRecurring(i % 4 == 0)
          .build();
      events.add(event);
    }
    return events;
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

    // return mock events if provided
    if (!Objects.isNull(setGetEventsInRange)) {
      if (!Objects.isNull(eventNameFilter)) {
        List<EventDTO> filteredEvents = new ArrayList<>(setGetEventsInRange);
        filteredEvents.removeIf(event -> !event.getSubject().equals(eventNameFilter));
        return filteredEvents;
      }
      return setGetEventsInRange;
    }

    List<EventDTO> events = new ArrayList<>();
    for (int i = 0; i < eventsInRangeCount; i++) {
      EventDTO.EventDTOBuilder eventBuilder = EventDTO.getBuilder()
          .setSubject("Event " + (i+1))
          .setStartTime(start.plusHours(i))
          .setEndTime(end.plusHours(i + 1))
          .setIsAllDay(eventIsAllDay)
          .setIsRecurring(eventIsRecurring);

      if (eventNameFilter != null && i == 0) {
        eventBuilder.setSubject(eventNameFilter);
      }

      events.add(eventBuilder.build());
    }
    return events;
  }

  @Override
  public List<EventDTO> getAllEvents() {
    getAllEventsCalled = true;
    return List.of();
  }


  class ExportEventsWithExporter {

    ICalendarExporter exporter;

    ExportEventsWithExporter(ICalendarExporter exporter) {
      this.exporter = exporter;
    }
  }

  @Override
  public String exportEventsWithExporter(ICalendarExporter exporter) {
    exportEventsWithExporterCalled = true;
    return "Result from exporter";
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
