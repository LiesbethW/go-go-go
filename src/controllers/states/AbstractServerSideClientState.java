package controllers.states;

import java.util.HashMap;
import java.util.HashSet;

import controllers.ClientHandler;
import network.protocol.Message;

public abstract class AbstractServerSideClientState implements 
								State, network.protocol.Constants {
	protected HashMap<String, State> transitionMap;
	protected HashSet<String> applicableCommands;
	protected ClientHandler client;
	
	public AbstractServerSideClientState(ClientHandler client) {
		this.client = client;
		applicableCommands = new HashSet<String>();
		transitionMap = new HashMap<String, State>();
	}

	@Override
	public State accept(Message message) {
		if (!transitionMap().containsKey(message.command())) {
			System.err.println(String.format("Server should not try to send command "
					+ "%s to client in state %s", message.command(), this.toString()));
		}
		return transitionMap().get(message.command());
	}
	
	public abstract void enter(Message message);
	
	public abstract void leave(Message message);
	
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
	
	public HashSet<String> applicableCommands() {
		return applicableCommands;
	}
	
	protected HashSet<State> reachableStates() {
		return new HashSet<State>(transitionMap.values());
	}
	
	protected HashMap<String, State> transitionMap() {
		return transitionMap;
	}

}
