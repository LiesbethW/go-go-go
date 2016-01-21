package network.protocol;

import java.util.Scanner;

import exceptions.GoException;
import exceptions.UnknownCommandException;

public abstract class Interpreter implements Constants {
	protected Scanner input;
	protected String command;
	protected String[] args;
	
	public Interpreter() {
		
	}
	
	/**
	 * Read the incoming message, check if it
	 * is part of the protocol, applicable in
	 * the current state, and execute it if 
	 * that is so.
	 * @param message
	 */
	public abstract Message digest(String message) throws UnknownCommandException;
	
	/**
	 * Separate the message into command and
	 * arguments.
	 * @param message
	 */
	public void parse(String message) {
		reset();
		String[] words = message.split(DELIMITER);
		command = words[0];
		args = new String[words.length - 1];
		System.arraycopy(words, 1, args, 0, words.length - 1);
	}
	
	/**
	 * Check if the read command is a part of the
	 * protocol.
	 * @param command
	 * @throws UnknownCommandException
	 */
	abstract Boolean commandPartOfProtocol(String command) throws UnknownCommandException;

	/**
	 * Construct an appropriate message from
	 * this exception.
	 * @param GoException
	 */
	public abstract Message exceptionMessage(GoException e);
	
	/**
	 * Reset the input scanner and parsed command
	 * and args back to null.
	 */
	private void reset() {
		input = null;
		command = null;
		args = new String[]{};
	}

}
