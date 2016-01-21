package network.protocol;

import java.util.HashMap;

import exceptions.InvalidArgumentException;
import exceptions.UnknownCommandException;
import game.Board;
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
	
	public static int integer(String string) throws InvalidArgumentException {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new InvalidArgumentException(
					String.format("A number was expected, %s was given", string));
		}
	}
	
	public static Board board(Message message) throws InvalidArgumentException {
		HashMap<String, Stone> toStone = new HashMap<>();
		toStone.put(B, Stone.BLACK);
		toStone.put(W, Stone.WHITE);
		toStone.put(E, Stone.NONE);
		
		int size = (int) Math.sqrt(message.args()[0].length());
		int blackCaptives = integer(message.args()[1]);
		int whiteCaptives = integer(message.args()[2]);
		Stone[][] stones = new Stone[size][size];
		
		String[] letters = message.args()[0].split("");
		int index = 0;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				stones[i][j] = toStone.get(letters[index]);
				index++;
			}
		}
		
		return new Board(stones, blackCaptives, whiteCaptives);
	}
	
}
