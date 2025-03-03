package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * ICalendar interface defines the methods for view of mvc architecture for the CalendarApp
 * application.
 *
 * <p>It provides methods to create, edit, and retrieve events from the calendar.
 *
 * <p>It also provides methods to export the calendar to a CSV file and check if the user is busy at
 * a given time.
 *
 */
public interface ICalendar {

  // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
  void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException;

  // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
  // repeats <weekdays> for <N> times
  void createRecurringEvent(EventDTO eventDTO, boolean autoDecline, Set<DayOfWeek> repeatDays,
      int occurrences)
      throws EventConflictException, IllegalArgumentException;

  // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
  // repeats <weekdays> until <dateStringTtimeString>
  void createRecurringEvent(EventDTO eventDTO, boolean autoDecline, Set<DayOfWeek> repeatDays,
      LocalDateTime endDate)
      throws EventConflictException, IllegalArgumentException;

  // create event --autoDecline <eventName> on <dateStringTtimeString>
  void createAllDayEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException;

  // create event <eventName> on <dateStringTtimeString> repeats <weekdays> for <N> times
  void createRecurringAllDayEvent(EventDTO eventDTO, boolean autoDecline, Set<DayOfWeek> repeatDays,
      int occurrences)
      throws EventConflictException, IllegalArgumentException;

  // create event <eventName> on <dateStringTtimeString> repeats <weekdays>
  // until <dateStringTtimeString>
  void createRecurringAllDayEvent(EventDTO eventDTO, boolean autoDecline, Set<DayOfWeek> repeatDays,
      LocalDateTime endDate)
      throws EventConflictException, IllegalArgumentException;

  // edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString>
  // with <NewPropertyValue>

  /**
   * @return the number of events edited
   */
  <T> Integer editEvent(String subject, LocalDateTime start, LocalDateTime end,
      Property<T> property,
      T newValue)
      throws IllegalArgumentException;

  // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>

  /**
   * @return the number of events edited
   */
  <T> Integer editEventsStartingFrom(String subject, LocalDateTime start, Property<T> property,
      T newValue)
      throws IllegalArgumentException;

  // edit events <property> <eventName> <NewPropertyValue>

  /**
   * @return the number of events edited
   */
  <T> Integer editAllEvents(String subject, Property<T> property, T newValue)
      throws IllegalArgumentException;


  // print events on <dateStringTtimeString>
  List<EventDTO> getEventsOnDate(LocalDate date);

  // print events from <dateStringTtimeString> to <dateStringTtimeString>
  List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end);

  // export cal fileName.csv
  String exportToCSV(String fileName) throws CalendarExportException;

  // show status on <dateStringTtimeString>
  Boolean isBusy(LocalDateTime dateTime);
}
