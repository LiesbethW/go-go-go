package controllers.states.clientside;

import java.util.HashMap;

import controllers.states.AbstractClientState;
import controllers.states.State;

public class NewClient extends AbstractClientState {
	private static HashMap<String, State> transitionMap = new HashMap<>();
	static 
	{
		transitionMap.put(FAILURE, new NewClient());
		transitionMap.put(NEWPLAYERACCEPTED, new ReadyToPlay());		
	}
	
	public NewClient() {

	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}

}
