package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This strategy writes the string data to the file and returns the absolute path of the file
 * written.
 *
 * <p>This implementation uses truncate methodology (a file is created if not present, or the
 * existing file is truncated and overwritten)
 */
public class StandardFileWriter implements IFileWriter {

  @Override
  public String write(String fileName, String data) throws IOException {
    File file = new File(fileName);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(data);
    }
    return file.getAbsolutePath();
  }
}
