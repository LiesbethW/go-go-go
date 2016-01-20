package game;

import controllers.ServerSideClientController;

public class HumanPlayer extends Player {
	private ServerSideClientController client;

	public HumanPlayer(ServerSideClientController client) {
		super(client.name());
		this.client = client;
	}
	
	@Override
	public void takeTurn(Game game) {
		// TODO Auto-generated method stub

	}

}
