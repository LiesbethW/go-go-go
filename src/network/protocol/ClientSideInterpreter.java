package network.protocol;

import exceptions.GoException;
import exceptions.UnknownCommandException;
import network.Client;

public class ClientSideInterpreter extends Interpreter {
	private Client client;
	
	public ClientSideInterpreter(Client client) {
		this.client = client;
	}

	@Override
	public Message digest(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Boolean commandPartOfProtocol(String command) throws UnknownCommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Message exceptionMessage(GoException e) {
		// TODO Auto-generated method stub
		return null;
	}

}
