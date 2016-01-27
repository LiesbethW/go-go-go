package game;

import network.protocol.Message;

public class ComputerPlayer extends Player {
	private Strategy strategy;
	
	public ComputerPlayer(String name, Strategy strategy) {
		super(name);
		this.strategy = strategy;
	}
	
	@Override
	public void takeTurn(Game game) {
		// TODO Auto-generated method stub

	}
	
	public void send(Message message) {
		
	}

}
