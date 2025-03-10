package view;

import exception.DisplayException;
import java.io.IOException;
import java.util.Objects;

public class CommandLineView implements IView {

  private final Readable inputStream;
  private final Appendable outputStream;

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
    System.out.println(error);
  }

  @Override
  public Readable getInputStream() {
    return this.inputStream;
  }
}
