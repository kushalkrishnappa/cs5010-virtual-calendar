package controller;

import java.time.LocalDateTime;
import java.util.Set;

public class RecurrenceData {

  private final Integer occurrences;
  private final Set<CalendarWeekDays> repeatDays;
  private final LocalDateTime untilDate;

  /**
   * Constructor for RecurrenceData.
   *
   * @param occurrences The number of occurrences.
   * @param repeatDays  The days of the week to repeat.
   * @param untilDate   The date to repeat until.
   */
  public RecurrenceData(
      Integer occurrences,
      Set<CalendarWeekDays> repeatDays,
      LocalDateTime untilDate) {
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  /**
   * Get the number of occurrences.
   *
   * @return The number of occurrences.
   */
  public Integer getOccurrences() {
    return occurrences;
  }

  /**
   * Get the days of the week to repeat.
   *
   * @return The days of the week to repeat.
   */
  public Set<CalendarWeekDays> getRepeatDays() {
    return repeatDays;
  }

  /**
   * Get the date to repeat until.
   *
   * @return The date to repeat until.
   */
  public LocalDateTime getUntilDate() {
    return untilDate;
  }

}
