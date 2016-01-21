package network.protocol;

import exceptions.InvalidArgumentException;
import exceptions.UnknownCommandException;
import game.Stone;

public class StaticInterpreter implements Constants {

	public static Message digest(String message) throws UnknownCommandException {
		String[] words = message.split(DELIMITER);
		String command = words[0];
		String[] args = new String[words.length - 1];
		System.arraycopy(words, 1, args, 0, words.length - 1);

		commandPartOfProtocol(command);
		return new Message(command, args);
	}

	public static Boolean commandPartOfProtocol(String command) throws UnknownCommandException {
		Boolean partOfProtocol = CommandSet.contains(command);
		if (!partOfProtocol) {
			throw new UnknownCommandException(command);
		}
		return partOfProtocol;
	}
	
	public static Stone color(String string) throws InvalidArgumentException {
		if (string == WHITE) {
			return Stone.WHITE;
		} else if (string == BLACK) {
			return Stone.BLACK;
		} else {
			throw new InvalidArgumentException();
		}
	}
	
	public static int coordinate(String string) throws InvalidArgumentException {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new InvalidArgumentException(
					String.format("A number was expected, %s was given", string));
		}
	}
	
}
