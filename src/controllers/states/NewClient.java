package controllers.states;

import java.util.HashMap;

public class NewClient extends AbstractState {
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
