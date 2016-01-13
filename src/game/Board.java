package game;

import exceptions.InvalidMoveException;

public class Board {
	
	public static int DEFAULT_BOARD_SIZE = 19;
	
	private int boardSize;
	private Node[][] grid;
	private int blackCaptives;
	private int whiteCaptives;
	
	/**
	 * Create a new board with the given size.
	 * @param size
	 */
	public Board(int size) {
		boardSize = size;
		reset();
	}
	
	/**
	 * Create a new board with the default size,
	 * 19 by 19.
	 */
	public Board() {
		this(DEFAULT_BOARD_SIZE);
	}
	
	/**
	 * The board size. Keep in mind that the board
	 * is always square: a 'size' of 9 means that the
	 * board is 9x9.
	 * @return size
	 */
	public int size() {
		return boardSize;
	}
	
	/**
	 * Is this combination of row and column
	 * values on the board?
	 * @param row
	 * @param col
	 * @return True if index is on the board, false
	 * if it is not.
	 */
	public Boolean onBoard(int row, int col) {
		return row >= 0 && row < size() && col >=0 && col < size();
	}
	
	/**
	 * Get the stone at the given row and column.
	 * @param row
	 * @param col
	 * @return (Enum) Stone, Stone.WHITE or Stone.BLACK,
	 * or Stone.NONE if the point is empty. Returns null
	 * if the given combination of row and column is
	 * not on the board.
	 */
	public Stone stoneAt(int row, int col) {
		if (!onBoard(row,col)) {
			return null;
		}
		return grid[row][col].getStone();
	}
	
	/**
	 * Return if there is a stone at the given
	 * row and column.
	 * @param row
	 * @param column
	 * @return True if there is a stone, false if
	 * this point is still empty.
	 */
	public Boolean anyStoneAt(int row, int col) {
		return grid[row][col].taken();
	}
	
	/**
	 * Lay a stone on the specified point on the grid.
	 * @param stone, must be Stone.WHITE or Stone.BLACK
	 * @param row index
	 * @param col index
	 * @throws InvalidMoveException if this point is already taken.
	 */
	public void layStone(Stone stone, int row, int col) throws InvalidMoveException {
		grid[row][col].layStone(stone);
	}
	
	/**
	 * Remove stone on specified position from board
	 * and add it to the captives of its color.
	 * @param row
	 * @param col
	 * @throws InvalidMoveException if there was no
	 * stone at that the specified position.
	 */
	public void removeStone(int row, int col) throws InvalidMoveException {
		takeCaptive(grid[row][col].getStone());
		grid[row][col].removeStone();
	}
	
	/**
	 * Get Node at a certain point. Necessary for Node
	 * to find it's neighbours.
	 * @param row
	 * @param column
	 * @return Node 
	 */
	public Node node(int row, int col) {
		if (!onBoard(row, col)) {
			return null;
		} else {
			return grid[row][col];
		}
	}
	
	/**
	 * Get the liberties of the group that the
	 * stone at the given position is part of.
	 * Not guaranteed to give sensible result
	 * if there is no stone a the given node.
	 * @param row
	 * @param column
	 * @return The number of liberties (natural
	 * integer).
	 */
	public int liberties(int row, int col) {
		return grid[row][col].liberties();
	}
	
	
	/**
	 * Take this stone captive: add 1 to the
	 * number of captives of the corresponding
	 * color.
	 * @param stone (Stone.BLACK or Stone.WHITE)
	 */
	private void takeCaptive(Stone stone) throws InvalidMoveException {
		if (stone == Stone.BLACK) {
			blackCaptives += 1;
		} else if (stone == Stone.WHITE ) {
			whiteCaptives += 1;
		} else {
			throw new InvalidMoveException();
		}
	}
	
	/**
	 * Return the number of captives for the given
	 * stone color.
	 */
	public int captives(Stone stone) {
		if (stone == Stone.BLACK) {
			return blackCaptives;
		} else if (stone == Stone.WHITE) {
			return whiteCaptives;
		} else {
			return 0;
		}
	}
	
	/**
	 * Resets the grid to all 'NONE' stones.
	 * Resets the captive numbers to 0.
	 */
	private void reset() {
		grid = new Node[size()][size()];
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < size(); j++) {
				grid[i][j] = new Node(this, i,j);
			}
		}
		blackCaptives = 0;
		whiteCaptives = 0;
	}
	
	/**
	 * Make a copy of this board.
	 * @return A new board with new nodes, that
	 * have the same stones on them.
	 */
	public Board deepCopy() {
		Board copy = new Board(size());
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < size(); j++) {
				try {
					copy.grid[i][j].layStone(this.grid[i][j].getStone());
				} catch (InvalidMoveException e) {
					//ignore for this special case.
				}				
			}
		}
		copy.blackCaptives = this.blackCaptives;
		copy.whiteCaptives = this.whiteCaptives;

		return copy;
	}
	
}
