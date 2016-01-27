package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class WaitingForOpponent extends AbstractServerSideClientState {

	public WaitingForOpponent(ClientHandler client) {
		super(client);
	}

	public void enter(Message message) { 
		client.send(Presenter.waitForOpponent());
	}
	
	public void leave(Message message) { 
		if (message.command().equals(Presenter.cancelled().toString())) {
			client.send(message);
		}
	}

}
