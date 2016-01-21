package controllers.states;

import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public interface State {
	
	public State accept(Message message) throws NotApplicableCommandException;
	
	public boolean applicable(String command);
	
	public void enter();
	
	public void leave();
	
}
