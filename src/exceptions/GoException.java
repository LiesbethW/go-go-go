package exceptions;

public class GoException extends Exception {

	public GoException() {
	}

	public GoException(String message) {
		super(message);
	}

	public GoException(Throwable cause) {
		super(cause);
	}

	public GoException(String message, Throwable cause) {
		super(message, cause);
	}

	public GoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
