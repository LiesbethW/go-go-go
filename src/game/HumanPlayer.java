package game;

import network.ClientHandler;

public class HumanPlayer extends Player {
	private ClientHandler client;

	public HumanPlayer(String name, ClientHandler client) {
		super(name);
		this.client = client;
	}
	
	@Override
	public void takeTurn(Game game) {
		// TODO Auto-generated method stub

	}

}
