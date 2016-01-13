package game;

import java.util.Arrays;
import java.util.Observable;

import exceptions.*;

public class Board {
	
	public static int DEFAULT_BOARD_SIZE = 19;
	
	private int boardSize;
	private Stone[] grid;
	private GroupHelper groupHelper;
	private Board backup;
	private int blackCaptives;
	private int whiteCaptives;
	
	/**
	 * Create a new board with the given size.
	 * @param size
	 */
	public Board(int size) {
		boardSize = size;
		reset();
		groupHelper = new GroupHelper(this);
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
	 * Rows times columns, useful to know for determining
	 * if indices will be out of bounds.
	 * @return The board size squared
	 */
	public int indexSize() {
		return size()*size();
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
		return onBoard(gridIndex(row, col));
	}
	
	/**
	 * Is this index a valid index on the board?
	 * @param gridIndex
	 * @return True if index is on the board, false
	 * if it is not.
	 */
	public Boolean onBoard(int gridIndex) {
		return gridIndex >= 0 && gridIndex < size()*size();
	}	
	
	/**
	 * Get the stone at the given row and column.
	 * @param row
	 * @param col
	 * @return (Enum) Stone, Stone.WHITE or Stone.BLACK,
	 * or null if the point is empty.
	 */
	public Stone stoneAt(int row, int col) {
		return stoneAt(gridIndex(row,col));
	}
	
	/**
	 * Get the stone at a certain grid point.
	 * @param gridIndex
	 * @return Stone.WHITE or Stone.BLACK,
	 * or null if the point is empty.
	 * @require gridIndex < size()*size()
	 */
	public Stone stoneAt(int gridIndex) {
		return grid[gridIndex];
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
		return anyStoneAt(gridIndex(row,col));
	}
	
	/**
	 * Return if there is a stone at the given
	 * gridIndex.
	 * @param gridIndex
	 * @return True if there is a stone, false if
	 * this point is still empty.
	 */
	public Boolean anyStoneAt(int gridIndex) {
		return stoneAt(gridIndex) != Stone.NONE;
	}
	
	/**
	 * Lay a stone on the specified point on the grid.
	 * @param stone, must be Stone.WHITE or Stone.BLACK
	 * @param row index
	 * @param col index
	 * @throws InvalidMoveException if this point is already taken.
	 */
	public void layStone(Stone stone, int row, int col) throws InvalidMoveException {
		layStone(stone, gridIndex(row,col));
	}
	
	/**
	 * Put a stone at the given point
	 * @param stone, must be Stone.BLACK or Stone.WHITE
	 * @param gridIndex
	 */
	public void layStone(Stone stone, int gridIndex) throws InvalidMoveException {
		if (anyStoneAt(gridIndex)) {
			throw new InvalidMoveException("Point " + gridIndex + " is already occupied");
		}
		
		try {
			backup = this.deepCopy();
			grid[gridIndex] = stone;
		}
		
	}
	
	/**
	 * Remove stone on specified position from board. 
	 * @param row
	 * @param col
	 * @throws InvalidMoveException if there was no
	 * stone at that the specified position.
	 */
	public void removeStone(int row, int col) throws InvalidMoveException {
		removeStone(gridIndex(row, col));
	}
	
	/**
	 * Remove stone on specified position from board and
	 * add it to the captives.
	 * @param gridIndex
	 * @throws InvalidMoveException if there was no
	 * stone at that the specified position.
	 */
	public void removeStone(int gridIndex) throws InvalidMoveException {
		if (!anyStoneAt(gridIndex)) {
			throw new InvalidMoveException("There is no stone at " + gridIndex);
		}
		
		takeCaptive(stoneAt(gridIndex));
		grid[gridIndex] = Stone.NONE;
	}
	
	/**
	 * Calculate the index of the point in the 1-dimensional
	 * grid array, based on row and column index.
	 * @param integer row
	 * @param integer column
	 * @return The index for the grid array.
	 */
	private int gridIndex(int row, int col) {
		return row*size() + col;
	}
	
	/**
	 * Take this stone captive: add 1 to the
	 * number of captives of the corresponding
	 * color.
	 * @param stone (Stone.BLACK or Stone.WHITE)
	 */
	private void takeCaptive(Stone stone) {
		if (stone == Stone.BLACK) {
			blackCaptives += 1;
		} else if (stone == Stone.WHITE ) {
			whiteCaptives += 1;
		}
		// else throw exception?
	}
	
	/**
	 * Resets the grid to all 'NONE' stones.
	 * Resets the captive numbers to 0.
	 */
	private void reset() {
		grid = new Stone[size()*size()];
		Arrays.fill(grid, (Stone.NONE));
		blackCaptives = 0;
		whiteCaptives = 0;
	}
	
	private Board deepCopy() {
		Board copy = new Board(this.size());
		System.arraycopy(grid, 0, copy.grid, 0, indexSize());
		copy.backup = this;
		return copy;
	}
	
	private void restore() throws CorruptedStateException {
		this.grid = backup.grid;
		this.backup = backup.backup;
		this.groupHelper = new GroupHelper(this);
	}
}
