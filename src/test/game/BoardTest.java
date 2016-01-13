package test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exceptions.InvalidMoveException;
import game.Board;
import game.Stone;

public class BoardTest {	
	private Board board;
	
	@Before
	public void setUp() {
		board = new Board();
	}
	
	@Test
	public void setUpTest() {
		assertNotNull(board);
		assertEquals(Stone.NONE, board.stoneAt(0,0));
		assertEquals(Stone.NONE, board.stoneAt(8,8));
	}
	
	@Test
	public void testIndices() {
		int rowsize = 9;
		int boardsize = rowsize*rowsize;
		Board smallBoard = new Board(rowsize);
		assertEquals(rowsize, smallBoard.size());
		assertEquals(boardsize, smallBoard.indexSize());
		assertTrue(smallBoard.onBoard(0));
		assertTrue(smallBoard.onBoard(boardsize - 1));
		assertFalse(smallBoard.onBoard(boardsize));
		
		assertTrue(smallBoard.onBoard(5,5));
		assertFalse(smallBoard.onBoard(rowsize + 1, 0));
	}
	
	@Test
	public void boardSize19byDefaultTest() {
		assertEquals(19, board.size());
	}
	
	@Test
	public void placeStoneTest() throws InvalidMoveException {
		board.layStone(Stone.BLACK, 3, 4);
		assertTrue(board.anyStoneAt(3, 4));
		assertEquals(Stone.BLACK, board.stoneAt(3, 4));
	}
	
	@Test(expected = InvalidMoveException.class)
	public void placeStoneDoubleTest() throws InvalidMoveException {
		board.layStone(Stone.BLACK, 3);
		board.layStone(Stone.WHITE, 3);
	}
	
	@Test
	public void removeStoneTest() throws InvalidMoveException {
		board.layStone(Stone.WHITE, 5, 5);
		assertEquals(Stone.WHITE, board.stoneAt(5, 5));
		
		board.removeStone(5, 5);
		assertFalse(board.anyStoneAt(5, 5));
	}
	
	@Test(expected = InvalidMoveException.class) 
	public void cannotRemoveNonexistingStone() throws InvalidMoveException {
		assertFalse(board.anyStoneAt(6));
		board.removeStone(6);
	}
	
}
