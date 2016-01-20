package controllers.states;

import exceptions.NotApplicableCommandException;

public interface State {
	
	public State accept(String command) throws NotApplicableCommandException;
	
	public boolean applicable(String command);
	
}
