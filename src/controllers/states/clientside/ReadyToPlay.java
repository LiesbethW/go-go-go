package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class ReadyToPlay extends AbstractClientState {
	
	public ReadyToPlay(Client client) {
		super(client);
	}
	
	public void enter(Message message) { 
		client.setOpponent(null);
		client.send(Presenter.challenge());
	}
	public void leave(Message message) { 
		client.setAvailableOpponents(null);
	}

}
 