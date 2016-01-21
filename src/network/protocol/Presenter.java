package network.protocol;

import java.util.HashMap;

import exceptions.GoException;
import game.Board;
import game.Stone;

public class Presenter implements Constants {

	public static Message exceptionMessage(GoException e) {
		return new Message(FAILURE, CommandSet.exceptionCommand(e));
	}
	
	public static Message newPlayerAccepted() {
		return new Message(NEWPLAYERACCEPTED);
	}
	
	public static Message waitForOpponent() {
		return new Message(WAITFOROPPONENT);
	}
	
	public static Message gameStart(String opponent, int boardSize, Stone color) {
		return new Message(GAMESTART, opponent, String.valueOf(boardSize), color(color));
	}

	public static Message boardMessage(Board board) {
		String boardString = board(board);
		String blackCaptives = String.valueOf(board.captives(Stone.BLACK));
		String whiteCaptives = String.valueOf(board.captives(Stone.WHITE));
		return new Message(BOARD, boardString, blackCaptives, whiteCaptives);
	}
	
	public static Message serverMove(Stone stone, int row, int col) {
		return new Message(MOVE, color(stone), String.valueOf(row), String.valueOf(col));
	}
	
	public static Message clientMove(int row, int col) {
		return new Message(MOVE, String.valueOf(row), String.valueOf(col));
	}
	
	public static Message serverPass(Stone stone) {
		return new Message(MOVE, color(stone), PASS);
	}
	
	public static Message clientPass() {
		return new Message(MOVE, PASS);
	}
	
	public static Message challengeAccepted() {
		return new Message(CHALLENGEACCEPTED);
	}
	
	public static Message challengeDenied() {
		return new Message(CHALLENGEDENIED);
	}
	
	public static Message challenge() {
		return new Message(CHALLENGE);
	}
	
	public static Message challenge(String opponent) {
		return new Message(CHALLENGE, opponent);
	}
	
	public static Message youveChallenged(String opponent) {
		return new Message(YOUVECHALLENGED, opponent);
	}
	
	public static String board(Board board) {
		HashMap<Stone, String> protocolStoneRepr = new HashMap<>();
		protocolStoneRepr.put(Stone.BLACK, B);
		protocolStoneRepr.put(Stone.WHITE, W);
		protocolStoneRepr.put(Stone.NONE, E);
		String[] nodes = new String[board.size()*board.size()];
		for (int i = 0; i < board.size(); i++) {
			for (int j = 0; j < board.size(); j++) {
				nodes[i*board.size() + j] = protocolStoneRepr.get(board.stoneAt(i, j));
			}
		}
		return String.join("", nodes);
	}	

	public static String color(Stone stone) {
		return stone.name();
	}
	
}