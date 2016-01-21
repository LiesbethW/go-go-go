package controllers.states.serverside;

import controllers.ServerSideClientController;
import controllers.states.AbstractServerSideClientState;
import exceptions.InvalidArgumentException;
import game.Stone;
import network.protocol.Message;
import network.protocol.Presenter;
import network.protocol.Interpreter;

public class Playing extends AbstractServerSideClientState {
	public String opponent;
	public Stone color;
	public int boardSize;
	
	public Playing(ServerSideClientController client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) {
		opponent = message.args()[0];
		try {
			boardSize = Interpreter.integer(message.args()[1]);
			color = Interpreter.color(message.args()[2]);
		} catch (InvalidArgumentException e) {
			System.err.println("ABSOLUTELY FORBIDDEN: the server sends this message to itself!");
		}
		client.send(Presenter.gameStart(opponent, boardSize, color));
	}
	
	public void leave(Message message) { 
		leave();
	}
	
	public void leave() {
		opponent = null;
		color = null;
	}

}
