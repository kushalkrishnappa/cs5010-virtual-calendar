package model;

import java.time.LocalDateTime;

/**
 * Defines static Property instances for various event properties. This class provides a centralized
 * location for accessing event property definitions. It also prevents instantiation, as it is meant
 * to be used as a static factory.
 */
public class EventProperties {

  public static final Property<String> SUBJECT = new Property<>("subject", String.class);
  public static final Property<LocalDateTime> START_TIME = new Property<>("startTime",
      LocalDateTime.class);
  public static final Property<LocalDateTime> END_TIME = new Property<>("endTime",
      LocalDateTime.class);
  public static final Property<String> DESCRIPTION = new Property<>("description",
      String.class);
  public static final Property<String> LOCATION = new Property<>("location", String.class);
  public static final Property<Boolean> IS_PUBLIC = new Property<>("isPublic", Boolean.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private EventProperties() {
  }
}
