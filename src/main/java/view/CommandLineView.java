package view;

import exception.DisplayException;
import java.io.IOException;
import java.util.Objects;

/**
 * CommandLineView class implements IView and displays messages and errors to the command line.
 */
public class CommandLineView implements IView {

  private final Readable inputStream;
  private final Appendable outputStream;

  /**
   * Constructs a CommandLineView object with the given input and output streams.
   *
   * @param inputStream The input stream to read from
   * @param outputStream The output stream to write to
   */
  public CommandLineView(Readable inputStream, Appendable outputStream) {
    Objects.requireNonNull(inputStream, "InputStream cannot be null");
    Objects.requireNonNull(outputStream, "OutputStream cannot be null");
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  @Override
  public void displayMessage(String output) {
    try {
      outputStream.append(output);
    } catch (IOException e) {
      throw new DisplayException("Error displaying message: " + output);
    }
  }

  @Override
  public void displayError(String error) {
    try {
      outputStream.append(error);
    } catch (IOException e) {
      throw new DisplayException("Error displaying error: " + error);
    }
  }

  @Override
  public Readable getInputStream() {
    return this.inputStream;
  }
}
