package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the days of the week. Used for specifying recurring events in a calendar.
 *
 * <p>The enum constants represent the days of the week as follows:
 * <ul>
 * <li>{@code M}: Monday</li>
 * <li>{@code T}: Tuesday</li>
 * <li>{@code W}: Wednesday</li>
 * <li>{@code R}: Thursday</li>
 * <li>{@code F}: Friday</li>
 * <li>{@code S}: Saturday</li>
 * <li>{@code U}: Sunday</li>
 * </ul>
 */
public enum DayOfWeek {
  /**
   * Represents Monday.
   */
  M,
  /**
   * Represents Tuesday.
   */
  T,
  /**
   * Represents Wednesday.
   */
  W,
  /**
   * Represents Thursday.
   */
  R,
  /**
   * Represents Friday.
   */
  F,
  /**
   * Represents Saturday.
   */
  S,
  /**
   * Represents Sunday.
   */
  U;

  public static Set<DayOfWeek> parseRepeatDays(String days) throws IllegalArgumentException {
    Set<DayOfWeek> repeatDays = new HashSet<>();
    for (char day : days.toCharArray()) {
      repeatDays.add(DayOfWeek.valueOf(String.valueOf(day)));
    }
    return repeatDays;
  }
}