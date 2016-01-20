package controllers.states;

import java.util.HashSet;

import exceptions.NotApplicableCommandException;
import exceptions.StateNotReachableException;

public abstract class AbstractLobbyState implements State {

	public AbstractLobbyState() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public State accept(String command) throws NotApplicableCommandException {
		if (!applicable(command)) {
			throw new NotApplicableCommandException();
		}
		return reachableStates().stream().filter(s -> s.toString().equals(command)).findFirst().get();
	}
	
	public State accept(State state) throws StateNotReachableException {
		if (!reachableState(state)) {
			throw new StateNotReachableException(this, state);
		}
		return state;
	}
	
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
	
	protected abstract HashSet<String> applicableCommands();
	
	protected abstract HashSet<State> reachableStates();

}
