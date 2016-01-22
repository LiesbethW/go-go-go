package game;

import controllers.ClientHandler;

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

}
