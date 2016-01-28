package controllers.states;

import java.util.HashMap;
import java.util.HashSet;

import controllers.Client;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public abstract class AbstractClientState implements State, network.protocol.Constants {
	protected HashMap<String, State> transitionMap;
	protected HashSet<String> applicableCommands;
	protected Client client;

	public AbstractClientState(Client client) {
		this.client = client;
		applicableCommands = new HashSet<String>();
		transitionMap = new HashMap<String, State>();
	}

	@Override
	public State accept(Message message) throws NotApplicableCommandException {
		if (!applicable(message.command())) {
			throw new NotApplicableCommandException(String.format("Cannot "
					+ "accept command %s in state %s", message.command(), this.toString()));
		}
		return transitionMap().get(message.command());
	}

	@Override
	public boolean applicable(String command) {
		return transitionMap().containsKey(command) || applicableCommands.contains(command);
	}
	
	public boolean inducesStateChange(String command) {
		return transitionMap().containsKey(command) || (transitionMap.get(command) != this);
	}
	
	@Override
	public boolean equals(Object object) {
		return this.getClass() == object.getClass();
	}
	
	public void addTransition(String command, State state) {
		transitionMap.put(command, state);
	}
	
	public void addCommand(String command) {
		applicableCommands.add(command);
	}
	
	public HashSet<String> applicableCommands() {
		return applicableCommands;
	}

	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}	
}
