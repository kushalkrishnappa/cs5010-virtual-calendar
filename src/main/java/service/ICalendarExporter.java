package service;

import dto.EventDTO;
import java.util.List;

public interface ICalendarExporter {

  String export(List<EventDTO> events, String fileName);

}
