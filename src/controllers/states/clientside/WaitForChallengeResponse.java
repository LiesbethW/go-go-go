package controllers.states.clientside;

import controllers.ClientSideClientController;
import controllers.states.AbstractClientState;
import network.protocol.Message;

public class WaitForChallengeResponse extends AbstractClientState {
	
	public WaitForChallengeResponse(ClientSideClientController client) {
		super(client);
	}
	
	public void enter(Message message) { }
	public void leave(Message message) { }

}
