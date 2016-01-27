package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;
import network.protocol.Presenter;

public class Challenged extends AbstractServerSideClientState {

	public Challenged(ClientHandler client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) { 
		client.setOpponent(message.args()[0]);
		client.send(message);
	}
	
	public void leave(Message message) { 
		if (message.command().equals(Presenter.challengeAccepted().toString()) || message.command().equals(Presenter.challengeDenied().toString())) {
			try {
				client.getOpponent().digest(message);
			} catch (NotApplicableCommandException e) {
				System.err.println(e.getMessage());
			}
		} else if (message.command().equals(Presenter.cancelled().toString())) {
			client.send(message);
		}
	}

}
