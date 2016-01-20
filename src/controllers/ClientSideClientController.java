package controllers;

import controllers.states.State;
import controllers.states.clientside.NewClient;
import exceptions.NotApplicableCommandException;
import network.Client;
import network.protocol.Message;

public class ClientSideClientController implements FSM {
	private Client client;
	private State currentState;
	
	public ClientSideClientController(Client client) {
		this.client = client;
		currentState = new NewClient();
	}

	@Override
	public void digest(Message message) throws NotApplicableCommandException {
		currentState = currentState.accept(message.command());
	}

//	@Override
//	public void dispatch(Message message) throws NotApplicableCommandException {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public State currentState() {
		return currentState;
	}

}
