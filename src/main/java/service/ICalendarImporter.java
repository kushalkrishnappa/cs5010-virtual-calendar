package service;

import dto.EventDTO;
import dto.ImportResult;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * This interface represents strategies to import events into the calendar model from an external
 * source.
 */
public interface ICalendarImporter {

  /**
   * Imports events from the given reader, invoking the consumer for each valid EventDTO.
   *
   * @param reader        the reader providing the event data
   * @param eventConsumer a Consumer function that takes a successfully parsed EventDTO and
   *                      processes it
   * @return an ImportResult object containing the count of successfully processed records (i.e.,
   *         parsed and accepted by the consumer without error) and the total records attempted
   * @throws IOException if a I/O error occurs while reading the source
   */
  ImportResult importEvents(Reader reader, Consumer<EventDTO> eventConsumer)
      throws IOException;
}