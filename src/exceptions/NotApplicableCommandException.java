package exceptions;

import controllers.states.State;

public class NotApplicableCommandException extends ProtocolException {

	public NotApplicableCommandException() {
		// TODO Auto-generated constructor stub
	}

	public NotApplicableCommandException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public NotApplicableCommandException(String command, State state) {
		this(String.format("The command %s is not applicable for client "
				+ "in state %s", command, state));
	}

	public NotApplicableCommandException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NotApplicableCommandException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NotApplicableCommandException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
