package view;

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
  void displayMessage(String output);

  /**
   * Display the error message to the user.
   *
   * @param error the error message to be displayed
   */
  void displayError(String error);

  /**
   * Return the input stream used by this view.
   *
   * @return the readable which the user is using
   */
  Readable getInputStream();
}
