package controllers.states.clientside;

import controllers.ClientSideClientController;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class ReadyToPlay extends AbstractClientState {

	public ReadyToPlay(ClientSideClientController client) {
		super(client);
	}
	
	public void enter(Message message) { }
	public void leave(Message message) { }

}
 