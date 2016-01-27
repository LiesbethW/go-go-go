package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class Challenged extends AbstractClientState {

	public Challenged(Client client) {
		super(client);
	}

	public void enter(Message message) { }
	public void leave(Message message) { }

}
