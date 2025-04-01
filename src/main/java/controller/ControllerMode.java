package controller;

/**
 * This class is an ENUM representing the modes in which the controller can operate.
 */
public enum ControllerMode {
  INTERACTIVE,
  HEADLESS,
  GUI;

  /**
   * The method converts the command line argument to the corresponding ControllerMode. If the
   * argument is invalid, it throws an IllegalArgumentException.
   *
   * @param arg The argument to be parsed from the command line
   * @return the mode in which the controller is specified to run from the command line
   * @throws IllegalArgumentException if the argument is invalid.
   */
  public static ControllerMode getControllerMode(String arg) throws IllegalArgumentException {
    return ControllerMode.valueOf(arg.toUpperCase());
  }
}
