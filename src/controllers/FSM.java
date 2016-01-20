package controllers;

import controllers.states.State;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public interface FSM {
	
	public void digest(Message message) throws NotApplicableCommandException;
	
	public State currentState();
	
}
