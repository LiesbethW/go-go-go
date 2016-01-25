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

	}
	
	public void leave(Message message) { 
		client.setName(message.args()[0]);
		client.send(Presenter.newPlayerAccepted());
	}

}
