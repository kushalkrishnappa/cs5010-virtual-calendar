package dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import model.CalendarDayOfWeek;

/**
 * Data Transfer Object for recurring details.
 */
public class RecurringDetailsDTO {

  private final Integer occurrences;
  private final Set<CalendarDayOfWeek> repeatDays;
  private final LocalDateTime untilDate;

  /**
   * Constructor for RecurringDetailsDTO.
   *
   * @param occurrences The number of occurrences.
   * @param repeatDays  The days of the week to repeat.
   * @param untilDate   The date to repeat until.
   */
  private RecurringDetailsDTO(
      Integer occurrences,
      Set<CalendarDayOfWeek> repeatDays,
      LocalDateTime untilDate) {
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  /**
   * Get a new RecurringDetailsDTOBuilder.
   *
   * @return A new RecurringDetailsDTOBuilder.
   */
  public static RecurringDetailsDTOBuilder getBuilder() {
    return new RecurringDetailsDTOBuilder();
  }

  /**
   * Builder for RecurringDetailsDTO.
   */
  public static class RecurringDetailsDTOBuilder {

    private Integer occurrences;
    private Set<CalendarDayOfWeek> repeatDays;
    private LocalDateTime untilDate;

    /**
     * Constructor for RecurringDetailsDTOBuilder.
     */
    private RecurringDetailsDTOBuilder() {
      this.occurrences = null;
      this.repeatDays = null;
      this.untilDate = null;
    }

    /**
     * Set the number of occurrences.
     *
     * @param occurrences The number of occurrences.
     * @return The RecurringDetailsDTOBuilder.
     */
    public RecurringDetailsDTOBuilder setOccurrences(Integer occurrences) {
      this.occurrences = occurrences;
      return this;
    }

    /**
     * Set the days of the week to repeat.
     *
     * @param repeatDays The days of the week to repeat.
     * @return The RecurringDetailsDTOBuilder.
     */
    public RecurringDetailsDTOBuilder setRepeatDays(Set<CalendarDayOfWeek> repeatDays) {
      this.repeatDays = repeatDays;
      return this;
    }

    /**
     * Set the date to repeat until.
     *
     * @param untilDate The date to repeat until.
     * @return The RecurringDetailsDTOBuilder.
     */
    public RecurringDetailsDTOBuilder setUntilDate(LocalDateTime untilDate) {
      this.untilDate = untilDate;
      return this;
    }

    /**
     * Build a new RecurringDetailsDTO.
     *
     * @return A new RecurringDetailsDTO.
     */
    public RecurringDetailsDTO build() {
      return new RecurringDetailsDTO(occurrences, repeatDays, untilDate);
    }
  }

  /**
   * Get the number of occurrences.
   *
   * @return The number of occurrences.
   */
  public Integer getOccurrences() {
    return this.occurrences;
  }

  /**
   * Get the days of the week to repeat.
   *
   * @return The days of the week to repeat.
   */
  public Set<CalendarDayOfWeek> getRepeatDays() {
    return this.repeatDays;
  }

  /**
   * Get the date to repeat until.
   *
   * @return The date to repeat until.
   */
  public LocalDateTime getUntilDate() {
    return this.untilDate;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RecurringDetailsDTO)) {
      return false;
    }
    RecurringDetailsDTO that = (RecurringDetailsDTO) o;
    return Objects.equals(occurrences, that.occurrences)
        && Objects.equals(repeatDays, that.repeatDays)
        && Objects.equals(untilDate, that.untilDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(occurrences, repeatDays, untilDate);
  }
}
