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
   * Monday.
   */
  M(DayOfWeek.MONDAY),

  /**
   * Tuesday.
   */
  T(DayOfWeek.TUESDAY),

  /**
   * Wednesday.
   */
  W(DayOfWeek.WEDNESDAY),

  /**
   * Thursday.
   */
  R(DayOfWeek.THURSDAY),

  /**
   * Friday.
   */
  F(DayOfWeek.FRIDAY),

  /**
   * Saturday.
   */
  S(DayOfWeek.SATURDAY),

  /**
   * Sunday.
   */
  U(DayOfWeek.SUNDAY);

  private final DayOfWeek dayOfWeek;

  /**
   * Constructs a CalendarDayOfWeek enum constant.
   *
   * @param dayOfWeek the Java Time object corresponding to the day of the week
   */
  CalendarDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  /**
   * Parses a string representation of the days of the week into a set of CalendarDayOfWeek objects.
   *
   * @param days the string representation of the days of the week
   * @return the set of days of the week
   * @throws IllegalArgumentException if the string is not a valid representation of the days of the
   */
  public static Set<CalendarDayOfWeek> parseRepeatDays(String days)
      throws IllegalArgumentException {
    Set<CalendarDayOfWeek> repeatDays = new HashSet<>();
    for (char day : days.toCharArray()) {
      repeatDays.add(CalendarDayOfWeek.valueOf(String.valueOf(day)));
    }
    return repeatDays;
  }

  /**
   * Gets the Java Time object specific to the day of the week.
   *
   * @param daysOfWeek the set of days of the week to convert
   * @return the sorted set of days of the week as Java Time objects
   */
  static TreeSet<DayOfWeek> getJavaTimeDaysOfWeek(Set<CalendarDayOfWeek> daysOfWeek) {
    TreeSet<DayOfWeek> javaTimeDaysOfWeek = new TreeSet<>();
    for (CalendarDayOfWeek day : daysOfWeek) {
      javaTimeDaysOfWeek.add(day.dayOfWeek);
    }
    return javaTimeDaysOfWeek;
  }
}