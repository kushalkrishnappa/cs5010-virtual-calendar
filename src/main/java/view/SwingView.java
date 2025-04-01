package view;

import controller.CalendarFeatures;
import javax.swing.JFrame;

public class SwingView extends JFrame implements IGUIView {

  @Override
  public void setFeatures(CalendarFeatures features) {

  }

  @Override
  public void displayMessage(String output) {

  }

  @Override
  public void displayError(String error) {

  }

  @Override
  public Readable getInputStream() {
    throw new UnsupportedOperationException("Not supported in GUI mode.");
  }
}
