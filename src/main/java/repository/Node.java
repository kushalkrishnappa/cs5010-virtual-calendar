package repository;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Node {

  List<EventDTO> events;
  LocalDateTime startTime;
  LocalDateTime endTime;
  LocalDateTime maxEnd;
  Node left, right;

  Node(EventDTO event) {
    this.events = new ArrayList<>();
    this.events.add(event);
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.maxEnd = event.getEndTime();
    this.left = this.right = null;
  }
}