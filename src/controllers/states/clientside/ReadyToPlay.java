package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class ReadyToPlay extends AbstractClientState {

	public ReadyToPlay(Client client) {
		super(client);
	}
	
	public void enter(Message message) { 
		client.setOpponent(null);
	}
	public void leave(Message message) { }

}
 