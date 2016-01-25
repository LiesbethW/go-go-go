package controllers;

import exceptions.GoException;
import network.protocol.Message;

public interface Command {
	public void runCommand(Message message) throws GoException;
}
