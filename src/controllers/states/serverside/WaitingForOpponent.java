package controllers.states.serverside;

import controllers.ServerSideClientController;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class WaitingForOpponent extends AbstractServerSideClientState {

	public WaitingForOpponent(ServerSideClientController client) {
		super(client);
	}

	public void enter(Message message) { 
		enter();
	}
	
	public void leave(Message message) { 
		leave();
	}

	public void enter() { 
		client.send(Presenter.waitForOpponent());
	}
	
	public void leave() { }

}
