package exceptions;

public class InvalidMoveException extends GoException {

	public InvalidMoveException() {
		this("The move you try to make is not a valid move.");
	}

	public InvalidMoveException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoveException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoveException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidMoveException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
