package test.userinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
				"\n   |   |   |   |   |   |   |   |   |   \n");
		assertEquals(boardView.columnIndicators(),
				"   A   B   C   D   E   F   G   H   I   ");
	}
	
	@Test
	public void testSizes() {
		BoardView b5 = new BoardView(new Board(5));
		assertEquals(b5.rowDistanceKeeper(), "\n   |   |   |   |   |   \n");
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

}
