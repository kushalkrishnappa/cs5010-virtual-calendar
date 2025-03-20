package repository;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in the interval tree. The data related to the calendar events is
 * stored in the node.
 *
 * <p>It contains the start and end time of the event, the maximum end time of the events in the
 * subtree, and the left and right child nodes along with the height of the node. Upon collision,
 * the node will store all the collided events in a list.
 */
class Node {

  List<EventDTO> events;

  LocalDateTime startTime;

  LocalDateTime endTime;

  LocalDateTime maxEnd;

  Node left;

  Node right;

  Integer height;

  /**
   * Constructor will initialize new node and adds the event to the node. It will also set the start
   * and end time of the node to the start and end time of the event. The maximum end time is set to
   * the end time of the event (property required to identify the conflicts).
   *
   * <p>The default value of the left and right child nodes is null. The height of the node is set
   * to 1.
   *
   * @param event the event to be added to the node
   */
  Node(EventDTO event) {
    this.events = new ArrayList<>();
    this.events.add(event);
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.maxEnd = event.getEndTime();
    this.left = null;
    this.right = null;
    this.height = 1;
  }
}
