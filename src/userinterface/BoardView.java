package userinterface;

import game.Board;

public class BoardView {
	private static String VERTICALLINE = "|";
	private static String HORIZONTALLINE = " - ";
	private static String DISTANCEKEEPER = "   ";
	
	private Board board;
	private String rowDistanceKeeper;
	
	public BoardView(Board board) {
		this.board = board;
	}
	
	public String render() {
		String[] rows = new String[board.size()];
		for (int i = 0; i < board.size(); i++) {
			String[] stones = new String[board.size()];
			for (int j = 0; j < board.size(); j++) {
				stones[j] = board.stoneAt(i, j).toString();
			}
			rows[i] = String.join(HORIZONTALLINE, stones);
		}
		return String.join(rowDistanceKeeper(), rows);
	}
	
	private String rowDistanceKeeper() {
		if (rowDistanceKeeper == null) {
			String[] verticals = new String[board.size()];
			java.util.Arrays.fill(verticals, VERTICALLINE);
			rowDistanceKeeper = String.join(DISTANCEKEEPER, verticals);
			rowDistanceKeeper = "/n" + rowDistanceKeeper + "/n";
		}
		return rowDistanceKeeper;
	}

}
