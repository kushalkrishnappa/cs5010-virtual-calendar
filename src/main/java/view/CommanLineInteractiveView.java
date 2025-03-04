package view;

import controller.IController;

public class CommanLineInteractiveView implements IView {

  @Override
  public void displayOutput(String output) {
    // TODO: Implement this method
    System.out.print(output + "\n>");
  }

  @Override
  public void displayError(String error) {
    // TODO: Implement this method
    System.err.print(error + "\n>");
  }

  @Override
  public void addController(IController controller) {
    // TODO: Implement this method
  }
}
