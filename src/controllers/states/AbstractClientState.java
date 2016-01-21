package controllers.states;

import java.util.HashMap;

import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public abstract class AbstractClientState implements State, network.protocol.Constants {
	
	public AbstractClientState() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public State accept(Message message) throws NotApplicableCommandException {
		if (!applicable(message.command())) {
			throw new NotApplicableCommandException();
		}
		return transitionMap().get(message.command());
	}

	@Override
	public boolean applicable(String command) {
		return transitionMap().containsKey(command);
	}
	
	@Override
	public boolean equals(Object object) {
		return this.getClass() == object.getClass();
	}
	
	protected abstract HashMap<String, State> transitionMap();

}
