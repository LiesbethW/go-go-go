package controllers.states.clientside;

import controllers.ClientSideClientController;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class Challenged extends AbstractClientState {

	public Challenged(ClientSideClientController client) {
		super(client);
	}

	public void enter(Message message) { }
	public void leave(Message message) { }

}
