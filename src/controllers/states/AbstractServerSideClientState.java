package controllers.states;

import java.util.HashMap;
import java.util.HashSet;

import controllers.ServerSideClientController;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public abstract class AbstractServerSideClientState implements State {
	protected HashMap<String, State> transitionMap;
	protected HashSet<String> applicableCommands;
	protected ServerSideClientController client;
	
	public AbstractServerSideClientState(ServerSideClientController client) {
		this.client = client;
		applicableCommands = new HashSet<String>();
		transitionMap = new HashMap<String, State>();
	}

	@Override
	public State accept(Message message) throws NotApplicableCommandException {
		if (!applicable(message.command())) {
			throw new NotApplicableCommandException();
		}
		return transitionMap.get(message.command());
	}
	
	public abstract void enter();
	
	public abstract void leave();
	
	public boolean reachableState(State state) {
		return reachableStates().stream().anyMatch(s -> s.equals(state));
	}
	
	public boolean reachableState(String state) {
		return reachableStates().stream().anyMatch(s -> s.toString().equals(state));
	}

	@Override
	public boolean applicable(String command) {
		return applicableCommands().stream().anyMatch(s -> s.equals(command));
	}
	
	@Override
	public boolean equals(Object object) {
		return this.getClass() == object.getClass();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	public void addTransition(String command, State state) {
		transitionMap.put(command, state);
	}
	
	public void addCommand(String command) {
		applicableCommands.add(command);
	}
	
	protected HashSet<String> applicableCommands() {
		return applicableCommands;
	}
	
	protected HashSet<State> reachableStates() {
		return new HashSet<State>(transitionMap.values());
	}

}