package network.protocol;

import exceptions.GoException;
import exceptions.UnknownCommandException;

public class ServerSideInterpreter extends Interpreter {

	/**
	 * Create a new ServerSideInterpreter for the 
	 * given ClientCommunicator.
	 * @param client
	 */
	public ServerSideInterpreter() {
		super();
	}

	@Override
	public Message digest(String message) throws UnknownCommandException {
		parse(message);
		commandPartOfProtocol(command);
		return new Message(command, args);
	}

	@Override
	public Boolean commandPartOfProtocol(String command) throws UnknownCommandException {
		Boolean partOfProtocol = CommandSet.contains(command);
		if (!partOfProtocol) {
			throw new UnknownCommandException(command);
		}
		return partOfProtocol;
	}

	@Override
	public Message exceptionMessage(GoException e) {
		return new Message(FAILURE, CommandSet.exceptionCommand(e));
	}

}
