package controllers.states;

import java.util.HashMap;

public class ReadyToPlay extends AbstractState {
	private static HashMap<String, State> transitionMap = new HashMap<String, State>();
	static {
		transitionMap.put(WAITFOROPPONENT, new WaitingForOpponent());
		transitionMap.put(GAMESTART, new Playing());
		transitionMap.put(YOURECHALLENGED, new Challenged());
		transitionMap.put(YOUVECHALLENGED, new ReadyToPlay());
		transitionMap.put(CHAT, new ReadyToPlay());
		transitionMap.put(OPTIONS, new ReadyToPlay());
		transitionMap.put(GETOPTIONS, new ReadyToPlay());
		transitionMap.put(QUIT, new ReadyToPlay());	
	}
	
	public ReadyToPlay() {
	
	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}

}
 