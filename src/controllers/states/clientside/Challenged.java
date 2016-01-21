package controllers.states.clientside;

import java.util.HashMap;

import controllers.states.AbstractClientState;
import controllers.states.State;

public class Challenged extends AbstractClientState {
	private static HashMap<String, State> transitionMap = new HashMap<String, State>();
	static {
		transitionMap.put(GAMESTART, new Playing());
		transitionMap.put(OPTIONS, new Challenged());
		transitionMap.put(CHAT, new Challenged());
		transitionMap.put(FAILURE, new Challenged());
		transitionMap.put(GETOPTIONS, new Challenged());
		transitionMap.put(QUIT, new Challenged());		
	}
	
	public Challenged() {

	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
	
	public void enter() { }
	public void leave() { }

}
