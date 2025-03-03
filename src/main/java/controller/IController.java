package controller;

import view.IView;

/**
 * IController interface defines the methods for controller of mvc architecture for the
 * CalendarApp application.
 *
 */
public interface IController {

  /**
   * Add the view to the controller.
   *
   * @param view the view to be added
   */
  void addView(IView view);

  /**
   * Exit the program.
   *
   */
  void exitProgram();

  /**
   * Start the program.
   */
  void startProgram();
}
