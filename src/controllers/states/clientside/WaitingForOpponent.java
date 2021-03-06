package controllers.states.clientside;

import java.util.HashMap;

import controllers.Client;
import controllers.states.AbstractClientState;
import controllers.states.State;
import network.protocol.Message;

public class WaitingForOpponent extends AbstractClientState {
	
	public WaitingForOpponent(Client client) {
		super(client);
	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}
	
	public void enter(Message message) { }
	public void leave(Message message) { }

}
