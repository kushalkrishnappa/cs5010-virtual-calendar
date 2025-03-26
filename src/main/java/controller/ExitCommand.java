package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.Scanner;

/**
 * This class represents an implementation of abstract Command class to exit the program.
 */
public class ExitCommand extends Command {

  /**
   * This method is not used in this class as ExitCommand does not require any parsing.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   */
  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    return this;
  }

  /**
   * This method executes the exit command by calling the exitProgram method of the
   * ControllerUtility object. It does not require any parameters.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar.
   * @throws EventConflictException  if there is a conflict with the event.
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    controllerUtility.exitProgram();
  }

  /**
   * This method is not used in this class as ExitCommand does not require any result prompting.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    return;
  }
}
