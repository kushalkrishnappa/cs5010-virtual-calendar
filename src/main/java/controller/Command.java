package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.Scanner;
import model.IModel;

abstract class Command {
  abstract void parseCommand(Scanner commandScanner) throws ParseCommandException;
  abstract void executeCommand(IModel model) throws CalendarExportException, EventConflictException;
  abstract void promptResult(ControllerUtility controllerUtility);
}
