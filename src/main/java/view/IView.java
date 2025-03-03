package view;

import controller.IController;

/**
 * IView interface defines the methods for view of mvc architecture for the CalendarApp
 * application.
 */
public interface IView {

  /**
   * Display the output message to the user.
   *
   * @param output the output to be displayed
   */
  void displayOutput(String output);

  /**
   * Display the error message to the user.
   *
   * @param error the error message to be displayed
   */
  void displayError(String error);

  /**
   * Add the controller to the view.
   *
   * @param controller the controller to be added
   */
  void addController(IController controller);

}
