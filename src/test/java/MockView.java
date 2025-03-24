import java.io.StringReader;
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
    String s = displayMessage.toString().split("\n")[2];
    return s.startsWith("calApp") ? s.substring(18) : s;
  }

  String getErrorMessage() {
    String s = displayErrorMessage.toString().split("\n")[0];
    return s.startsWith("calApp") ? s.substring(18) : s;
  }
}