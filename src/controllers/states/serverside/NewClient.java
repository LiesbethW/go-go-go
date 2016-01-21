package controllers.states.serverside;

import controllers.ServerSideClientController;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class NewClient extends AbstractServerSideClientState {

	public NewClient(ServerSideClientController client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) { 
		enter();
	}
	
	public void leave(Message message) { 
		leave();
	}	
	
	public void leave() { 
		client.send(Presenter.newPlayerAccepted());
	}

}
