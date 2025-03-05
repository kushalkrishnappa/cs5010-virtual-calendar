package controller.command;


import dto.EventDTO;
import exception.EventConflictException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;
import model.DayOfWeek;
import model.IModel;

public class CreateEventCommand implements Command {

  private Runnable command;
  private final IModel model;
  private final Scanner lineScanner;
  private final DateTimeFormatter dateTimeFormatter;
  private final DateTimeFormatter dateFormatter;

  private String eventName;
  private boolean autoDecline;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Set<DayOfWeek> repeatDays;
  private LocalDateTime untilTime;
  private int times;


  public CreateEventCommand(IModel model, Scanner commandScanner,
      DateTimeFormatter dateTimeFormatter, DateTimeFormatter dateFormatter)
      throws ParseCommandException {
    this.model = model;
    this.lineScanner = commandScanner;
    this.dateTimeFormatter = dateTimeFormatter;
    this.dateFormatter = dateFormatter;
    parseCommand();
  }

  @Override
  public void execute() throws EventConflictException {
    command.run();
  }

  private void parseCommand() throws ParseCommandException {
    if (!lineScanner.next().equals("event")) {
      throw new ParseCommandException("Invalid command (not create event)");
    }

    String next = lineScanner.next();
    if (next.equals("--autoDecline")) {
      autoDecline = true;
      eventName = lineScanner.next();
    } else {
      eventName = next;
    }
    switch (lineScanner.next()) {
      case "from":
        handleCreateSpannedEvent();
        break;
      case "on":
        handleCreateAllDayEvent();
        break;
      default:
        throw new ParseCommandException("Invalid format");
    }
  }

  private void handleCreateAllDayEvent() throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(lineScanner.next(), dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid date format");
    }

    if (!lineScanner.hasNext()) {
      command = () -> model.createAllDayEvent(EventDTO.getBuilder()
          .setSubject(eventName)
          .setStartTime(startTime)
          .build(), autoDecline);
      return;
    }

    if (!lineScanner.next().equals("repeats")) {
      throw new ParseCommandException("Invalid format (not repeats)");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(lineScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Repeat days is not specified correctly");
    }

    switch (lineScanner.next()) {
      case "for":
        handleCreateAllDayNTimesEvent();
        break;
      case "until":
        handleCreateAllDayUntilEvent();
        break;
    }
  }

  private void handleCreateAllDayUntilEvent()
      throws ParseCommandException {
    try {
      untilTime = LocalDateTime.parse(lineScanner.next(), dateFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid untilTime format");
    }

    command = () -> model.createRecurringAllDayEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .build(), autoDecline, repeatDays, this.untilTime);
  }

  private void handleCreateAllDayNTimesEvent()
      throws ParseCommandException {
    times = Integer.parseInt(lineScanner.next());
    if (!lineScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }

    command = () -> model.createRecurringAllDayEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .build(), autoDecline, repeatDays, times);
  }

  private void handleCreateSpannedEvent() throws ParseCommandException {
    try {
      startTime = LocalDateTime.parse(lineScanner.next(), dateTimeFormatter);
      if (!lineScanner.next().equals("to")) {
        throw new ParseCommandException("Invalid format");
      }
      endTime = LocalDateTime.parse(lineScanner.next(), dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid time format");
    }

    if (!lineScanner.hasNext()) {
      command = () -> model.createEvent(EventDTO.getBuilder()
          .setSubject(eventName)
          .setStartTime(startTime)
          .setEndTime(endTime)
          .build(), autoDecline);
      return;
    }

    if (!lineScanner.next().equals("repeats")) {
      throw new ParseCommandException("Invalid format (not repeats)");
    }

    try {
      repeatDays = DayOfWeek.parseRepeatDays(lineScanner.next());
    } catch (IllegalArgumentException e) {
      throw new ParseCommandException("Repeat days is not specified correctly");
    }

    switch (lineScanner.next()) {
      case "for":
        handleCreateSpannedNTimesEvent();
        break;
      case "until":
        handleCreateSpannedUntilEvent();
        break;
    }
  }

  private void handleCreateSpannedUntilEvent() throws ParseCommandException {
    try {
      untilTime = LocalDateTime.parse(lineScanner.next(), dateTimeFormatter);
    } catch (DateTimeParseException e) {
      throw new ParseCommandException("Invalid untilTime format");
    }

    command = () -> model.createRecurringEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .build(), autoDecline, repeatDays, untilTime);
  }

  private void handleCreateSpannedNTimesEvent() throws ParseCommandException {
    times = Integer.parseInt(lineScanner.next());
    if (!lineScanner.next().equals("times")) {
      throw new ParseCommandException("Invalid format (not <N> times)");
    }

    command = () -> model.createRecurringEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .build(), autoDecline, repeatDays, times);
  }

}
