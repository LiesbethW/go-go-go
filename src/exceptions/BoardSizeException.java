package exceptions;

public class BoardSizeException extends GoException {

	public BoardSizeException() {
		// TODO Auto-generated constructor stub
	}
	
	public BoardSizeException(int requestedSize) {
		this("You requested a board size of " + requestedSize + 
				" intersections squared. We're so sorry! Only "
				+ "board sizes between 3 and 19 are currently "
				+ "supported.");
	}

	public BoardSizeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BoardSizeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BoardSizeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BoardSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
