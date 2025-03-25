package repository;

import dto.EventDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the data structure IntervalTree. The events are stored in the nodes of the
 * tree. The tree is used to store events and perform operations like insert, search, and delete.
 * The events are stored in the nodes based on their start time.
 */
public class IntervalTree {

  private Node root;
  private Boolean isDeleted;

  /**
   * Constructor for IntervalTree.
   *
   * @param event The event to be inserted
   * @return true if the event is successfully inserted, false otherwise
   */
  public Boolean insert(EventDTO event) {
    if (event == null) {
      return false;
    }
    root = insert(root, event);
    return true;
  }

  /**
   * Recursive method to insert an event into the tree.
   *
   * @param node  The node to insert the event
   * @param event The event to be inserted
   * @return The node after inserting the event
   */
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

    // update the height of the node
    updateHeight(node);

    // balance the tree
    return balanceTree(node);
  }

  /**
   * Search for events that overlap with the given time range.
   *
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return A list of events that overlap with the given time range
   */
  public List<EventDTO> searchOverlapping(LocalDateTime startTime, LocalDateTime endTime) {
    List<EventDTO> result = new ArrayList<>();
    searchOverlapping(root, startTime, endTime, result);
    return result;
  }

  /**
   * Search for events that overlap with the given time range within a node.
   *
   * @param node      The node to search for overlapping events
   * @param startTime The start time
   * @param endTime   The end time
   * @param result    The list of events that overlap with the given time range
   */
  private void searchOverlapping(Node node, LocalDateTime startTime,
      LocalDateTime endTime, List<EventDTO> result) {
    if (node == null) {
      return;
    }
    // If the left child is not null and the maxEnd of the left child is after the start time
    if (node.left == null || node.left.maxEnd.isBefore(startTime)) {
      searchOverlappingWithinNode(node, startTime, endTime, result);
      searchOverlapping(node.right, startTime, endTime, result);
    } else {
      searchOverlapping(node.left, startTime, endTime, result);
      searchOverlappingWithinNode(node, startTime, endTime, result);
      searchOverlapping(node.right, startTime, endTime, result);
    }
  }

  /**
   * Search for events that overlap with the given time range within a node.
   *
   * @param node      The node to search for overlapping events
   * @param startTime The start time
   * @param endTime   The end time
   * @param result    The list of events that overlap with the given time range
   */
  private static void searchOverlappingWithinNode(Node node, LocalDateTime startTime,
      LocalDateTime endTime, List<EventDTO> result) {
    // Compare the node itself for overlapping
    if (node.startTime.isBefore(endTime) && node.endTime.isAfter(startTime)
        || node.startTime.isEqual(startTime)) {
      node.events.stream().filter(event ->
          (event.getStartTime().isBefore(endTime)
              && event.getEndTime().isAfter(startTime)
              || node.startTime.isEqual(startTime))
      ).forEach(result::add);
    }
  }

  /**
   * Search for events that overlap with the given time range.
   *
   * @param dateTime The date and time to get events for
   * @return a list of events at the given date and time
   */
  public List<EventDTO> searchOverlappingPoint(LocalDateTime dateTime) {
    List<EventDTO> result = new ArrayList<>();
    searchOverlappingPoint(root, dateTime, result);
    return result;
  }

  /**
   * Search for events that overlap with the given time range within a node.
   *
   * @param node     The node to search for overlapping events
   * @param dateTime The date and time to get events for
   * @param result   The list of events that overlap with the given time range
   */
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

  /**
   * Retrieves all events in the repository on given date.
   *
   * @param subject The name of the event
   * @return a list of events with the given name
   */
  public List<EventDTO> findByName(String subject) {
    List<EventDTO> result = new ArrayList<>();
    findByName(root, subject, result);
    return result;
  }

  /**
   * Search for events with the given name within a node.
   *
   * @param node    The node to search for events
   * @param subject The name of the event
   * @param result  The list of events with the given name
   */
  private void findByName(Node node, String subject, List<EventDTO> result) {
    if (node == null) {
      return;
    }
    findByName(node.left, subject, result);
    node.events.stream().filter(event -> event.getSubject().equals(subject)).forEach(result::add);
    findByName(node.right, subject, result);
  }

  /**
   * Retrieves an event in the repository based on the name, start time, and end time.
   *
   * @param subject   The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return an event if it exists, empty otherwise
   */
  public EventDTO findEvent(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    return findEvent(root, subject, startTime, endTime);
  }

  /**
   * Search for an event with the given name, start time, and end time within a node.
   *
   * @param node      The node to search for the event
   * @param subject   The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return an event if it exists, empty otherwise
   */
  private EventDTO findEvent(Node node, String subject, LocalDateTime startTime,
      LocalDateTime endTime) {
    if (node == null) {
      return null;
    }
    // check within the node
    if (node.startTime.isEqual(startTime)) {
      for (EventDTO event : node.events) {
        if (event.getSubject().equals(subject) && event.getEndTime().isEqual(endTime)) {
          return event;
        }
      }
    }
    // check the right child if the start time is before the node's start time
    if (node.startTime.isBefore(startTime)) {
      return findEvent(node.right, subject, startTime, endTime);
    } else {
      return findEvent(node.left, subject, startTime, endTime);
    }
  }

  /**
   * Get all events in the repository.
   *
   * @return A list of all events in the repository
   */
  public List<EventDTO> getAllEvents() {
    return getAllEvents(root);
  }

  /**
   * Get all events in the repository within a node.
   *
   * @param node The node to get all events from
   * @return A list of all events in the repository
   */
  private List<EventDTO> getAllEvents(Node node) {
    if (node == null) {
      return List.of();
    }
    List<EventDTO> result = new ArrayList<>();
    // Inorder traversal
    result.addAll(getAllEvents(node.left));
    result.addAll(node.events);
    result.addAll(getAllEvents(node.right));
    return result;
  }

  /**
   * Deletes an event from the repository based on the name, start time, and end time.
   *
   * @param subject   The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return true if the event is successfully deleted, false otherwise
   */
  public Boolean delete(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    isDeleted = false;
    root = delete(root, subject, startTime, endTime);
    return isDeleted;
  }

  /**
   * Recursive method to delete an event from the tree.
   *
   * @param node      The node to delete the event
   * @param subject   The name of the event
   * @param startTime The start time of the event
   * @param endTime   The end time of the event
   * @return The node after deleting the event
   */
  private Node delete(Node node, String subject, LocalDateTime startTime, LocalDateTime endTime) {
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
      updateMaxEnd(node);
    } else if (node.startTime.isBefore(startTime)) {
      node.right = delete(node.right, subject, startTime, endTime);
    } else {
      node.left = delete(node.left, subject, startTime, endTime);
    }

    // update the height of the node
    updateHeight(node);

    return balanceTree(node);
  }

  /**
   * Delete the successor of a node.
   *
   * @param node The node to delete the successor
   * @return The node after deleting the successor
   */
  private Node deleteSuccessor(Node node) {
    // check if the node has no children
    if (node == null) {
      return null;
    }
    // check if the node has only one child
    if (node.left == null) {
      return node.right;
    } else { // check if the node has two children
      node.left = deleteSuccessor(node.left);
      updateMaxEnd(node);
      return node;
    }
  }

  /**
   * Find the successor of a node.
   *
   * @param node The node to find the successor
   * @return The successor of the node
   */
  private Node findSuccessor(Node node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  /**
   * Update the height of the Node.
   *
   * <p>The height of a node is the maximum height of its left and right child plus one. If the
   * node is null, the height is set to 0. If the node is leaf node, the height is set to 1.
   *
   * @param node The node to update the height for
   */
  private void updateHeight(Node node) {
    if (node != null) {
      int leftHeight = Objects.isNull(node.left) ? 0 : node.left.height;
      int rightHeight = Objects.isNull(node.right) ? 0 : node.right.height;
      node.height = Math.max(leftHeight, rightHeight) + 1;
    }
  }

  /**
   * Balance factor is a tree invariant that is used to determine if the tree is balanced. If the
   * balance factor is greater than 1 or less than -1, the tree is unbalanced.
   *
   * @param node The node to get the balance factor for
   * @return The balance factor of the node
   */
  private int getBalanceFactor(Node node) {
    if (node != null) {
      int leftHeight = Objects.isNull(node.left) ? 0 : node.left.height;
      int rightHeight = Objects.isNull(node.right) ? 0 : node.right.height;
      return leftHeight - rightHeight;
    }
    return 0;
  }

  /**
   * Update the maxEnd of a node. The maxEnd is the maximum end time of all the events in the
   * subtree of the node.
   *
   * @param node The node to update the maxEnd for
   */
  private static void updateMaxEnd(Node node) {
    if (node != null) {
      // update the maxEnd from events within the node
      node.maxEnd = node.events.stream()
          .map(EventDTO::getEndTime)
          .max(LocalDateTime::compareTo)
          .orElse(null);

      // check if the left child is not null and the maxEnd of the left child is greater
      if (node.left != null) {
        if (node.maxEnd != null && node.left.maxEnd.isAfter(node.maxEnd)) {
          node.maxEnd = node.left.maxEnd;
        }
      }

      // check if the right child is not null and the maxEnd of the right child is greater
      if (node.right != null) {
        if (node.maxEnd != null && node.right.maxEnd.isAfter(node.maxEnd)) {
          node.maxEnd = node.right.maxEnd;
        }
      }
    }
  }

  /**
   * Rotate the tree to the right. This is used to balance the tree when the left subtree is taller
   * than the right subtree.
   *
   * @param node The node to rotate right
   * @return The new root of the tree after rotation
   */
  private Node rotateRight(Node node) {
    Node newRoot = node.left;
    Node tempRight = newRoot.right;

    // perform rotation
    newRoot.right = node;
    node.left = tempRight;

    // update heights
    updateHeight(node);
    updateHeight(newRoot);

    // update maxEnd
    updateMaxEnd(node);
    updateMaxEnd(newRoot);

    return newRoot;
  }

  /**
   * Rotate the tree to the left. This is used to balance the tree when the right subtree is taller
   * than the left subtree.
   *
   * @param node The node to rotate left
   * @return The new root of the tree after rotation
   */
  private Node rotateLeft(Node node) {
    Node newRoot = node.right;
    Node tempLeft = newRoot.left;

    // perform rotation
    newRoot.left = node;
    node.right = tempLeft;

    // update heights
    updateHeight(node);
    updateHeight(newRoot);

    // update maxEnd
    updateMaxEnd(node);
    updateMaxEnd(newRoot);

    return newRoot;
  }

  /**
   * Balance the tree if the balance factor is greater than 1 or less than -1.
   *
   * @param node the node on which the tree is to be balanced
   * @return the node on which the tree is balanced i.e, balance factor is between -1 and 1
   */
  private Node balanceTree(Node node) {
    if (node == null) {
      return null;
    }

    // get the balance factor of the root node
    int balanceFactor = getBalanceFactor(node);

    // check if the tree is left heavy
    if (balanceFactor > 1) {
      // left-right case
      if (getBalanceFactor(node.left) < 0) {
        node.left = rotateLeft(node.left);
      }
      // left-left case
      return rotateRight(node);
    }

    // check if the tree is right heavy
    if (balanceFactor < -1) {
      // right-left case
      if (getBalanceFactor(node.right) > 0) {
        node.right = rotateRight(node.right);
      }
      // right-right case
      return rotateLeft(node);
    }

    // the node is balanced
    return node;
  }
}
