package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class WaitForChallengeResponse extends AbstractClientState {
	
	public WaitForChallengeResponse(Client client) {
		super(client);
	}
	
	public void enter(Message message) { 
		client.setOpponent(message.args()[0]);
	}
	public void leave(Message message) { }

}
