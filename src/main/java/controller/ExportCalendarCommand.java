package controller;

import controller.CalendarController.ControllerUtility;
import exception.CalendarExportException;
import exception.EventConflictException;
import exception.ParseCommandException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import service.CSVCalendarExporter;
import service.ICalendarExporter;
import service.IFileWriter;
import service.StandardFileWriter;

/**
 * This class represents an implementation of the abstract Command class to export calendar events
 * to a CSV file. It parses the command, executes the export operation, and prompts the result.
 */
class ExportCalendarCommand extends Command {

  private String outputFilePath;

  private String filename;

  private final IFileWriter fileWriter;

  /**
   * The constructor initializes the output file path and the filename and creates a new instance of
   * StandardFileWriter.
   */
  ExportCalendarCommand() {
    outputFilePath = null;
    filename = null;
    fileWriter = new StandardFileWriter();
  }

  /**
   * This method starts parsing the export calendar command from the Scanner object. It checks if
   * the command contains `cal` keyword and the filename. If the filename does not end with `.csv`,
   * it appends `.csv` to the filename. If the filename contains a dot but does not end with `.csv`,
   * it throws a ParseCommandException.
   *
   * @param commandScanner a Scanner object that reads the command (File or console input)
   * @return this command object
   * @throws ParseCommandException if the command provided is invalid
   */
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

  /**
   * Execute the export command on the model. It uses the CSVCalendarExporter to get the CSV data
   * from the model and writes it to a file using the StandardFileWriter. If there is an error
   * during writing the file, it throws a CalendarExportException.
   *
   * @param controllerUtility the controller utility object
   * @throws CalendarExportException if there is an error on exporting the calendar
   * @throws EventConflictException if there is a conflict with the event
   */
  @Override
  void executeCommand(ControllerUtility controllerUtility)
      throws CalendarExportException, EventConflictException {
    // get csv string from the model by passing format strategy
    ICalendarExporter calendarExporter = new CSVCalendarExporter();
    String csvData = controllerUtility.getCurrentCalendar().model
        .exportEventsWithExporter(calendarExporter);

    // write the data to file using file strategy
    try {
      outputFilePath = fileWriter.write(filename, csvData);
    } catch (IOException e) {
      throw new CalendarExportException(
          "Could not write to file: " + filename);
    }
  }

  /**
   * Prompt the result of the export command with the output file path as message.
   *
   * @param controllerUtility the controller utility object
   */
  @Override
  void promptResult(ControllerUtility controllerUtility) {
    controllerUtility.promptOutput("Calendar exported to file:\n" + outputFilePath);
  }
}
