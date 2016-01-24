package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;

public class Playing extends AbstractServerSideClientState {

	public Playing(ClientHandler client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) {

	}
	
	public void leave(Message message) { 
		client.send(message); // Is GAMEOVER Message
		client.removeGameController();
	}
	

}
