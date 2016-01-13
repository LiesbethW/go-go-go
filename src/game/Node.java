package game;

import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidMoveException;

public class Node {
	int row;
	int col;
	Stone stone;
	Board board;
	List<Node> neighbours;
	
	/**
	 * Create a new Node on a board with the
	 * given row and column position.
	 * @param Board 
	 * @param row
	 * @param col
	 */
	public Node(Board board, int row, int col) {
		this.board = board;
		this.row = row;
		this.col = col;
		stone = Stone.NONE;
	}
	
	/**
	 * Is there a stone on this Node?
	 * @return true if Stone.WHITE or Stone.BLACK
	 * at this Node.
	 */
	public Boolean taken() {
		return getStone() == Stone.BLACK || getStone() == Stone.WHITE;
	}
	
	/**
	 * Get the Stone on this Node.
	 * @return WHITE, BLACK or NONE
	 */
	public Stone getStone() {
		return stone;
	}
	
	/**
	 * Lay a stone on this node
	 * @param stone
	 * @throws InvalidMoveException if getStone() != NONE
	 */
	public void layStone(Stone stone) throws InvalidMoveException {
		if (this.stone != Stone.NONE) {
			throw new InvalidMoveException();
		} else {
			this.stone = stone;
		}
	}
	
	/**
	 * Remove stone from this Node.
	 */
	public void removeStone() {
		this.stone = Stone.NONE;
	}
	
	public int liberties() {
		
		return 0;
	}
	
	public List<Node> neighbours() {
		if (neighbours == null) {
			neighbours = new ArrayList<Node>();
			if (board.node(row - 1,col) != null) {
				neighbours.add(board.node(row - 1,col));
			}
			if (board.node(row + 1,col) != null) {
				neighbours.add(board.node(row + 1,col));
			}
			if (board.node(row, col - 1) != null) {
				neighbours.add(board.node(row, col - 1));
			}
			if (board.node(row, col + 1) != null) {
				neighbours.add(board.node(row,col + 1));
			}
		} 
		
		return neighbours;
	}
	
}
