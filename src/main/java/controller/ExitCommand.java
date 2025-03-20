package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.Scanner;

public class ExitCommand extends Command {

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    return this;
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    controllerUtility.exitProgram();
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    return;
  }
}
