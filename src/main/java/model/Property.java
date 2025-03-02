package model;

/**
 * Represents a property with a name and type. This class is used to ensure type safety when working
 * with event properties.
 *
 * @param <T> the type of the property
 */
class Property<T> {

  private final String name;
  private final Class<T> type;

  /**
   * Constructs a new Property with the specified name and type.
   *
   * @param name the name of the property
   * @param type the class object representing the type of the property
   */
  Property(String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }
}
