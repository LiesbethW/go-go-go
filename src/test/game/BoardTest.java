package test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exceptions.InvalidMoveException;
import game.Board;
import game.Stone;

public class BoardTest {	
	private Board board;
	private int row = 5;
	private int col = 5;
	
	@Before
	public void setUp() {
		board = new Board();
	}
	
	@Test
	public void setUpTest() {
		assertNotNull(board);
		assertEquals(Stone.NONE, board.stoneAt(0,0));
		assertEquals(Stone.NONE, board.stoneAt(8,8));
		assertEquals(null, board.stoneAt(-1, 0));
	}
	
	@Test
	public void testIndices() {
		int rowsize = 9;
		Board smallBoard = new Board(rowsize);
		assertEquals(rowsize, smallBoard.size());	
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
		board.layStone(Stone.BLACK, 3, 3);
		board.layStone(Stone.WHITE, 3, 3);
	}
	
	@Test
	public void removeStoneTest() throws InvalidMoveException {
		board.layStone(Stone.WHITE, 5, 5);
		assertEquals(Stone.WHITE, board.stoneAt(5, 5));
		
		board.takeCaptive(5, 5);
		assertFalse(board.anyStoneAt(5, 5));
	}
	
	@Test(expected = InvalidMoveException.class) 
	public void cannotRemoveNonexistingStone() throws InvalidMoveException {
		assertFalse(board.anyStoneAt(6, 1));
		board.takeCaptive(6, 1);
	}
	
	@Test
	public void testCopy() throws InvalidMoveException {
		Board boardCopy = board.deepCopy();
		boardCopy.layStone(Stone.BLACK, 5, 5);
		assertTrue(boardCopy.anyStoneAt(5, 5));
		assertFalse(board.anyStoneAt(5, 5));
		
	}
	
	@Test
	public void testEquals() throws InvalidMoveException {
		Board board1 = new Board();
		Board board2 = new Board();
		assertEquals(board1, board2);
		board1.layStone(Stone.BLACK, row, col);
		assertNotEquals(board1, board2);
		board2.layStone(Stone.BLACK, row, col);
		assertEquals(board1, board2);
	}
	
	@Test
	public void testCaptives() throws InvalidMoveException {
		board.layStone(Stone.WHITE, 5, 5);
		assertEquals(0, board.captives(Stone.WHITE));
		board.takeCaptive(5, 5);
		assertEquals(1, board.captives(Stone.WHITE));
	}
	
	@Test
	public void testLiberties() throws InvalidMoveException {
		board.layStone(Stone.BLACK, 5, 5);
		assertEquals(4, board.liberties(5, 5));
		board.layStone(Stone.WHITE, 4, 5);
		assertEquals(3, board.liberties(5, 5));
		board.layStone(Stone.BLACK, 5, 6);
		assertEquals(5, board.liberties(5, 5));
	}
	
	@Test
	public void testCapturing() throws InvalidMoveException {
		board.layStone(Stone.BLACK, row, col);
		board.layStone(Stone.WHITE, row - 1, col);
		board.layStone(Stone.WHITE, row + 1, col);
		board.layStone(Stone.WHITE, row, col + 1);
		
		assertEquals(1, board.liberties(row, col));
		assertEquals(0, board.captives(Stone.BLACK));
		board.layStone(Stone.WHITE, row, col - 1);
		assertFalse(board.anyStoneAt(row, col));
		assertEquals(1, board.captives(Stone.BLACK));
	}
	
	@Test(expected = InvalidMoveException.class)
	public void suicideNotAllowedTest() throws InvalidMoveException {
		board.layStone(Stone.BLACK, row - 1, col);
		board.layStone(Stone.BLACK, row + 1, col);
		board.layStone(Stone.BLACK, row, col + 1);
		board.layStone(Stone.BLACK, row, col - 1);
		
		board.layStone(Stone.WHITE, row, col);
	}
	
	@Test
	public void createBoardFromStoneArrayTest() {
		Stone[][] stones = new Stone[2][2];
		stones[0][0] = Stone.BLACK;
		stones[0][1] = Stone.BLACK;
		stones[1][0] = Stone.WHITE;
		stones[1][1] = Stone.NONE;
		
		Board boardFromArray = new Board(stones, 5, 6);
		assertEquals(Stone.BLACK, boardFromArray.stoneAt(0, 0));
		assertEquals(Stone.WHITE, boardFromArray.stoneAt(1, 0));
		assertEquals(5, boardFromArray.captives(Stone.BLACK));
		assertEquals(6, boardFromArray.captives(Stone.WHITE));
	}
	
}
