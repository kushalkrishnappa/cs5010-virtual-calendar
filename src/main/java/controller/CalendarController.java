package controller;

import dto.EventDTO;
import exception.EventConflictException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import model.DayOfWeek;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  IView view;
  IModel model;
  Scanner scanner;
  DateTimeFormatter formatter;

  public CalendarController(IModel model, InputStream in) {
    this.model = model;
    this.scanner = new Scanner(in);
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  }

  public CalendarController(IModel model, String filePath) throws FileNotFoundException {
    this.model = model;
    this.scanner = new Scanner(new File(filePath));
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  }

  @Override
  public void exitProgram() {
    scanner.close();
    System.exit(0);
  }

  @Override
  public void startProgram() {
    if (null == scanner) {
      return;
    }
    if (view == null) {
      throw new IllegalStateException("View must be added before starting the program.");
    }

    view.displayOutput("");
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      Scanner lineScanner = new Scanner(line);
      if (!lineScanner.hasNext()) {
        view.displayOutput("");
        continue;
      }
      try {
        switch (lineScanner.next()) {
          case "create":
            handleCreateCommand(lineScanner);
            break;
          case "edit":
            handleEditCommand();
            break;
          case "print":
            handlePrintCommand();
            break;
          case "export":
            handleExportCommand();
            break;
          case "show":
            handleShowCommand();
            break;
          case "exit":
            exitProgram();
            break;
          default:
            view.displayError("Unknown command");
        }
      } catch (NoSuchElementException e) {
        view.displayError("Invalid command format from handle");
      } catch (EventConflictException e) {
        view.displayError(e.getMessage());
      }
    }
  }

  private void handleShowCommand() {

  }

  private void handleExportCommand() {
  }

  private void handlePrintCommand() {
  }

  private void handleEditCommand() {
  }

  private void handleCreateCommand(Scanner lineScanner) throws EventConflictException {
    boolean autoDecline = false;
    String eventName;

    if (!lineScanner.next().equals("event")) {
      view.displayError("Invalid command (not create event)");
      return;
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
        handleCreateSpannedEvent(autoDecline, eventName, lineScanner);
        break;
      case "on":
        handleCreateAllDayEvent(autoDecline, eventName, lineScanner);
        break;
      default:
        view.displayError("Invalid format");
    }
  }

  private void handleCreateAllDayEvent(boolean autoDecline, String eventName, Scanner lineScanner) {

  }

  private void handleCreateSpannedEvent(boolean autoDecline, String eventName,
      Scanner lineScanner) throws EventConflictException {
    LocalDateTime startTime;
    LocalDateTime endTime;
    try {
      startTime = LocalDateTime.parse(lineScanner.next(), formatter);
      if (!lineScanner.next().equals("to")) {
        view.displayError("Invalid format");
      }
      endTime = LocalDateTime.parse(lineScanner.next(), formatter);
    } catch (DateTimeParseException e) {
      view.displayError("Invalid time format");
      return;
    }

    if (!lineScanner.hasNext()) {
      model.createEvent(EventDTO.getBuilder()
          .setSubject(eventName)
          .setStartTime(startTime)
          .setEndTime(endTime)
          .build(), autoDecline);
      return;
    }

    if (!lineScanner.next().equals("repeats")) {
      view.displayError("Invalid format (not repeats)");
    }
    Set<DayOfWeek> repeatDays = parseRepeatDays(lineScanner.next());
    if (repeatDays.isEmpty()) {
      return;
    }

    switch (lineScanner.next()) {
      case "for":
        handleCreateSpannedNTimesEvent(autoDecline, eventName, startTime, endTime, repeatDays,
            lineScanner);
        break;
      case "until":
        handleCreateSpannedUntilEvent(autoDecline, eventName, startTime, endTime, repeatDays,
            lineScanner);
        break;
    }
  }

  private void handleCreateSpannedUntilEvent(boolean autoDecline, String eventName,
      LocalDateTime startTime, LocalDateTime endTime, Set<DayOfWeek> repeatDays,
      Scanner lineScanner) throws EventConflictException {
    LocalDateTime untilTime;
    try {
      untilTime = LocalDateTime.parse(lineScanner.next(), formatter);
    } catch (DateTimeParseException e) {
      view.displayError("Invalid untilTime format");
      return;
    }

    model.createRecurringEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .build(), autoDecline, repeatDays, untilTime);
  }

  private void handleCreateSpannedNTimesEvent(boolean autoDecline, String eventName,
      LocalDateTime startTime, LocalDateTime endTime, Set<DayOfWeek> repeatDays,
      Scanner lineScanner) throws EventConflictException {
    int times = Integer.parseInt(lineScanner.next());
    if (!lineScanner.next().equals("times")) {
      view.displayError("Invalid format (not <N> times)");
      return;
    }

    model.createRecurringEvent(EventDTO.getBuilder()
        .setSubject(eventName)
        .setStartTime(startTime)
        .setEndTime(endTime)
        .build(), autoDecline, repeatDays, times);
  }

  private Set<DayOfWeek> parseRepeatDays(String days) {
    Set<DayOfWeek> repeatDays = new HashSet<>();
    for (char day : days.toCharArray()) {
      try {
        repeatDays.add(DayOfWeek.valueOf(String.valueOf(day)));
      } catch (IllegalArgumentException e) {
        view.displayError("Invalid day of week: " + day);
        return new HashSet<>(); // Return empty set to indicate error
      }
    }
    return repeatDays;
  }

  @Override
  public void addView(IView view) {
    this.view = view;
    this.view.addController(this);
  }
}
