package exceptions;

public class InvalidArgumentException extends UnknownCommandException {

	public InvalidArgumentException() {
		this("The arguments given were not valid.");
	}

	public InvalidArgumentException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidArgumentException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidArgumentException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidArgumentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
