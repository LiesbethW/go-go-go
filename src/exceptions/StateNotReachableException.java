package exceptions;

import controllers.states.State;

public class StateNotReachableException extends GoException {

	public StateNotReachableException() {
		// TODO Auto-generated constructor stub
	}
	
	public StateNotReachableException(State fromState, State toState) {
		this(String.format("Cannot go from %s to %s.", fromState.toString(), toState.toString()));
	}

	public StateNotReachableException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StateNotReachableException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public StateNotReachableException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public StateNotReachableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
