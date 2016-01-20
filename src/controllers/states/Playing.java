package controllers.states;

import java.util.HashMap;

public class Playing extends AbstractState {
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
}
