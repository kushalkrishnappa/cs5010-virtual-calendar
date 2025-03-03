package model;

import dto.EventDTO;
import exception.CalendarExportException;
import exception.EventConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Calendar implements IModel {

  @Override
  public void createEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public void createRecurringEvent(EventDTO eventDTO, boolean autoDecline,
      Set<DayOfWeek> repeatDays, int occurrences)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public void createRecurringEvent(EventDTO eventDTO, boolean autoDecline,
      Set<DayOfWeek> repeatDays, LocalDateTime endDate)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public void createAllDayEvent(EventDTO eventDTO, boolean autoDecline)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public void createRecurringAllDayEvent(EventDTO eventDTO, boolean autoDecline,
      Set<DayOfWeek> repeatDays, int occurrences)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public void createRecurringAllDayEvent(EventDTO eventDTO, boolean autoDecline,
      Set<DayOfWeek> repeatDays, LocalDateTime endDate)
      throws EventConflictException, IllegalArgumentException {

  }

  @Override
  public <T> Integer editEvent(String subject, LocalDateTime start, LocalDateTime end,
      Property<T> property, T newValue) throws IllegalArgumentException {
    return 0;
  }

  @Override
  public <T> Integer editEventsStartingFrom(String subject, LocalDateTime start,
      Property<T> property, T newValue) throws IllegalArgumentException {
    return 0;
  }

  @Override
  public <T> Integer editAllEvents(String subject, Property<T> property, T newValue)
      throws IllegalArgumentException {
    return 0;
  }

  @Override
  public List<EventDTO> getEventsOnDate(LocalDate date) {
    return List.of();
  }

  @Override
  public List<EventDTO> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public String exportToCSV(String fileName) throws CalendarExportException {
    return "";
  }

  @Override
  public Boolean isBusy(LocalDateTime dateTime) {
    return null;
  }
}
