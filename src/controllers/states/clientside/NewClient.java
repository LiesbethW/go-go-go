package controllers.states.clientside;

import controllers.Client;
import controllers.states.AbstractClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class NewClient extends AbstractClientState {
	
	public NewClient(Client client) {
		super(client);
	}
	
	public void enter(Message message) { }
	public void leave(Message message) {
		client.send(Presenter.getExtensions());
		client.send(Presenter.extensions(Client.SUPPORTED_EXTENSIONS));
	}

}
