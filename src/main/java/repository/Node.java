package repository;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Node is a class that represents a node in an Interval Tree.
 *
 * <p>It contains a list of events, a start time, an end time, a maximum end time, and references to
 * the left and right children.
 */
class Node {

  List<EventDTO> events;

  LocalDateTime startTime;

  LocalDateTime endTime;

  LocalDateTime maxEnd;

  Node left;

  Node right;

  /**
   * Constructor for Node. The node will be part of interval tree and will store the given event.
   *
   * @param event The event to be stored in the node
   */
  Node(EventDTO event) {
    this.events = new ArrayList<>();
    this.events.add(event);
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.maxEnd = event.getEndTime();
    this.left = this.right = null;
  }
}
