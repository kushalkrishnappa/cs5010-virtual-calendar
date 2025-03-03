package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import model.IModel;
import view.IView;

public class CalendarController implements IController {

  IView view;
  IModel model;
  Scanner scanner;

  public CalendarController(IModel model, InputStream in) {
    this.model = model;
    this.scanner = new Scanner(in);
  }

  public CalendarController(IModel model, String filePath) throws FileNotFoundException {
    this.model = model;
    this.scanner = new Scanner(new File(filePath));
  }

  @Override
  public void exitProgram() {
    scanner.close();
    System.exit(0);
  }

  @Override
  public void startProgram() {

  }

  @Override
  public void addView(IView view) {
    this.view = view;
    this.view.addController(this);
  }
}
