package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;


public class Challenged extends AbstractServerSideClientState {

	public Challenged(ClientHandler client) {
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
