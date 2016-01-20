package controllers;

import controllers.states.State;
import controllers.states.serverside.Playing;
import exceptions.CorruptedAuthorException;
import exceptions.NotApplicableCommandException;
import network.ClientCommunicator;
import network.Server;
import network.protocol.Message;

public class ServerSideClientController implements FSM {
	private ClientCommunicator clientCommunicator;
	private State state;
	private Server server;
	private GameController gameController;
	private String name;
	
	public ServerSideClientController(ClientCommunicator clientCommunicator, Server server) {
		this.clientCommunicator = clientCommunicator;
		this.server = server;
	}

	public void digest(Message message) throws NotApplicableCommandException {
		if (!state.applicable(message.command())) {
			throw new NotApplicableCommandException();
		}
		message.setAuthor(clientCommunicator);
		if (state.equals(new Playing()) && gameController != null) {
			try {
				gameController.enqueue(message);
			} catch (CorruptedAuthorException e) {
				System.err.println(e.getMessage());
			}		
		} else {
			server.enqueue(message);
		}
	}
	
	public State currentState() {
		return state;
	}
	
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
}
