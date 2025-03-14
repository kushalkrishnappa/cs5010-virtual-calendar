package controller;

/**
 * ControllerMode enum represents the mode of the controller.
 */
public enum ControllerMode {
  INTERACTIVE,
  HEADLESS;

  /**
   * Get the ControllerMode from the argument.
   *
   * @param arg The argument to be parsed.
   * @return The ControllerMode.
   * @throws IllegalArgumentException if the argument is invalid.
   */
  public static ControllerMode getControllerMode(String arg) throws IllegalArgumentException {
    return ControllerMode.valueOf(arg.toUpperCase());
  }
}
