package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 * Is this Node free? (No stone on it).
	 * @return true if no stone is on this node
	 */
	public Boolean free() {
		return getStone() == Stone.NONE;
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
	
	/**
	 * Liberties of the group this stone
	 * belongs to. Not guaranteed to give a sensible
	 * result if this Node is free.
	 * @return The number of free adjacent nodes.
	 */
	public int liberties() {
		return freeAdjacentNodes().size();
	}
	
	/**
	 * The adjacent nodes of this node.
	 * @return a list of Nodes.
	 */
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
	
	/**
	 * The size of the group this stone
	 * belongs to.
	 * @return
	 */
	public int groupSize() {
		return group().size();
	}
	
	/**
	 * The group that this node belongs to.
	 * @return
	 */
	public Set<Node> group() {
		Set<Node> myGroup = new HashSet<Node>();
		myGroup.add(this);
		return group(myGroup);
	}

	/**
	 * For each of this nodes neighbours, add
	 * that neighbour to the group if it is the
	 * same color stone, and then repeat for the
	 * neighbours of my neighbour.
	 * @param myGroup
	 * @return
	 */
	private Set<Node> group(Set<Node> myGroup) {
		for (Node neighbour : neighbours()) {
			if (!myGroup.contains(neighbour) && neighbour.getStone() == this.getStone()) {
				myGroup.add(neighbour);
				myGroup.addAll(neighbour.group(myGroup));
			}
		}
		return myGroup;
	}
	
	private Set<Node> freeAdjacentNodes() {
		Set<Node> freeAdjacentNodes = new HashSet<Node>();
		for (Node node : group()) {
			for (Node neighbour : node.neighbours()) {
				if (neighbour.free()) {
					freeAdjacentNodes.add(neighbour);
				}
			}
		}
		return freeAdjacentNodes;
	}
	
}
