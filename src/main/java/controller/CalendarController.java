package controller;

import dto.EventDTO;
import exception.EventConflictException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import model.DayOfWeek;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  private final ControllerMode mode;
  private final IView view;
  private final IModel model;
  private final DateTimeFormatter formatter;

  public CalendarController(IModel model, ControllerMode mode, IView view) {
    this.model = model;
    this.view = view;
    this.mode = mode;
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  }

  @Override
  public void run() {
    Objects.requireNonNull(mode, "mode cannot be null");
    Objects.requireNonNull(view, "view cannot be null");
    Objects.requireNonNull(model, "model cannot be null");

    Scanner scanner = new Scanner(view.getInputStream());
    promptUserInput();
    while (scanner.hasNextLine()) {
      Scanner lineScanner = new Scanner(scanner.nextLine());
      if (!lineScanner.hasNext()) {
        promptUserInput();
        continue;
      }
      executeCommand(lineScanner);
      promptUserInput();
    }
  }

  private void promptUserInput() {
    if (mode == ControllerMode.INTERACTIVE) {
      view.displayMessage("calApp> ");
    }
  }

  private void promptError(String message) {
    view.displayError(message);
    if (mode == ControllerMode.HEADLESS) {
      exitProgram();
    }
  }

  private void exitProgram() {
    System.exit(0);
  }

  private void executeCommand(Scanner lineScanner) {
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
          promptError("Unknown command");
      }
    } catch (NoSuchElementException e) {
      promptError("Invalid command format from handle");
    } catch (EventConflictException e) {
      promptError(e.getMessage());
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
      promptError("Invalid command (not create event)");
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
        promptError("Invalid format");
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
        promptError("Invalid format");
      }
      endTime = LocalDateTime.parse(lineScanner.next(), formatter);
    } catch (DateTimeParseException e) {
      promptError("Invalid time format");
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
      promptError("Invalid format (not repeats)");
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
      promptError("Invalid untilTime format");
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
      promptError("Invalid format (not <N> times)");
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
        promptError("Invalid day of week: " + day);
        return new HashSet<>(); // Return empty set to indicate error
      }
    }
    return repeatDays;
  }

}
