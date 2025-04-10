import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import controller.CalendarFeatures;
import controller.CalendarWeekDays;
import controller.EventData;
import controller.GUIController;
import controller.IController;
import controller.RecurrenceData;
import dto.EventDTO;
import dto.ImportResult;
import dto.RecurringDetailsDTO;
import exception.CalendarExportException;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import model.CalendarDayOfWeek;
import model.IModel;
import org.junit.Before;
import org.junit.Test;
import service.ICalendarImporter;

/**
 * Test cases for {@link GUIController} class.
 */
public class GUIControllerTest {

  MockGUIView view;
  MockModel model;
  Supplier<IModel> modelSupplier;
  CalendarFeatures controller;
  MockImporter importer;

  private static final String EDIT_THIS_EVENT = "This event only";
  private static final String EDIT_THIS_AND_FOLLOWING =
      "This and following events with same name";
  private static final String EDIT_ALL_EVENTS = "All events with same name";


  @Before
  public void setUp() {
    view = new MockGUIView();
    model = new MockModel();
    modelSupplier = () -> model;
    GUIController guiController = new GUIController(modelSupplier, view);
    importer = new MockImporter();
    guiController.setImporter(importer);
    controller = guiController;

    ((IController) controller).run();
    controller = view.lastSetFeatures;
    view.reset();
  }

