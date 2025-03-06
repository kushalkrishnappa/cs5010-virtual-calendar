package controller;

import exception.ParseCommandException;
import java.util.Scanner;

class ExportCalendarCommand extends Command {

  private String filePath;

  public ExportCalendarCommand(CalendarController calendarController, Scanner commandScanner) {
    super(calendarController, commandScanner);
  }

  @Override
  void parseCommand() throws ParseCommandException {
    if (!commandScanner.next().equals("cal")) {
      throw new ParseCommandException("Invalid command format: export cal...");
    }

    String filename = commandScanner.next();
    if (!filename.endsWith(".csv")) {
      if (filename.contains(".")) {
        throw new ParseCommandException(
            "Filename must end with .csv or specified without extension. Found: " +
                filename.substring(filename.lastIndexOf(".")));
      }
      filename = filename + ".csv";
    }

    String finalFilename = filename;
    command = () -> filePath = calendarController.getModel().exportToCSV(finalFilename);
  }

  @Override
  void promptResult() {
    calendarController.promptOutput("Calendar exported to file:\n" + filePath + "\n");
  }
}
