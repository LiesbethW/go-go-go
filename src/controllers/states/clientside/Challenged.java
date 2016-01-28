package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class Challenged extends AbstractClientState {

	public Challenged(Client client) {
		super(client);
	}

	public void enter(Message message) { 
		client.setOpponent(message.args()[0]);
	}
	
	public void leave(Message message) { 
		if (message.command().equals(Presenter.challengeDenied().toString()) 
				|| message.command().equals(Presenter.challengeAccepted().toString())) {
			client.send(message);
		}
	}

}
