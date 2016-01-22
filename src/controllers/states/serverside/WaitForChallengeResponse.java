package controllers.states.serverside;

import controllers.ClientHandler;
import controllers.states.AbstractServerSideClientState;
import network.protocol.Message;
import network.protocol.Presenter;

public class WaitForChallengeResponse extends AbstractServerSideClientState {
	private String challengedOpponent;
	
	public WaitForChallengeResponse(ClientHandler client) {
		super(client);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Message message) {
		challengedOpponent = message.args()[0];
		client.send(Presenter.youveChallenged(challengedOpponent));
	}
	
	public void leave(Message message) { 
		if (message.command() == CHALLENGEACCEPTED) {
			client.send(Presenter.challengeAccepted());
		} else if (message.command() == CHALLENGEDENIED) {
			client.send(Presenter.challengeDenied());
		}
		leave();
	}
	
	public void leave() {
		challengedOpponent = null;
	}


}
