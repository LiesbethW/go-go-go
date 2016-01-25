package controllers.states.clientside;

import java.util.HashMap;

import controllers.states.AbstractClientState;
import controllers.states.State;
import network.protocol.Message;

public class WaitForChallengeResponse extends AbstractClientState {
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
	
	public void enter(Message message) { }
	public void leave(Message message) { }

}
