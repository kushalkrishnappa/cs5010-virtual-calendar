package dto;

import java.time.LocalDateTime;
import java.util.Set;
import model.CalendarDayOfWeek;

public class RecurringDetailsDTO {

  private final Integer occurrences;
  private final Set<CalendarDayOfWeek> repeatDays;
  private final LocalDateTime untilDate;

  private RecurringDetailsDTO(
      Integer occurrences,
      Set<CalendarDayOfWeek> repeatDays,
      LocalDateTime untilDate) {
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  public static RecurringDetailsDTOBuilder getBuilder() {
    return new RecurringDetailsDTOBuilder();
  }

  public static class RecurringDetailsDTOBuilder {

    private Integer occurrences;
    private Set<CalendarDayOfWeek> repeatDays;
    private LocalDateTime untilDate;

    private RecurringDetailsDTOBuilder() {
      this.occurrences = null;
      this.repeatDays = null;
      this.untilDate = null;
    }

    public RecurringDetailsDTOBuilder setOccurrences(Integer occurrences) {
      this.occurrences = occurrences;
      return this;
    }

    public RecurringDetailsDTOBuilder setRepeatDays(Set<CalendarDayOfWeek> repeatDays) {
      this.repeatDays = repeatDays;
      return this;
    }

    public RecurringDetailsDTOBuilder setUntilDate(LocalDateTime untilDate) {
      this.untilDate = untilDate;
      return this;
    }

    public RecurringDetailsDTO build() {
      return new RecurringDetailsDTO(occurrences, repeatDays, untilDate);
    }
  }

  public Integer getOccurrences() {
    return this.occurrences;
  }

  public Set<CalendarDayOfWeek> getRepeatDays() {
    return this.repeatDays;
  }

  public LocalDateTime getUntilDate() {
    return this.untilDate;
  }
}
