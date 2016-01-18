package game;

public class Game {
	public static final int DEFAULT_BOARD_SIZE = 19;
	
	private Board board;
	private Player black;
	private Player white;
	
	public Game(Player player1, Player player2, int boardSize) {
		black = player1;
		white = player2;
		board = new Board(boardSize);
	}
	
	public Game(Player player1, Player player2) {
		this(player1, player2, DEFAULT_BOARD_SIZE);
	}
	
}
