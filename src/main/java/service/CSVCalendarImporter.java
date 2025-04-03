package service;

import dto.EventDTO;
import dto.ImportResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CSVCalendarImporter implements ICalendarImporter {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
  private final Map<String, BiConsumer<ImportEventDetails, String>> fieldSetters;

  public CSVCalendarImporter() {
    this.fieldSetters = createPropertySetters();
  }

  private static class ImportEventDetails {

    String subject;
    LocalDate startDate;
    LocalTime startTime;
    LocalDate endDate;
    LocalTime endTime;
    Boolean allDay;
    String description;
    String location;
    Boolean privateEvent;

    public ImportEventDetails() {
      this.subject = null;
      this.startDate = null;
      this.startTime = null;
      this.endDate = null;
      this.endTime = null;
      this.allDay = null;
      this.description = null;
      this.location = null;
      this.privateEvent = null;
    }
  }

  private final Map<String, BiConsumer<ImportEventDetails, String>> createPropertySetters() {
    Map<String, BiConsumer<ImportEventDetails, String>> setters = new HashMap<>();
    setters.put("Subject",
        (event, value) -> event.subject = value);
    setters.put("Start Date",
        (event, value) ->
            event.startDate = LocalDate.parse(value, dateFormatter));
    setters.put("Start Time",
        (event, value) ->
            event.startTime = LocalTime.parse(value, timeFormatter));
    setters.put("End Date",
        (event, value) ->
            event.endDate = LocalDate.parse(value, dateFormatter));
    setters.put("End Time",
        (event, value) ->
            event.endTime = LocalTime.parse(value, timeFormatter));
    setters.put("All Day Event",
        (event, value) ->
            event.allDay = Boolean.parseBoolean(value));
    setters.put("Description",
        (event, value) -> event.description = value);
    setters.put("Location",
        (event, value) -> event.location = value);
    setters.put("Private",
        (event, value) -> event.privateEvent = Boolean.parseBoolean(value));
    return setters;
  }

  @Override
  public ImportResult importEvents(Reader reader, Consumer<EventDTO> eventConsumer)
      throws IOException {
    int successCount = 0;
    int totalCount = 0;
    String line;

    try (BufferedReader br = new BufferedReader(reader)) {
      line = br.readLine();

      if (Objects.isNull(line)) {
        return new ImportResult(0, 0, "Empty CSV file");
      }

      if (line.trim().isEmpty()) {
        return new ImportResult(0, 0, "CSV header mismatch or missing");
      }

      List<String> columnHeaders = splitCSVLineWithEscapedQuotes(line);
      if (!columnHeaders.contains("Subject") || !columnHeaders.contains("Start Date")) {
        return new ImportResult(0, 0, "Missing Subject or Start Date");
      }

      List<BiConsumer<ImportEventDetails, String>> columnSetters = new ArrayList<>();
      for (String columnHeader : columnHeaders) {
        if (Objects.isNull(fieldSetters.get(columnHeader))) {
          return new ImportResult(0, 0, "Invalid column header: " + columnHeader);
        }
        columnSetters.add(fieldSetters.get(columnHeader));
      }

      line = br.readLine();
      // read events
      while (Objects.nonNull(line)) {
        if (line.trim().isEmpty()) {
          // skip empty lines
          line = br.readLine();
          continue;
        }
        totalCount++;
        ImportEventDetails eventDetails = new ImportEventDetails();

        // parse line and set fields
        List<String> values = splitCSVLineWithEscapedQuotes(line);
        if (values.size() != columnHeaders.size()) {
          // skip event
          line = br.readLine();
          continue;
        }

        for (int i = 0; i < columnSetters.size(); i++) {
          try {
            columnSetters.get(i).accept(eventDetails, values.get(i));
          } catch (Exception e) {
            //continue
          }
        }

        // update values as per our model
        EventDTO eventDTO = getEventDTOFromImportEventDetails(eventDetails);

        try {
          eventConsumer.accept(eventDTO);
          successCount++;
        } catch (Exception e) {
          //skip event
        }
        line = br.readLine();
      }

    }
    //

    return new ImportResult(successCount, totalCount, null);
  }

  private EventDTO getEventDTOFromImportEventDetails(ImportEventDetails eventDetails) {
    return EventDTO.getBuilder()
        .setSubject(eventDetails.subject)
        .setDescription(eventDetails.description)
        .setLocation(eventDetails.location)
        .setIsPublic(!eventDetails.privateEvent)
        .setStartTime(Objects.nonNull(eventDetails.startTime)
            ? eventDetails.startDate.atTime(eventDetails.startTime)
            : eventDetails.startDate.atStartOfDay())
        .setEndTime(Objects.isNull(eventDetails.endDate)
            ? Objects.isNull(eventDetails.endTime)
            ? null
            : eventDetails.startDate.atTime(eventDetails.endTime)
            : Objects.nonNull(eventDetails.endTime)
                ? eventDetails.endDate.atTime(eventDetails.endTime)
                : eventDetails.endDate.atStartOfDay())
        .setIsAllDay(Objects.nonNull(eventDetails.allDay)
            ? eventDetails.allDay
            : Objects.nonNull(eventDetails.endDate)
                ? Objects.nonNull(eventDetails.endTime)
                ? Boolean.FALSE
                : eventDetails.endDate.isEqual(eventDetails.startDate.plusDays(1))
                    ? Boolean.TRUE
                    : Boolean.FALSE
                : Boolean.TRUE)
        .build();
  }

  public static List<String> splitCSVLineWithEscapedQuotes(String line) {
    List<String> tokens = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder currentToken = new StringBuilder();

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        // Handle escaped quotes
        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
          currentToken.append('"'); // Append a single quote
          i++; // Skip the next quote
        } else {
          inQuotes = !inQuotes; // Toggle quote state
        }
      } else if (c == ',' && !inQuotes) {
        tokens.add(currentToken.toString());
        currentToken.setLength(0); // Reset the current token
      } else {
        currentToken.append(c);
      }
    }
    tokens.add(currentToken.toString()); // Add the last token
    return tokens;
  }
}
