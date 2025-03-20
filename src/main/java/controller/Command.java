package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.util.Scanner;

/**
 * Command class is an abstract class that represents a command to be executed by the controller.
 */
abstract class Command {

  /**
   * Parses the command from the Scanner object.
   *
   * @param commandScanner Scanner object that contains the command to be parsed.
   * @throws ParseCommandException if the command is invalid.
   */
  abstract Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException;

  /**
   * Executes the command on the model.
   *
   * @param controllerUtility the controller utility
   * @throws CalendarExportException if there is an error exporting the calendar.
   * @throws EventConflictException  if there is a conflict with the event.
   */
  abstract void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException;

  /**
   * Prompts the result of the command.
   *
   * @param controllerUtility ControllerUtility object that will be used to prompt the result of the
   *                          command.
   */
  abstract void promptResult(ControllerUtility controllerUtility);

}
