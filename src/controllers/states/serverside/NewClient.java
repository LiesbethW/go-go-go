package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class NewClient extends AbstractServerSideClientState {

	public NewClient(ClientHandler client) {
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
