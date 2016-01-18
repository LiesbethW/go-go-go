package network.protocol;

import exceptions.ArgumentsMissingException;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import exceptions.UnknownCommandException;
import network.ClientHandler;

public class ClientSpeakInterpreter extends Interpreter {
	private ClientHandler client;
	
	public ClientSpeakInterpreter(ClientHandler client) {
		super();
		this.client = client;
	}

	@Override
	void commandPartOfProtocol(String command) throws UnknownCommandException {
		// TODO Auto-generated method stub

	}

	@Override
	void commandApplicableForState(String command) throws NotApplicableCommandException {

	}

	@Override
	void execute(String command, String[] args) throws ArgumentsMissingException {
		// TODO Auto-generated method stub

	}

	@Override
	void handleException(GoException e) {
		// TODO Auto-generated method stub

	}

}
