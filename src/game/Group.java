package game;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private Board board;
	private Stone color;
	private List<Integer> points;
	private int liberties;
	private List<Integer> adjacentFreePoints;
	
	/**
	 * Create a new group for a single stone.
	 * @param stone
	 * @param gridIndex
	 */
	public Group(Board board, Stone stone, int gridIndex) {
		this.board = board;
		color = stone;
		points = new ArrayList<Integer>();
		adjacentFreePoints = new ArrayList<Integer>();
		
		points.add(gridIndex);
		checkPoints();
		determineLiberties();
	}
	
	/**
	 * Find out if certain gridIndex is one of the free
	 * points adjacent to this group.
	 * @param gridIndex
	 * @return
	 */
	public Boolean adjacent(int gridIndex) {
		return adjacentFreePoints.contains((int) gridIndex);
	}
	
	public void checkPoints() {
		for (int point : points) {
			
		}
	}
	
	public Stone color() {
		return color;
	}
	
	public void determineLiberties() {
		
	}
	
	/**
	 * Use this method to take away liberties of 
	 * @param gridIndex
	 */
	public void reduceLiberties(int gridIndex) {
		
	}
	
	/**
	 * Capture a certain group: remove the stones
	 * from the board.
	 */
	private void captureGroup() {
		
	}

}
