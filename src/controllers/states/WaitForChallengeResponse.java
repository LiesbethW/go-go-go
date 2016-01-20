package controllers.states;

import java.util.HashMap;

public class WaitForChallengeResponse extends AbstractState {
	private static HashMap<String, State> transitionMap = new HashMap<String, State>();
	static {
		transitionMap.put(GAMESTART, new Playing());
		transitionMap.put(CHALLENGEACCEPTED, new WaitForChallengeResponse());
		transitionMap.put(CHALLENGEDENIED, new ReadyToPlay());
		transitionMap.put(OPTIONS, new WaitForChallengeResponse());
		transitionMap.put(CHAT, new WaitForChallengeResponse());
		transitionMap.put(FAILURE, new WaitForChallengeResponse());	
		transitionMap.put(GETOPTIONS, new WaitForChallengeResponse());
		transitionMap.put(QUIT, new WaitForChallengeResponse());			
	}
	
	public WaitForChallengeResponse() {
	
	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}	
}
