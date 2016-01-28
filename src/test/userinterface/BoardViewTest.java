package test.userinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import exceptions.InvalidMoveException;
import game.Board;
import game.Stone;
import userinterface.BoardView;

public class BoardViewTest {
	private Board board;
	private int boardSize;
	private BoardView boardView;
	
	@Before
	public void setUp() {
		boardSize = 9;
		board = new Board(boardSize);
		boardView = new BoardView(board);
	}
	
	@Test
	public void testBoardView() {
		assertNotNull(boardView);
		assertEquals(boardView.rowDistanceKeeper(), 
				"\n      |   |   |   |   |   |   |   |   |   \n   ");
		assertEquals(boardView.columnIndicators(),
				"   A   B   C   D   E   F   G   H   I   ");
	}
	
	@Test
	public void testSizes() {
		BoardView b5 = new BoardView(new Board(5));
		assertEquals(b5.rowDistanceKeeper(), "\n      |   |   |   |   |   \n   ");
		assertEquals(b5.columnIndicators(), "   A   B   C   D   E   ");
		System.out.println(b5.render());
	}
	
	@Test
	public void show() throws InvalidMoveException {
		System.out.println(boardView.render());
		board.layStone(Stone.BLACK, 3, 4);
		board.layStone(Stone.WHITE, 3, 5);
		System.out.println(boardView.render());
	}
	
	@Test
	public void bigBoard() {
		int boardSize = 19;
		Board board = new Board(boardSize);
		BoardView boardView = new BoardView(board);
		System.out.println(boardView.render());
	}
	
	@Test
	public void charTest() {
		int boardSize = 9;
		
		
		String center = String.format("%c", (char) 0x253C);
		String leftTop = String.format("%c", (char) 0x250C);
		String rightTop = String.format("%c", (char) 0x2510);
		String leftBottom = String.format("%c", (char) 0x2514);
		String rightBottom = String.format("%c", (char) 0x2518);
		String leftSide = String.format("%c", (char) 0x251C);
		String rightSide = String.format("%c", (char) 0x2524);
		String top = String.format("%c", (char) 0x252C);
		String bottom = String.format("%c", (char) 0x2534);
		
		String vert = String.format("%c", (char) 0x2502);
		String hor = String.format("%c", (char) 0x2500);
		
		String topLine = String.join(" ", leftTop, hor, "X", hor, top, hor, top, hor, rightTop);
		String midLine = String.join(" ", leftSide, hor, center, hor, "O", hor, center, hor, rightSide);
		String bottomLine = String.join(" " , leftBottom, hor, bottom, hor, bottom, hor, bottom, hor, rightBottom);
		
		String[] verticals = new String[5];
		Arrays.fill(verticals, vert);
		String distanceHolder = String.join("   ", verticals);
		distanceHolder = String.join("", "\n", distanceHolder, "\n");
		
		String board = String.join(distanceHolder, topLine, midLine, bottomLine);
		
		System.out.println("\n\n\n");
		System.out.println(board);
	}

}
