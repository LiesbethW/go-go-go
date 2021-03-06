package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;
import network.protocol.Presenter;

public class WaitForChallengeResponse extends AbstractServerSideClientState {
	
	public WaitForChallengeResponse(ClientHandler client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) {
		client.setOpponent(message.args()[0]);
		client.send(Presenter.youveChallenged(message.args()[0]));
	}
	
	public void leave(Message message) { 
		client.send(message);
		if (message.command().startsWith(Presenter.cancelled().toString())) {
			try {
				client.getOpponent().digest(message);
			} catch (NotApplicableCommandException e) {
				System.err.println(e.getMessage());
			}
		}
	}

}
