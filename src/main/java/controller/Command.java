package controller;

import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.Scanner;

abstract class Command {

  protected final CalendarController calendarController;
  protected final Scanner commandScanner;
  protected Runnable command;

  Command(CalendarController calendarController, Scanner commandScanner) {
    this.calendarController = calendarController;
    this.commandScanner = commandScanner;
  }

  abstract void parseCommand() throws ParseCommandException;

  void executeCommand() throws CalendarExportException, EventConflictException {
    command.run();
  }

  abstract void promptResult();
}
