package service;

import java.io.IOException;

/**
 * This interface represents strategies to write data to a file.
 */
public interface IFileWriter {

  /**
   * Write string to a file with provided filename.
   *
   * @param fileName the file name
   * @param data     the data to write to the file
   * @return the absolute path of the file written to
   * @throws IOException if any error was encountered while writing the data
   */
  String write(String fileName, String data) throws IOException;

}
