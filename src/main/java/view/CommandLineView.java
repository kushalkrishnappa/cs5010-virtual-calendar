package view;

public class CommandLineView implements IView {

  private final Readable inputStream;

  public CommandLineView(Readable inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void displayMessage(String output) {
    System.out.print(output);
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
