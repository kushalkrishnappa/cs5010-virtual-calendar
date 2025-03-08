package repository;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntervalTree {

  private static class Node {

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

  private Node root;

  public Boolean insert(EventDTO event) {
    root = insert(root, event);
    return true;
  }

  private Node insert(Node node, EventDTO event) {
    if (node == null) {
      return new Node(event);
    }

    if (event.getStartTime().isEqual(node.startTime)) {
      node.events.add(event);
      node.endTime = event.getEndTime().isAfter(node.endTime) ? event.getEndTime() : node.endTime;
      node.maxEnd = event.getEndTime().isAfter(node.maxEnd) ? event.getEndTime() : node.maxEnd;
      return node;
    }

    if (event.getStartTime().isBefore(node.startTime)) {
      node.left = insert(node.left, event);
      node.maxEnd = node.left.maxEnd.isAfter(node.maxEnd) ? node.left.maxEnd : node.maxEnd;
    } else {
      node.right = insert(node.right, event);
      node.maxEnd = node.right.maxEnd.isAfter(node.maxEnd) ? node.right.maxEnd : node.maxEnd;
    }
    return node;
  }

  public List<EventDTO> searchOverlapping(LocalDateTime startTime, LocalDateTime endTime) {
    List<EventDTO> result = new ArrayList<>();
    searchOverlapping(root, startTime, endTime, result);
    return result;
  }

  private void searchOverlapping(Node node, LocalDateTime startTime, LocalDateTime endTime,
      List<EventDTO> result) {
    if (node == null) {
      return;
    }

    // Compare the node itself for overlapping
    if (node.startTime.isBefore(endTime) && node.endTime.isAfter(startTime)
        || node.startTime.isEqual(startTime)) {
      node.events.stream().filter(event ->
          (event.getStartTime().isBefore(endTime)
              && event.getEndTime().isAfter(startTime)
              || node.startTime.isEqual(startTime))
      ).forEach(result::add);
    }

    // If the left child is not null and the maxEnd of the left child is after the start time
    if (node.left != null && node.left.maxEnd.isBefore(startTime)) {
      searchOverlapping(node.right, startTime, endTime, result);
    } else {
      searchOverlapping(node.left, startTime, endTime, result);
    }
  }

  public List<EventDTO> searchOverlappingPoint(LocalDateTime dateTime) {
    List<EventDTO> result = new ArrayList<>();
    searchOverlappingPoint(root, dateTime, result);
    return result;
  }

  private void searchOverlappingPoint(Node node, LocalDateTime dateTime, List<EventDTO> result) {
    if (node == null) {
      return;
    }

    // If the maxEnd of the node is before the dateTime, return
    if (node.maxEnd.isBefore(dateTime)) {
      return;
    }

    if (node.startTime.isAfter(dateTime)) {
      searchOverlappingPoint(node.left, dateTime, result);
    } else {
      node.events.stream().filter(event ->
              (event.getStartTime().isBefore(dateTime))
                  && event.getEndTime().isAfter(dateTime)
                  || event.getStartTime().isEqual(dateTime)
                  || event.getEndTime().isEqual(dateTime))
          .forEach(result::add);
      searchOverlappingPoint(node.right, dateTime, result);
    }
  }

  public List<EventDTO> findByName(String subject) {
    List<EventDTO> result = new ArrayList<>();
    findByName(root, subject, result);
    return result;
  }

  private void findByName(Node node, String subject, List<EventDTO> result) {
    if (node == null) {
      return;
    }
    findByName(node.left, subject, result);
    node.events.stream().filter(event -> event.getSubject().equals(subject)).forEach(result::add);
    findByName(node.right, subject, result);
  }

  public EventDTO findEvent(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    return findEvent(root, subject, startTime, endTime);
  }

  private EventDTO findEvent(Node node, String subject, LocalDateTime startTime,
      LocalDateTime endTime) {
    if (node == null) {
      return null;
    }

    if (node.startTime.isEqual(startTime)) {
      for (EventDTO event : node.events) {
        if (event.getSubject().equals(subject) && event.getEndTime().isEqual(endTime)) {
          return event;
        }
      }
    }

    if (node.startTime.isBefore(startTime)) {
      return findEvent(node.right, subject, startTime, endTime);
    } else {
      return findEvent(node.left, subject, startTime, endTime);
    }
  }

  public List<EventDTO> getAllEvents() {
    return getAllEvents(root);
  }

  private List<EventDTO> getAllEvents(Node node) {
    List<EventDTO> result = new ArrayList<>();
    if (node == null) {
      return result;
    }
    result.addAll(getAllEvents(node.left));
    result.addAll(node.events);
    result.addAll(getAllEvents(node.right));
    return result;
  }

  public Boolean delete(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    boolean isDeleted = false;
    root = delete(root, subject, startTime, endTime, isDeleted);
    return isDeleted;
  }

  private Node delete(Node node, String subject, LocalDateTime startTime, LocalDateTime endTime,
      boolean isDeleted) {
    if (node == null) {
      return null;
    }

    if (node.startTime.isEqual(startTime)) {
      for (EventDTO event : node.events) {
        if (event.getSubject().equals(subject) && event.getEndTime().isEqual(endTime)) {
          node.events.remove(event);
          isDeleted = true;
          break;
        }
      }

      // Delete the node if it has no events
      if (node.events.isEmpty()) {
        // check if the node has no children
        if (node.left == null && node.right == null) {
          return null;
        }

        // check if the node has only one child
        if (node.left == null) {
          return node.right;
        } else if (node.right == null) {
          return node.left;
        }

        // check if the node has two children
        // find the inorder child
        Node successor = findSuccessor(node.right);
        // copy the successor's data to the node
        node.startTime = successor.startTime;
        node.endTime = successor.endTime;
        node.events.addAll(successor.events);
        // delete the successor
        node.right = deleteSuccessor(node.right);
        // done deleting

      }
      reComputeMaxEnd(node);

    }
    else if (node.startTime.isBefore(startTime)) {
      return delete(node.right, subject, startTime, endTime, isDeleted);
    } else {
      return delete(node.left, subject, startTime, endTime, isDeleted);
    }
    return node;
  }

  private static void reComputeMaxEnd(Node node) {
    // update the maxEnd
    node.maxEnd = node.events.stream().map(EventDTO::getEndTime)
        .max(LocalDateTime::compareTo).orElse(null);

    if(!Objects.isNull(node.maxEnd)) {
      if (node.left != null && node.maxEnd.isBefore(node.left.maxEnd)){
        node.maxEnd = node.left.maxEnd;
      }
      if (node.right != null && node.maxEnd.isBefore(node.right.maxEnd)) {
        node.maxEnd = node.right.maxEnd;
      }
    }
  }

  private Node deleteSuccessor(Node node) {
    if (node == null) {
      return null;
    }

    if (node.left == null) {
      return node.right;
    } else {
      node.left = deleteSuccessor(node.left);
      reComputeMaxEnd(node);
      return node;
    }


  }

  private Node findSuccessor(Node node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

}
