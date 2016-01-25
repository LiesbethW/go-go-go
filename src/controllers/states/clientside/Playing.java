package controllers.states.clientside;

import java.util.HashMap;

import controllers.states.AbstractClientState;
import controllers.states.State;
import exceptions.InvalidArgumentException;
import game.Stone;
import network.protocol.Interpreter;
import network.protocol.Message;

public class Playing extends AbstractClientState {
	private static HashMap<String, State> transitionMap = new HashMap<String, State>();
	static {
		transitionMap.put(GAMEOVER, new ReadyToPlay());
		transitionMap.put(MOVE, new Playing());
		transitionMap.put(BOARD, new Playing());
		transitionMap.put(CHAT, new Playing());
		transitionMap.put(TWOPASS, new Playing());
		transitionMap.put(TERRITORYSCORING, new Playing());
		transitionMap.put(OPTIONS, new Playing());
		transitionMap.put(FAILURE, new Playing());	
	}

	public String opponent;
	public Stone color;
	public int boardSize;
	
	public Playing() {

	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
	
	public void enter(Message message) {
		opponent = message.args()[0];
		try {
			boardSize = Interpreter.integer(message.args()[1]);
			color = Interpreter.color(message.args()[2]);
		} catch (InvalidArgumentException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void leave(Message message) { }

}
