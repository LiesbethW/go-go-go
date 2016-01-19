package network.protocol;

import exceptions.ArgumentsMissingException;
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
			checkFormat(command, args);
			return new Message(command, args);
		} catch (GoException e) {
			client.handleException(e);
			return null;
		}
	}

	@Override
	public Boolean commandPartOfProtocol(String command) throws UnknownCommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkFormat(String command, String[] args) throws ArgumentsMissingException {
		// TODO Auto-generated method stub
	}

	@Override
	public Message exceptionMessage(GoException e) {
		return new Message(FAILURE, e.toString().replaceAll("[exceptions.]", "").replaceAll("Exception", ""));
	}

}
