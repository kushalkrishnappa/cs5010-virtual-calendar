package controller;

import exception.ParseCommandException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ShowStatusCommand extends Command {

  private LocalDateTime dateTime;
  private Boolean isBusy;

  ShowStatusCommand(CalendarController calendarController, Scanner commandScanner) {
    super(calendarController, commandScanner);
  }

  @Override
  void parseCommand() throws ParseCommandException {
    if (!commandScanner.next().equals("status")) {
      throw new ParseCommandException("Invalid command format: show status...");
    }
    if (!commandScanner.next().equals("on")) {
      throw new ParseCommandException("Invalid command format: show status on...");
    }

    dateTime = LocalDateTime.parse(commandScanner.next(), calendarController.dateTimeFormatter);

    command = () -> isBusy = calendarController.getModel().isBusy(dateTime);
  }

  @Override
  void promptResult() {
    if (isBusy) {
      calendarController.promptOutput("Busy at " + dateTime);
    }
    else {
      calendarController.promptOutput("Available at " + dateTime);
    }
  }
}
