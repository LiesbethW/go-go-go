package controllers.states.clientside;

import java.util.HashMap;

import controllers.states.AbstractClientState;
import controllers.states.State;
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
	
	public Playing() {

	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
	
	public void enter(Message message) { }
	public void leave(Message message) { }

}
