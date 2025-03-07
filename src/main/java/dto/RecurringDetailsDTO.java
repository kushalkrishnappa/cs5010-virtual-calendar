package dto;

import java.time.LocalDateTime;
import java.util.Set;
import model.DayOfWeek;

public class RecurringDetailsDTO implements IRecurringDetailsDTO {

  private final Integer occurrences;
  private final Set<DayOfWeek> repeatDays;
  private final LocalDateTime untilDate;

  private RecurringDetailsDTO(
      Integer occurrences,
      Set<DayOfWeek> repeatDays,
      LocalDateTime untilDate) {
    this.occurrences = occurrences;
    this.repeatDays = repeatDays;
    this.untilDate = untilDate;
  }

  public static RecurringDetailsDTOBuilder getBuilder() {
    return new RecurringDetailsDTOBuilder();
  }

  public static class RecurringDetailsDTOBuilder{
    private Integer occurrences;
    private Set<DayOfWeek> repeatDays;
    private LocalDateTime untilDate;

    private RecurringDetailsDTOBuilder() {
    }

    public RecurringDetailsDTOBuilder setOccurrences(int occurrences) {
      this.occurrences = occurrences;
      return this;
    }

    public RecurringDetailsDTOBuilder setRepeatDays(Set<DayOfWeek> repeatDays) {
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

  @Override
  public int getOccurrences() {
    return this.occurrences;
  }

  @Override
  public Set<DayOfWeek> getRepeatDays() {
    return this.repeatDays;
  }

  @Override
  public LocalDateTime getUntilDate() {
    return this.untilDate;
  }
}
