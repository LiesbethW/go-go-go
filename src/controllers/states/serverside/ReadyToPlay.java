package controllers.states.serverside;

import controllers.ServerSideClientController;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;

public class ReadyToPlay extends AbstractServerSideClientState {

	public ReadyToPlay(ServerSideClientController client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) { 
		enter();
	}
	
	public void leave(Message message) { 
		leave();
	}

}
