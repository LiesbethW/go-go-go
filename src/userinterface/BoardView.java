package userinterface;

import java.util.stream.IntStream;

import game.Board;

public class BoardView {
	private static String VERTICALLINE = "|";
	private static String HORIZONTALLINE = " - ";
	private static String DISTANCEKEEPER = "   ";
	
	public static String row(int row) {
		return String.valueOf(row + 1);
	}
	
	public static String col(int col) {
		return String.valueOf(Character.toChars(col + 65));
	}
	
	private Board board;
	private String rowDistanceKeeper;
	private String columnIndicators;
	
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
			String intersections = String.join(HORIZONTALLINE, stones);
			rows[i] = String.join("  ", row(i), 
					intersections, row(i));
		}
		String allRows = String.join(rowDistanceKeeper(), rows);
		return String.join("\n", columnIndicators(), allRows, columnIndicators());
	}
	
	public String rowDistanceKeeper() {
		if (rowDistanceKeeper == null) {
			String[] verticals = new String[board.size()];
			java.util.Arrays.fill(verticals, VERTICALLINE);
			rowDistanceKeeper = String.join(DISTANCEKEEPER, verticals);
			rowDistanceKeeper = String.join("", "\n", DISTANCEKEEPER, 
					rowDistanceKeeper, DISTANCEKEEPER, "\n");
		}
		return rowDistanceKeeper;
	}
	
	public String columnIndicators() {
		if (columnIndicators == null) {
			String[] columns = new String[board.size()];
			IntStream.range(0, board.size()).forEach(val -> columns[val] = col(val));
			columnIndicators = String.join(DISTANCEKEEPER, columns);
			columnIndicators = String.join("", DISTANCEKEEPER, columnIndicators, DISTANCEKEEPER);
		}
		return columnIndicators;
	}
	
	public String toString() {
		return render();
	}

}