  @Test
  public void testRun() {
    GUIController controller = new GUIController(modelSupplier, view);
    controller.run();
    assertTrue(view.setAvailableCalendarsCalled);
    assertTrue(view.setCurrentCalendarTzCalled);
    assertTrue(view.setMonthYearLabelCalled);
    assertTrue(view.setCalendarMonthDatesCalled);
    assertTrue(view.setFeaturesCalled);
    assertFalse(view.showDayViewDialogCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.showNewCalendarDialogCalled);
    assertFalse(view.showEditCalendarDialogCalled);
    assertFalse(view.showImportCalendarDialogCalled);
    assertFalse(view.showExportCalendarDialogCalled);
    assertFalse(view.displayRecurringEventOptionsCalled);
    assertFalse(view.showCreateEventDialogCalled);
    assertFalse(view.showEditEventDialogCalled);
    assertFalse(view.showEventDetailsDialogCalled);
    assertFalse(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  // Create Calendar


  @Test
  public void testCreateCalendarSuccess() {
    String newName = "Work Calendar";
    String newTz = "America/New_York";
    controller.createCalendar(newName, newTz);

    assertTrue(view.setAvailableCalendarsCalled);
    assertTrue(view.setCurrentCalendarCalled);
    assertEquals(newName, view.lastSetCurrentCalendar);
    assertTrue(view.setCurrentCalendarTzCalled);
    assertEquals(newTz, view.lastSetCurrentCalendarTz);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarNullName() {
    controller.createCalendar(null, "America/New_York");
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarEmptyName() {
    controller.createCalendar("", "America/New_York");
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarNullTimezone() {
    controller.createCalendar("Test", null);
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarEmptyTimezone() {
    controller.createCalendar("Test", null);
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarInvalidTimezone() {
    controller.createCalendar("Test", "Invalid/Timezone");
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  @Test
  public void testCreateCalendarWithSameNameAsExisting() {
    controller.createCalendar("Default", "America/New_York");
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
  }

  // Edit Calendar
  @Test
  public void testEditCalendarSuccessNameAndTimezoneChange() {
    String currentName = "Default";
    String newName = "Personal";
    String newTz = "Europe/London";
    controller.editCalendar(currentName, newName, newTz);

    assertTrue(view.setAvailableCalendarsCalled);
    assertTrue(view.setCurrentCalendarCalled);
    assertEquals(newName, view.lastSetCurrentCalendar);
    assertTrue(view.setCurrentCalendarTzCalled);
    assertEquals(newTz, view.lastSetCurrentCalendarTz);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditCalendarSuccessOnlyTimezoneChange() {
    String currentName = "Default";
    String newName = "Default";
    String newTz = "Europe/London";
    controller.editCalendar(currentName, newName, newTz);

    assertTrue(view.setAvailableCalendarsCalled);
    assertTrue(view.setCurrentCalendarCalled);
    assertEquals(newName, view.lastSetCurrentCalendar);
    assertTrue(view.setCurrentCalendarTzCalled);
    assertEquals(newTz, view.lastSetCurrentCalendarTz);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditCalendarNewNameExists() {
    controller.createCalendar("ExistingCal", "UTC");
    view.reset();

    controller.editCalendar("Default", "ExistingCal", "UTC");

    assertTrue(view.displayErrorCalled);
    assertTrue(view.lastDisplayErrorMessage.contains("already exists"));
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
  }

  @Test
  public void testEditCalendarNullName() {
    controller.editCalendar(null, "Test", "UTC");
    assertTrue(view.displayErrorCalled);
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
  }

  @Test
  public void testEditCalendarInvalidTimezone() {
    controller.editCalendar("Default", "Test", "Invalid/Timezone");
    assertTrue(view.displayErrorCalled);
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
  }

  @Test
  public void testEditCalendarNullNewName() {
    controller.editCalendar("Default", null, "UTC");
    assertTrue(view.displayErrorCalled);
    assertFalse(view.setAvailableCalendarsCalled);
    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
  }

  // Switch Calendar

  @Test
  public void testSwitchCalendar() {
    controller.createCalendar("SwitchedTo", "UTC");
    view.reset();

    controller.switchCalendar("SwitchedTo");

    assertTrue(view.setCurrentCalendarCalled);
    assertEquals("SwitchedTo", view.lastSetCurrentCalendar);
    assertTrue(view.setCurrentCalendarTzCalled);
    assertEquals("UTC", view.lastSetCurrentCalendarTz);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testSwitchCalendarInvalidName() {
    controller.createCalendar("SwitchedTo", "UTC");
    view.reset();

    controller.switchCalendar("InvalidName");

    assertFalse(view.setCurrentCalendarCalled);
    assertFalse(view.setCurrentCalendarTzCalled);
    assertTrue(view.displayErrorCalled);
  }

  // Request dialogs
  @Test
  public void testRequestCalendarCreation() {
    controller.requestCalendarCreation();
    assertTrue(view.showNewCalendarDialogCalled);
  }

  @Test
  public void testRequestCalendarEdit() {
    controller.requestCalendarEdit();
    assertTrue(view.showEditCalendarDialogCalled);
  }

  @Test
  public void testRequestCalendarExport() {
    controller.requestCalendarExport();
    assertTrue(view.showExportCalendarDialogCalled);
  }

  @Test
  public void testRequestCalendarImport() {
    controller.requestCalendarImport();
    assertTrue(view.showImportCalendarDialogCalled);
  }

  @Test
  public void testRequestEventCreation() {
    LocalDate date = LocalDate.now();
    controller.requestEventCreation(date);
    assertTrue(view.showCreateEventDialogCalled);
    assertEquals(date, view.lastCreateEventDialogDate);
  }

  @Test
  public void testRequestEventEdit() {
    EventData eventData = EventData.getBuilder().setSubject("Test Event").build();
    controller.requestEventEdit(eventData);
    assertTrue(view.showEditEventDialogCalled);
    assertEquals(eventData, view.lastEditEventDialogData);
  }

  @Test
  public void testRequestEventViewDetails() {
    EventData eventData = EventData.getBuilder().setSubject("Details Event").build();
    controller.requestEventViewDetails(eventData);
    assertTrue(view.showEventDetailsDialogCalled);
    assertEquals(eventData, view.lastEventDetailsDialogData);
  }

  // View

  @Test
  public void testViewDayWithEvents() {
    LocalDate date = LocalDate.of(2025, 4, 10);
    // Setup mock model to return events
    List<EventDTO> dtos = new ArrayList<>();
    dtos.add(EventDTO.getBuilder()
        .setSubject("Event 1")
        .setStartTime(date.atTime(10, 0))
        .setEndTime(date.atTime(11, 0))
        .setLocation("test location")
        .setDescription("test description")
        .setIsPublic(false)
        .setIsAllDay(false)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .build()
    );
    model.setGetEventsOnDate = dtos;

    controller.viewDay(date);

    assertTrue(model.getEventsOnDateCalled);
    assertEquals(date, model.getEventsOnDateReceived.date);
    assertTrue(view.showDayViewDialogCalled);
    assertEquals(date, view.lastShowDayViewDialogDate);
    assertEquals(1, view.lastShowDayViewDialogEvents.size());
    assertEquals("Event 1", view.lastShowDayViewDialogEvents.get(0).getSubject());
    assertEquals(date.atTime(10, 0), view.lastShowDayViewDialogEvents.get(0).getStartTime());
    assertEquals(date.atTime(11, 0), view.lastShowDayViewDialogEvents.get(0).getEndTime());
    assertEquals("test location", view.lastShowDayViewDialogEvents.get(0).getLocation());
    assertEquals("test description", view.lastShowDayViewDialogEvents.get(0).getDescription());
    assertFalse(view.lastShowDayViewDialogEvents.get(0).getPublic());
    assertFalse(view.lastShowDayViewDialogEvents.get(0).getAllDay());
    assertFalse(view.lastShowDayViewDialogEvents.get(0).getRecurring());
    assertNull(view.lastShowDayViewDialogEvents.get(0).getRecurringDetails());

  }

  @Test
  public void testViewDayNoEvents() {
    LocalDate date = LocalDate.of(2025, 4, 11);
    model.setGetEventsOnDate = Collections.emptyList(); // Configure mock model

    controller.viewDay(date);

    assertTrue(model.getEventsOnDateCalled);
    assertEquals(date, model.getEventsOnDateReceived.date);
    assertTrue(view.showDayViewDialogCalled);
    assertEquals(date, view.lastShowDayViewDialogDate);
    assertTrue(view.lastShowDayViewDialogEvents.isEmpty());
  }


  @Test
  public void testNextMonthYear() {
    YearMonth current = YearMonth.of(2025, 4);
    YearMonth next = YearMonth.of(2025, 5);
    controller.nextMonthYear(current);

    assertTrue(view.setMonthYearLabelCalled);
    assertEquals(next, view.lastSetMonthYearLabelMonthYear);
    assertTrue(view.setCalendarMonthDatesCalled);
    assertEquals(next, view.lastSetCalendarMonthDatesMonthYear);
  }

  @Test
  public void testPreviousMonthYear() {
    YearMonth current = YearMonth.of(2025, 4);
    YearMonth previous = YearMonth.of(2025, 3);
    controller.previousMonthYear(current);

    assertTrue(view.setMonthYearLabelCalled);
    assertEquals(previous, view.lastSetMonthYearLabelMonthYear);
    assertTrue(view.setCalendarMonthDatesCalled);
    assertEquals(previous, view.lastSetCalendarMonthDatesMonthYear);
  }

  // create event

  private EventData createBasicEventData(LocalDate date) {
    return EventData.getBuilder()
        .setSubject("New Event")
        .setStartTime(date.atTime(10, 0))
        .setEndTime(date.atTime(11, 0))
        .setDescription("Desc")
        .setLocation("Loc")
        .setIsPublic(true)
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
  }

  private EventData createRecurringEventData(
      LocalDate date, Set<CalendarWeekDays> days, LocalDateTime until) {
    return EventData.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(date.atTime(14, 0))
        .setEndTime(date.atTime(15, 0))
        .setDescription("Recurring Desc")
        .setLocation("Recurring Loc")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(new RecurrenceData(null, days, until))
        .build();
  }

  @Test
  public void testCreateEventSimpleSuccess() {
    LocalDate date = LocalDate.of(2025, 5, 1);
    EventData data = createBasicEventData(date);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.createEvent(data);

    assertTrue(model.createEventCalled);
    assertTrue(model.getEventsOnDateCalled);
    assertEquals(date, model.getEventsOnDateReceived.date);
    assertTrue(view.showDayViewDialogCalled);
    assertEquals(date, view.lastShowDayViewDialogDate);
    assertFalse(view.displayErrorCalled);
    assertTrue(view.displayMessageCalled);
  }

  @Test
  public void testCreateEventRecurringSuccess() {
    LocalDate date = LocalDate.of(2025, 5, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.M, CalendarWeekDays.F);
    LocalDate until = LocalDate.of(2025, 6, 1);
    EventData data = createRecurringEventData(date, days, until.atStartOfDay());
    model.setGetEventsOnDate = List.of(EventDTO.getBuilder()
        .setSubject("Recurring Event")
        .setStartTime(date.atTime(10, 0))
        .setEndTime(date.atTime(11, 0))
        .setDescription("Recurring Desc")
        .setLocation("Recurring Loc")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(RecurringDetailsDTO.getBuilder()
            .setRepeatDays(Set.of(CalendarDayOfWeek.M))
            .setOccurrences(10)
            .build())
        .build());

    controller.createEvent(data);

    assertTrue(model.createEventCalled);
    assertEquals(Set.of(CalendarDayOfWeek.M, CalendarDayOfWeek.F),
        model.createEventReceived.eventDTO.getRecurringDetails().getRepeatDays());
    assertEquals(until.atStartOfDay(),
        model.createEventReceived.eventDTO.getRecurringDetails().getUntilDate());
    assertTrue(model.getEventsOnDateCalled);
    assertEquals(date, model.getEventsOnDateReceived.date);
    assertTrue(view.showDayViewDialogCalled);
    assertEquals(date, view.lastShowDayViewDialogDate);
    assertEquals("Recurring Event", view.lastShowDayViewDialogEvents.get(0).getSubject());
    assertEquals(date.atTime(10, 0), view.lastShowDayViewDialogEvents.get(0).getStartTime());
    assertEquals(date.atTime(11, 0), view.lastShowDayViewDialogEvents.get(0).getEndTime());
    assertEquals("Recurring Desc", view.lastShowDayViewDialogEvents.get(0).getDescription());
    assertEquals("Recurring Loc", view.lastShowDayViewDialogEvents.get(0).getLocation());
    assertTrue(view.lastShowDayViewDialogEvents.get(0).getPublic());
    assertFalse(view.lastShowDayViewDialogEvents.get(0).getAllDay());
    assertTrue(view.lastShowDayViewDialogEvents.get(0).getRecurring());
    assertEquals(Set.of(CalendarWeekDays.M),
        view.lastShowDayViewDialogEvents.get(0).getRecurringDetails().getRepeatDays());
    assertEquals(Integer.valueOf(10),
        view.lastShowDayViewDialogEvents.get(0).getRecurringDetails().getOccurrences());
    assertEquals(null,
        view.lastShowDayViewDialogEvents.get(0).getRecurringDetails().getUntilDate());

    assertFalse(view.displayErrorCalled);
    assertTrue(view.displayMessageCalled);
  }

  @Test
  public void testCreateEventRecurringNoRepeatDaysSelectedError() {
    LocalDate date = LocalDate.of(2025, 5, 1);
    EventData data =
        EventData.getBuilder()
            .setSubject("Recurring Event")
            .setStartTime(date.atTime(14, 0))
            .setEndTime(date.atTime(15, 0))
            .setIsRecurring(true)
            .setRecurringDetails(
                new RecurrenceData(
                    null, Collections.emptySet(), null)) // Empty repeat days
            .build();

    controller.createEvent(data);

    assertTrue(view.displayErrorCalled);
    assertTrue(view.lastDisplayErrorMessage.contains("Select a repeat week day"));
    assertFalse(model.createEventCalled);
    assertFalse(view.showDayViewDialogCalled);
    assertFalse(view.displayMessageCalled);
  }

  @Test
  public void testCreateEvent_Exception() {
    LocalDate date = LocalDate.of(2025, 5, 1);
    EventData data = createBasicEventData(date);
    model.shouldThrowEventConflictException = true;

    controller.createEvent(data);

    assertTrue(model.createEventCalled);
    assertTrue(view.displayErrorCalled);
    assertFalse(view.showDayViewDialogCalled);
    assertFalse(view.displayMessageCalled);
  }

  // export calendar

  @Test
  public void testExportCalendarSuccess() throws CalendarExportException {
    model.exportEventsWithExporterReceived = "Exporter called";
    controller.exportCalendar("export.csv");
    assertTrue(model.exportEventsWithExporterCalled);
    assertTrue(view.displayMessageCalled);
    assertTrue(view.lastDisplayMessage.endsWith(".csv\n"));
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testExportCalendarSuccessExtension() throws CalendarExportException {
    model.exportEventsWithExporterReceived = "Exporter called";
    controller.exportCalendar("export");
    assertTrue(model.exportEventsWithExporterCalled);
    assertTrue(view.displayMessageCalled);
    assertTrue(view.lastDisplayMessage.endsWith(".csv\n"));
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testExportCalendarFailure() throws CalendarExportException {
    controller.exportCalendar("export.csv");
    assertTrue(model.exportEventsWithExporterCalled);
    assertTrue(view.displayErrorCalled);
    assertFalse(view.displayMessageCalled);
  }

  // import calendar

  private class MockImporter implements ICalendarImporter {

    String readerContent;
    String setReturnMessage;
    int setSuccessCount;
    int setTotalCount;
    EventDTO setEventDTO;

    MockImporter() {
      setSuccessCount = 1;
      setTotalCount = 1;
    }

    @Override
    public ImportResult importEvents(Reader reader, Consumer<EventDTO> eventConsumer)
        throws IOException {
      readerContent = (new Scanner(reader)).useDelimiter("\\A").next();
      eventConsumer.accept(setEventDTO);
      return new ImportResult(setSuccessCount, setTotalCount, setReturnMessage);
    }
  }

  @Test
  public void testImportCalendarFromFileSuccess() {
    importer.setEventDTO = EventDTO.getBuilder()
        .setSubject("Event 1")
        .setStartTime(LocalDateTime.of(2025, 10, 21, 12, 0))
        .setEndTime(LocalDateTime.of(2025, 10, 21, 13, 0))
        .setLocation("test")
        .setDescription("Event Description")
        .setIsAllDay(false)
        .setIsPublic(true)
        .setIsRecurring(false)
        .setRecurringDetails(null)
        .build();
    controller.importCalendarFromFile("src/test/resources/importTest.csv");
    assertTrue(model.createEventCalled);
    assertEquals(importer.setEventDTO, model.createEventReceived.eventDTO);
    assertTrue(model.createEventReceived.autoDecline);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
    assertEquals("Successfully imported 1 out of 1 records.", view.lastDisplayMessage);
  }

  @Test
  public void testImportCalendarFromNonExistingFile() {
    controller.importCalendarFromFile("src/test/resources/importTest2.csv");
    assertFalse(view.displayMessageCalled);
    assertTrue(view.displayErrorCalled);
    assertTrue(view.lastDisplayErrorMessage.contains("File not found"));
  }

  @Test
  public void testImportCalendarFromEmptyFile() {
    importer.setSuccessCount = 0;
    importer.setTotalCount = 0;
    controller.importCalendarFromFile("src/test/resources/importTest.csv");
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
    assertTrue(view.lastDisplayMessage.contains(
        "The selected file was empty or contained no valid data rows"));
  }

  @Test
  public void testImportCalendarFromFileWithMessageFromImporter() {
    importer.setReturnMessage = "test message";
    controller.importCalendarFromFile("src/test/resources/importTest.csv");
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
    assertTrue(view.lastDisplayMessage.contains(
        "test message"));
  }

  // edit event

  private EventData createExistingNonRecurringEventData(LocalDate date) {
    return EventData.getBuilder()
        .setSubject("Existing Event")
        .setStartTime(date.atTime(9, 0))
        .setEndTime(date.atTime(10, 0))
        .setDescription("Old Desc")
        .setLocation("Old Loc")
        .setIsPublic(true)
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
  }

  private EventData createNewNonRecurringEventData(LocalDate date) {
    return EventData.getBuilder()
        .setSubject("New Subject")
        .setStartTime(date.atTime(9, 30))
        .setEndTime(date.atTime(10, 30))
        .setDescription("New Desc")
        .setLocation("New Loc")
        .setIsPublic(false)
        .setIsRecurring(false)
        .setIsAllDay(false)
        .build();
  }

  private EventData createExistingRecurringEventData(LocalDate date, Set<CalendarWeekDays> days,
      LocalDateTime until, Integer occurrence) {
    return EventData.getBuilder()
        .setSubject("Existing Recurring")
        .setStartTime(date.atTime(11, 0))
        .setEndTime(date.atTime(12, 0))
        .setDescription("Desc")
        .setLocation("Loc")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(new RecurrenceData(occurrence, days, until))
        .build();
  }

  private EventData createNewRecurringEventDataNoRecurrenceChange(LocalDate date,
      Set<CalendarWeekDays> days, LocalDateTime until) {
    // Change non-recurring fields only
    return EventData.getBuilder()
        .setSubject("New Subject Recurring") // Changed
        .setStartTime(date.atTime(11, 0))
        .setEndTime(date.atTime(12, 0))
        .setDescription("Desc")
        .setLocation("Loc")
        .setIsPublic(false) // Changed
        .setIsRecurring(true)
        .setIsAllDay(false)
        .setRecurringDetails(new RecurrenceData(null, days, until)) // same recurrence
        .build();
  }

  private EventData createNewRecurringEventDataWithRecurrenceChange(LocalDate date,
      Set<CalendarWeekDays> newDays, LocalDateTime newUntil, Integer newOccurrence) {
    return EventData.getBuilder()
        .setSubject("Existing Recurring") // Same subject
        .setStartTime(date.atTime(11, 0))
        .setEndTime(date.atTime(12, 0))
        .setDescription("Desc")
        .setLocation("Loc")
        .setIsPublic(true)
        .setIsRecurring(true)
        .setIsAllDay(true) // Changed
        .setRecurringDetails(
            new RecurrenceData(newOccurrence, newDays, newUntil)) // changed recurrence
        .build();
  }

  @Test
  public void testEditEventSuccess() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    EventData existing = createExistingNonRecurringEventData(date);
    EventData changed = createNewNonRecurringEventData(date);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertEquals(existing.getEndTime(), model.editEventReceived.endTime);
    assertEquals(changed.getSubject(), model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(changed.getStartTime(), model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(changed.getEndTime(), model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(changed.getDescription(),
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(changed.getLocation(), model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(changed.getPublic(), model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertFalse(view.displayRecurringEventOptionsCalled);
    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditEventRecurringNoRecurrenceChangeDisplaysOptions() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataNoRecurrenceChange(date, days,
        until.atStartOfDay());

    controller.editEvent(existing, changed);

    assertTrue(view.displayRecurringEventOptionsCalled);
    assertTrue(view.lastDisplayRecurringEventOptions.contains(EDIT_THIS_EVENT));
    assertTrue(view.lastDisplayRecurringEventOptions.contains(EDIT_THIS_AND_FOLLOWING));
    assertTrue(view.lastDisplayRecurringEventOptions.contains(EDIT_ALL_EVENTS));

    assertFalse(model.editEventCalled);
    assertFalse(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditEventRecurringWithRecurrenceChangeDisplaysLimitedOptions() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> oldDays = Set.of(CalendarWeekDays.T);
    LocalDate oldUntil = LocalDate.of(2025, 7, 1);
    Set<CalendarWeekDays> newDays = Set.of(CalendarWeekDays.W);
    LocalDate newUntil = LocalDate.of(2025, 8, 1);
    EventData existing = createExistingRecurringEventData(date, oldDays, oldUntil.atStartOfDay(),
        null);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, newDays,
        newUntil.atStartOfDay(), null);

    controller.editEvent(existing, changed);

    assertTrue(view.displayRecurringEventOptionsCalled);

    assertFalse(view.lastDisplayRecurringEventOptions.contains(EDIT_THIS_EVENT));
    assertTrue(view.lastDisplayRecurringEventOptions.contains(EDIT_THIS_AND_FOLLOWING));
    assertTrue(view.lastDisplayRecurringEventOptions.contains(EDIT_ALL_EVENTS));

    assertFalse(model.editEventCalled);
    assertFalse(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testSelectedRecurringEventOption_EditThisEvent() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataNoRecurrenceChange(date, days,
        until.atStartOfDay());
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_EVENT);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNotNull(model.editEventReceived.endTime);
    assertEquals(existing.getEndTime(), model.editEventReceived.endTime);

    assertEquals(changed.getSubject(), model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(changed.getPublic(), model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testSelectedRecurringEventOption_EditThisAndFollowing() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataNoRecurrenceChange(date, days,
        until.atStartOfDay());
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(changed.getSubject(), model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(changed.getPublic(), model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getRecurringDetails());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testSelectedRecurringEventOption_EditAllEvents() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataNoRecurrenceChange(date, days,
        until.atStartOfDay());
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_ALL_EVENTS);

    assertTrue(model.editEventCalled);
    assertNull(model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(changed.getSubject(), model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(changed.getPublic(), model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getRecurringDetails());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditEventExceptionDuringEditCommand() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    EventData existing = createExistingNonRecurringEventData(date);
    EventData changed = createNewNonRecurringEventData(date);
    model.shouldThrowEventConflictException = true;

    controller.editEvent(existing, changed);

    assertTrue(model.editEventCalled);
    assertTrue(view.displayErrorCalled);
    assertFalse(view.showDayViewDialogCalled);
    assertFalse(view.displayMessageCalled);
  }

  @Test
  public void testEditRecurringDetailsExistingUntilWithNewUntil() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    LocalDate newUntil = LocalDate.of(2025, 8, 1);
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, days,
        newUntil.atStartOfDay(), null);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(newUntil.atStartOfDay(),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());
    assertEquals(Set.of(CalendarDayOfWeek.T),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditRecurringDetailsExistingUntilWithNewOccurrences() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    LocalDate until = LocalDate.of(2025, 7, 1);
    Integer newOccurrences = 10;
    EventData existing = createExistingRecurringEventData(date, days, until.atStartOfDay(), null);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, days, null,
        newOccurrences);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());
    assertEquals(Set.of(CalendarDayOfWeek.T),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(newOccurrences,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditRecurringDetailsExistingOccurrenceWithNewUntil() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    Integer existingOccurrences = 10;
    LocalDate newUntil = LocalDate.of(2025, 8, 1);
    EventData existing = createExistingRecurringEventData(date, days, null, existingOccurrences);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, days,
        newUntil.atStartOfDay(), null);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(newUntil.atStartOfDay(),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());
    assertEquals(Set.of(CalendarDayOfWeek.T),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditRecurringDetailsExistingOccurrencesWithNewOccurrences() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    Integer existingOccurrences = 5;
    Integer newOccurrences = 10;
    EventData existing = createExistingRecurringEventData(date, days, null, existingOccurrences);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, days, null,
        newOccurrences);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());
    assertEquals(Set.of(CalendarDayOfWeek.T),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(newOccurrences,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditRecurringDetailsWeekdays() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    Set<CalendarWeekDays> days = Set.of(CalendarWeekDays.T);
    Set<CalendarWeekDays> newDays = Set.of(CalendarWeekDays.W);
    Integer existingOccurrences = 5;
    EventData existing = createExistingRecurringEventData(date, days, null, existingOccurrences);
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, newDays, null,
        existingOccurrences);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());
    assertEquals(Set.of(CalendarDayOfWeek.W),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(existingOccurrences,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  @Test
  public void testEditNonRecurringEventRecurrenceDetails() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    EventData existing = createExistingNonRecurringEventData(date);
    Set<CalendarWeekDays> newDays = Set.of(CalendarWeekDays.W);
    Integer newOccurrence = 5;
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, newDays, null,
        newOccurrence);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertEquals(existing.getEndTime(), model.editEventReceived.endTime);
    assertEquals(changed.getSubject(), model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(changed.getStartTime(), model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(changed.getEndTime(), model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(changed.getDescription(),
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(changed.getLocation(), model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNotNull(null, model.editEventReceived.parametersToUpdate.getRecurringDetails());
    assertEquals(Set.of(CalendarDayOfWeek.W),
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getRepeatDays());
    assertEquals(newOccurrence,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getOccurrences());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getRecurringDetails().getUntilDate());

    assertFalse(view.displayRecurringEventOptionsCalled);
    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }


  @Test
  public void testEditNoChangeInRecurrenceDetails() {
    LocalDate date = LocalDate.of(2025, 6, 1);
    EventData existing = createExistingRecurringEventData(date, Set.of(CalendarWeekDays.W), null,
        5);
    Set<CalendarWeekDays> newDays = Set.of(CalendarWeekDays.W);
    Integer newOccurrence = 5;
    EventData changed = createNewRecurringEventDataWithRecurrenceChange(date, newDays, null,
        newOccurrence);
    model.setGetEventsOnDate = Collections.emptyList();

    controller.editEvent(existing, changed);

    controller.editEvent(existing, changed);
    assertTrue(view.displayRecurringEventOptionsCalled);

    controller.selectedRecurringEventOption(EDIT_THIS_AND_FOLLOWING);

    assertTrue(model.editEventCalled);
    assertEquals(existing.getSubject(), model.editEventReceived.name);
    assertNotNull(model.editEventReceived.startTime);
    assertEquals(existing.getStartTime(), model.editEventReceived.startTime);
    assertNull(model.editEventReceived.endTime);

    assertEquals(null, model.editEventReceived.parametersToUpdate.getSubject());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getStartTime());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getEndTime());
    assertEquals(null,
        model.editEventReceived.parametersToUpdate.getDescription());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getLocation());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsPublic());
    assertEquals(true, model.editEventReceived.parametersToUpdate.getIsAllDay());
    assertEquals(null, model.editEventReceived.parametersToUpdate.getIsRecurring());
    assertNull(model.editEventReceived.parametersToUpdate.getRecurringDetails());

    assertTrue(model.getEventsOnDateCalled);
    assertTrue(view.showDayViewDialogCalled);
    assertTrue(view.displayMessageCalled);
    assertFalse(view.displayErrorCalled);
  }

  // jump to today

  @Test
  public void testRequestThisMonthView() {
    LocalDate today = LocalDate.now();
    YearMonth current = YearMonth.of(today.getYear(), today.getMonth());
    controller.requestThisMonthView();

    assertTrue(view.setMonthYearLabelCalled);
    assertEquals(current, view.lastSetMonthYearLabelMonthYear);
    assertTrue(view.setCalendarMonthDatesCalled);
    assertEquals(current, view.lastSetCalendarMonthDatesMonthYear);
  }
}