package controller.command;

import exception.EventConflictException;

public interface Command {
  void execute() throws EventConflictException;
}
