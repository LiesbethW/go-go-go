package game;

import controllers.ClientHandler;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public class HumanPlayer extends Player {
	private ClientHandler client;

	public HumanPlayer(ClientHandler client) {
		super(client.name());
		this.client = client;
	}
	
	@Override
	public void takeTurn(Game game) {
		// TODO Auto-generated method stub

	}
	
	public void send(Message message) {
		client.send(message);
	}
	
	public void digest(Message message) {
		try {
			client.digest(message);
		} catch (NotApplicableCommandException e) {
			System.err.printf("The server sent a non-applicable command %s "
					+ "to player with state %s%n", e.getMessage(), 
					client.currentState().toString());
		}		
	}
	
	public ClientHandler client() {
		return client;
	}

}
