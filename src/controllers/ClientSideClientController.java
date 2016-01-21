package controllers;

import controllers.states.State;
import controllers.states.clientside.NewClient;
import exceptions.NotApplicableCommandException;
import network.Client;
import network.protocol.Message;
import userinterface.TUIView;
import userinterface.View;

public class ClientSideClientController implements FSM {
	private Client client;
	private State currentState;
	private View view;
	
	public ClientSideClientController(Client client) {
		this.client = client;
		currentState = new NewClient();
		view = new TUIView(System.out);
	}

	@Override
	public void digest(Message message) throws NotApplicableCommandException {
		currentState = currentState.accept(message);
	}

	@Override
	public State currentState() {
		return currentState;
	}

}
