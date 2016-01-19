package network.protocol;

import exceptions.GoException;
import exceptions.UnknownCommandException;
import network.ClientCommunicator;

public class ServerSideInterpreter extends Interpreter {
	private ClientCommunicator client;

	/**
	 * Create a new ServerSideInterpreter for the 
	 * given ClientCommunicator.
	 * @param client
	 */
	public ServerSideInterpreter(ClientCommunicator client) {
		super();
		this.client = client;
	}

	@Override
	public Message digest(String message) {
		parse(message);
		try {
			commandPartOfProtocol(command);
			return new Message(command, args);
		} catch (GoException e) {
			client.handleException(e);
			return null;
		}
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
