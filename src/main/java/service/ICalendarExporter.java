package service;

import dto.EventDTO;
import java.util.List;

/**
 * This interface represents strategies to export the events in the calendar model.
 */
public interface ICalendarExporter {

  /**
   * This method receives a list of all events in the model and should return a formatted string of
   * the required specification.
   *
   * @param events the list of all events in the calendar
   * @return the formatted string that this class requires the events to be in
   */
  String export(List<EventDTO> events);

}
