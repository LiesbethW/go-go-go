package network.protocol;

import java.util.Scanner;

import exceptions.ArgumentsMissingException;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import exceptions.UnknownCommandException;

public abstract class Interpreter implements Constants {
	private Scanner input;
	private String command;
	private String[] args;
	
	public Interpreter() {
	
	}
	
	/**
	 * Read the incoming message, check if it
	 * is part of the protocol, applicable in
	 * the current state, and execute it if 
	 * that is so.
	 * @param message
	 */
	public void digest(String message) {
		parse(message);
		try {
			commandPartOfProtocol(command);
			commandApplicableForState(command);
			execute(command, args);
		} catch (GoException e) {
			handleException(e);
		}
		
	}
	
	/**
	 * Separate the message into command and
	 * arguments.
	 * @param message
	 */
	public void parse(String message) {
		reset();
		input = new Scanner(message);
		if (input.hasNext()) {
			command = input.next();
		} else {
			// Should this throw an exception?
			return;
		}
		int argIndex = 0;
		while (input.hasNext()) {
			args[argIndex] = input.next();
			argIndex++;
		}
	}
	
	/**
	 * Check if the read command is a part of the
	 * protocol.
	 * @param command
	 * @throws UnknownCommandException
	 */
	abstract void commandPartOfProtocol(String command) throws UnknownCommandException;
	
	/**
	 * Check if the command has meaning in the current
	 * situation (as represented by the ClientState).
	 * @param command
	 * @throws NotApplicableCommandException
	 */
	abstract void commandApplicableForState(String command) throws NotApplicableCommandException;
	
	/**
	 * Execute the command with the given arguments.
	 * @param command
	 * @param args
	 * @throws ArgumentsMissingException if more arguments
	 * were expected than given.
	 */
	abstract void execute(String command, String[] args) throws ArgumentsMissingException;
	
	/**
	 * Handle the exception: if applicable, send it
	 * as a failure message to the other party.
	 * @param e
	 */
	abstract void handleException(GoException e);
	
	/**
	 * Reset the input scanner and parsed command
	 * and args back to null.
	 */
	private void reset() {
		input = null;
		command = null;
		args = null;
	}

}
