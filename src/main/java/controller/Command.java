package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.InvalidTimeZoneException;
import exception.ParseCommandException;
import java.util.Scanner;

/**
 * This class represents a command abstraction that involves parsing a command from a command
 * scanner, executing the command on a model, and prompting the result of the command.
 */
abstract class Command {

  /**
   * Parses the command from the Scanner object.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input).
   * @throws ParseCommandException if the command provided is invalid.
   */
  abstract Command parseCommand(Scanner commandScanner)
      throws ParseCommandException, InvalidTimeZoneException;

  /**
   * Executes the command on the model.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar.
   * @throws EventConflictException  if there is a conflict with the event.
   */
  abstract void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException;

  /**
   * Prompts the result of the command.
   *
   * @param controllerUtility the controller utility object
   */
  abstract void promptResult(ControllerUtility controllerUtility);

}
