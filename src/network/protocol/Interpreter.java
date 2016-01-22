package network.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import exceptions.InvalidArgumentException;
import exceptions.UnknownCommandException;
import game.Stone;

public class Interpreter implements Constants {

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
		if (string.equals(WHITE)) {
			return Stone.WHITE;
		} else if (string.equals(BLACK)) {
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
	
	public static HashMap<String, Boolean> options(Message message) {
		List<String> options = new ArrayList<String>(Arrays.asList(CHAT, 
				CHALLENGE, OBSERVER, COMPUTERPLAYER));
		HashMap<String, Boolean> optionMap = new HashMap<String, Boolean>();
		for (String optionString : message.args()) {
			if (options.stream().anyMatch(s -> s.equals(optionString))) {
				optionMap.put(optionString, true);
			}
		}
		return optionMap;
	}
	
}
