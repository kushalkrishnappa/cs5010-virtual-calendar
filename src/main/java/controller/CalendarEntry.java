package controller;

import exception.InvalidTimeZoneException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;
import model.IModel;

/**
 * This class represents a calendar entry with a model and a time zone. Each calendar entry will
 * have a model and a time zone associated with it. The time zone is represented as a string in IANA
 * TZ format.
 *
 * <p>The CalendarEntry class is immutable and can be built using the builder pattern.
 */
class CalendarEntry {

  final IModel model;
  final ZoneId zoneId;

  /**
   * Constructor for CalendarEntry to initialize the model and time zone.
   *
   * @param model  the model associated with this calendar entry
   * @param zoneId the time zone set for this calendar entry
   */
  private CalendarEntry(IModel model, ZoneId zoneId) {
    this.model = model;
    this.zoneId = zoneId;
  }

  /**
   * This method returns the builder for the CalendarEntry class to create a new instance.
   *
   * @return builder for CalendarEntry
   */
  static CalendarEntryBuilder getBuilder() {
    return new CalendarEntryBuilder();
  }

  /**
   * This class represents the builder for the CalendarEntry class.
   */
  static class CalendarEntryBuilder {

    private IModel model;
    private ZoneId zoneId;

    /**
     * Constructor is private to enforce the use of the builder pattern.
     */
    private CalendarEntryBuilder() {
      model = null;
      zoneId = null;
    }

    /**
     * This method sets the model for the CalendarEntry.
     *
     * @param model the model associated with this calendar entry
     * @return this builder instance
     */
    CalendarEntryBuilder setModel(IModel model) {
      this.model = model;
      return this;
    }

    /**
     * This method sets the time zone for the CalendarEntry.
     *
     * @param zoneId the time zone set for this calendar entry
     * @return this builder instance
     * @throws IllegalArgumentException if the time zone is invalid
     */
    CalendarEntryBuilder setZoneId(String zoneId)
        throws IllegalArgumentException {
      parseTimeZone(zoneId);
      return this;
    }

    private void parseTimeZone(String zoneId)
        throws InvalidTimeZoneException {
      try {
        this.zoneId = ZoneId.of(zoneId);
      } catch (ZoneRulesException e) {
        throw new InvalidTimeZoneException(
            "Invalid timezone specified for time zone " + zoneId);
      } catch (DateTimeException e) {
        throw new InvalidTimeZoneException(
            "Expected timezone in IANA TZ format: \"area/location\" ");
      }
    }

    /**
     * This method builds a new instance of CalendarEntry with the specified model and time zone.
     *
     * @return the new instance of CalendarEntry
     */
    CalendarEntry build() {
      Objects.requireNonNull(model, "model is null");
      Objects.requireNonNull(zoneId, "zoneId is null");
      return new CalendarEntry(model, zoneId);
    }
  }
}
