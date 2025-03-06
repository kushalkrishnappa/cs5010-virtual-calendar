package dto;

import model.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * This interface represents the getter methods for the recurring event DTO. This will enforce the
 * required attributes to be instantiated in the RecurringEventDTO class.
 */
public interface IRecurringEventDTO {

  /**
   * Returns the number of occurrences of the recurring event.
   *
   * @return the number of occurrences of the recurring event
   */
  int getOccurrences();

  /**
   * Returns the repeat days of the recurring event.
   *
   * @return the repeat days of the recurring event
   */
  Set<DayOfWeek> getRepeatDays();

  /**
   * Returns the end date of the recurring event.
   *
   * @return the end date of the recurring event
   */
  LocalDateTime getUntilDate();


}
