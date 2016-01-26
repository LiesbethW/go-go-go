package controllers.states.clientside;

import java.util.HashMap;

import controllers.Client;
import controllers.states.AbstractClientState;
import controllers.states.State;
import exceptions.InvalidArgumentException;
import game.Stone;
import network.protocol.Interpreter;
import network.protocol.Message;

public class Playing extends AbstractClientState {
	public String opponent;
	public Stone color;
	public int boardSize;
	
	public Playing(Client client) {
		super(client);
	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
	
	public void enter(Message message) {
		opponent = message.args()[0];
		try {
			client.setBoardSize(Interpreter.integer(message.args()[1]));
			client.setColor(Interpreter.color(message.args()[2]));
		} catch (InvalidArgumentException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void leave(Message message) { }

}
