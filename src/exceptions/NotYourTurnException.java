package exceptions;

public class NotYourTurnException extends GoException {

	public NotYourTurnException() {
		this("It is not your turn!");
	}

	public NotYourTurnException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NotYourTurnException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NotYourTurnException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NotYourTurnException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
