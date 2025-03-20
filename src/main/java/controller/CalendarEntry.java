package controller;

import exception.InvalidTimeZoneException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;
import model.IModel;

class CalendarEntry {

  final IModel model;
  final ZoneId zoneId;

  private CalendarEntry(IModel model, ZoneId zoneId) {
    this.model = model;
    this.zoneId = zoneId;
  }

  static CalendarEntryBuilder getBuilder() {
    return new CalendarEntryBuilder();
  }

  static class CalendarEntryBuilder {

    private IModel model;
    private ZoneId zoneId;

    private CalendarEntryBuilder() {
      model = null;
      zoneId = null;
    }

    CalendarEntryBuilder setModel(IModel model) {
      this.model = model;
      return this;
    }

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

    CalendarEntry build() {
      Objects.requireNonNull(model, "model is null");
      Objects.requireNonNull(zoneId, "zoneId is null");
      return new CalendarEntry(model, zoneId);
    }
  }
}
