package controllers.states;

import java.util.HashMap;

public class WaitingForOpponent extends AbstractState {
	private static HashMap<String, State> transitionMap = new HashMap<String, State>();
	static {
		transitionMap.put(GAMESTART, new Playing());
		transitionMap.put(CHAT, new WaitingForOpponent());
		transitionMap.put(OPTIONS, new WaitingForOpponent());
		transitionMap.put(GETOPTIONS, new WaitingForOpponent());
		transitionMap.put(QUIT, new WaitingForOpponent());
	}
	
	public WaitingForOpponent() {

	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
}
