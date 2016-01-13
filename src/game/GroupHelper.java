package game;

import java.util.ArrayList;
import java.util.List;

import exceptions.CorruptedStateException;
import exceptions.GoException;
import exceptions.InvalidMoveException;

public class GroupHelper {
	private List<Group> groups;
	private Board board;
	
	public GroupHelper(Board board) throws CorruptedStateException {
		this.board = board;
		calculate();
	}
	
	public void calculate() throws CorruptedStateException {
		groups = new ArrayList<Group>();
		for (int i = 0; i < board.indexSize(); i++) {
			if (board.onBoard(i)) {
				try {
					recalculate(i);
				} catch (GoException e) {
					throw new CorruptedStateException();
				}
			}
		}
	}
	
	public void recalculate(int newStoneIndex) throws InvalidMoveException {
		System.out.println("Recalculate groups...");
		Stone color = board.stoneAt(newStoneIndex);
		List<Group> adjacentSameColorGroups = new ArrayList<Group>();
		List<Group> adjacentOtherColorGroups = new ArrayList<Group>();
		for (Group group : groups) {
			if (group.adjacent(newStoneIndex)) {
				if (group.color() == color) {
					adjacentSameColorGroups.add(group);
				} else {
					adjacentOtherColorGroups.add(group);
				}
			}
		}
		
		for (Group group : adjacentOtherColorGroups) {
			group.reduceLiberties(newStoneIndex);
		}
		
	}
	
}
