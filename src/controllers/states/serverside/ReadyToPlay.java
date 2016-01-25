package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;

public class ReadyToPlay extends AbstractServerSideClientState {

	public ReadyToPlay(ClientHandler client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) { 
		client.removeOpponent();
	}
	
	public void leave(Message message) { 
		
	}

}
