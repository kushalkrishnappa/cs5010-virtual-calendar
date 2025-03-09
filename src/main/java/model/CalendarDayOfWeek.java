package model;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
public enum CalendarDayOfWeek {
  /**
   * Represents Monday.
   */
  M(DayOfWeek.MONDAY),
  /**
   * Represents Tuesday.
   */
  T(DayOfWeek.TUESDAY),
  /**
   * Represents Wednesday.
   */
  W(DayOfWeek.WEDNESDAY),
  /**
   * Represents Thursday.
   */
  R(DayOfWeek.THURSDAY),
  /**
   * Represents Friday.
   */
  F(DayOfWeek.FRIDAY),
  /**
   * Represents Saturday.
   */
  S(DayOfWeek.SATURDAY),
  /**
   * Represents Sunday.
   */
  U(DayOfWeek.SUNDAY);

  private final DayOfWeek dayOfWeek;

  CalendarDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public static Set<CalendarDayOfWeek> parseRepeatDays(String days) throws IllegalArgumentException {
    Set<CalendarDayOfWeek> repeatDays = new HashSet<>();
    for (char day : days.toCharArray()) {
      repeatDays.add(CalendarDayOfWeek.valueOf(String.valueOf(day)));
    }
    return repeatDays;
  }

  static TreeSet<DayOfWeek> getJavaTimeDaysOfWeek(Set<CalendarDayOfWeek> daysOfWeek) {
    TreeSet<DayOfWeek> javaTimeDaysOfWeek = new TreeSet<>();
    for (CalendarDayOfWeek day : daysOfWeek) {
      javaTimeDaysOfWeek.add(day.dayOfWeek);
    }
    return javaTimeDaysOfWeek;
  }
}