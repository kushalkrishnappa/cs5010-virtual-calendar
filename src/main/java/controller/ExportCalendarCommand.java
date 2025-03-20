package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ExportCalendarCommand class implements Command and execute the command to export the calendar to
 * a CSV file.
 */
class ExportCalendarCommand extends Command {

  private String outputFilePath;

  private String filename;

  /**
   * Constructor for ExportCalendarCommand.
   */
  ExportCalendarCommand() {
    outputFilePath = null;
    filename = null;
  }

  @Override
  Command parseCommand(Scanner commandScanner) throws ParseCommandException {
    try {
      if (!commandScanner.next().equals("cal")) {
        throw new ParseCommandException("Invalid command format: export cal ...");
      }

      filename = commandScanner.next();
      if (!filename.endsWith(".csv")) {
        if (filename.contains(".")) {
          throw new ParseCommandException(
              "Filename must end with .csv or specified without extension. Found: " +
                  filename.substring(filename.lastIndexOf(".")));
        }
        filename = filename + ".csv";
      }
    } catch (NoSuchElementException e) {
      throw new ParseCommandException("Invalid command format: export cal <filename(.csv)>");
    }
    return this;
  }

  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    outputFilePath = controllerUtility.getCurrentCalendar().model
        .exportToCSV(filename);
  }

  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Calendar exported to file:\n" + outputFilePath);
  }
}
