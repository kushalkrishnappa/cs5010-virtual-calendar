package dto;

import java.time.LocalDateTime;


public interface EventDTO {

  String getSubject();

  LocalDateTime getStart();

  LocalDateTime getEnd();

  String getDescription();

  String getLocation();

  boolean isPublic();
}
