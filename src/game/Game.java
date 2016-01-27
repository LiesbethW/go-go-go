package game;

import exceptions.InvalidMoveException;
import exceptions.NotYourTurnException;

public class Game {
	public static final int DEFAULT_BOARD_SIZE = 19;
	
	private Board board;
	private Board oneTurnEarlier;
	private Player black;
	private Player white;
	private Player playerOnTurn;
	private Player winner;
	private int consecutivePasses;
	private boolean gameOver;
	
	/**
	 * Create a new game with the given players and
	 * boardsize.
	 * @param player1
	 * @param player2
	 * @param boardSize
	 */
	public Game(Player player1, Player player2, int boardSize) {
		black = player1;
		player1.setColor(Stone.BLACK);
		white = player2;
		player2.setColor(Stone.WHITE);
		board = new Board(boardSize);
		gameOver = false;
		playerOnTurn = black;
	}
	
	/**
	 * Create a new game with the default board
	 * size.
	 * @param player1
	 * @param player2
	 */
	public Game(Player player1, Player player2) {
		this(player1, player2, DEFAULT_BOARD_SIZE);
	}
	
	/**
	 * Return the player whose turn it is.
	 * @return
	 */
	public Player whosTurnIsIt() {
		return playerOnTurn;
	}
	
	public Player otherPlayer(Player player) {
		if (player == white) {
			return black;
		} else if (player == black) {
			return white;
		}
		return null;
	}
	
	/**
	 * To use after a move: switch turns
	 * to the other player.
	 */
	public void switchTurns() {
		playerOnTurn = otherPlayer(playerOnTurn);
	}
	
	public void makeMove(Player player, int row, int col) 
			throws NotYourTurnException, InvalidMoveException {
		if (player != playerOnTurn) {
			throw new NotYourTurnException();
		} else if (!validMove(player.getColor(), row, col)) {
			throw new InvalidMoveException();
		}
		
		consecutivePasses = 0;
		oneTurnEarlier = board.deepCopy();
		board.layStone(player.getColor(), row, col);
		switchTurns();
	}
	
	public void pass(Player player) throws NotYourTurnException {
		if (player != playerOnTurn) {
			throw new NotYourTurnException();
		}
		
		consecutivePasses++;
		switchTurns();
		checkIfGameHasEnded();
	}
	
	/**
	 * Check if a move would be valid.
	 * @param Player
	 * @param row
	 * @param col
	 * @return true if the move is allowed.
	 */
	public boolean validMove(Player player, int row, int col) {
		return validMove(player.getColor(), row, col);
	}

	/**
	 * Check if a move would be valid.
	 * @param Stone
	 * @param row
	 * @param col
	 * @return true if the move is allowed.
	 */
	public boolean validMove(Stone stone, int row, int col) {
		Board boardCopy = board.deepCopy();
		try {
			boardCopy.layStone(stone, row, col);
		} catch (InvalidMoveException e) {
			return false;
		}
		// The Ko rule
		if (boardCopy.equals(oneTurnEarlier)) {
			return false;
		}
		return true;
	}
	
	public void checkIfGameHasEnded() {
		if (consecutivePasses >= 2 && playerOnTurn == black) {
			determineWinner();
			playerOnTurn = null;
			gameOver = true;
		}
	}
	
	public Board getBoard() {
		return board.deepCopy();
	}

	public void determineWinner() {
//		board.getTerritories();
	}
	
	public Player winner() {
		return winner;
	}
	
	public boolean gameOver() {
		return gameOver;
	}
	
}
