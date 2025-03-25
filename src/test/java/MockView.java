import java.io.StringReader;
import java.util.Arrays;
import view.IView;

/**
 * MockView class implements IView and is used for testing purposes.
 */
class MockView implements IView {

  Readable inputStream;
  StringBuilder displayMessage;
  StringBuilder displayErrorMessage;

  MockView(String inputStream) {
    this.inputStream = new StringReader(
        "create calendar --name default --timezone Asia/Kolkata\n"
        + "use calendar --name default\n"
        + inputStream);
    displayMessage = new StringBuilder();
    displayErrorMessage = new StringBuilder();
  }

  @Override
  public void displayMessage(String output) {
    displayMessage.append(output);
  }

  @Override
  public void displayError(String error) {
    displayErrorMessage.append(error);
  }

  @Override
  public Readable getInputStream() {
    return inputStream;
  }

  String getDisplayMessage() {
    String[] split = displayMessage.toString().split("\n");
    String s = String.join("\n",Arrays.copyOfRange(split, 2, split.length-1));
    return s.startsWith("calApp") ? s.substring(18) : s;
  }

  String getErrorMessage() {
    String s = displayErrorMessage.toString().split("\n")[0];
    return s.startsWith("calApp") ? s.substring(18) : s;
  }
}