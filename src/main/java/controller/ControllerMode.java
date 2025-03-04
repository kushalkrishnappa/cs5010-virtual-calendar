package controller;

public enum ControllerMode {
  INTERACTIVE,
  HEADLESS;

  public static ControllerMode getControllerMode(String arg) throws IllegalArgumentException {
    return ControllerMode.valueOf(arg.toUpperCase());
  }
}
