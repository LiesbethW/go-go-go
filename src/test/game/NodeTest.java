package test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import exceptions.InvalidMoveException;
import game.Board;
import game.Node;
import game.Stone;

public class NodeTest {
	private Board board;
	private Node centerNode;
	private Node edgeNode;
	private Node cornerNode;
	private int row = 5;
	private int col = 5;
	
	@Before
	public void setUp() {
		board = new Board();
		centerNode = board.node(row, col);
		edgeNode = board.node(0, col);
		cornerNode = board.node(0, 0);
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(board);
		assertNotNull(centerNode);
		assertEquals(board.stoneAt(row, col), centerNode.getStone());
		assertFalse(centerNode.taken());
	}
	
	@Test
	public void testNeighbours() {
		assertEquals(4, centerNode.neighbours().size());
		assertEquals(3, edgeNode.neighbours().size());
		assertEquals(2, cornerNode.neighbours().size());
		
		assertTrue(centerNode.neighbours().contains(board.node(row - 1, col)));
		assertTrue(centerNode.neighbours().contains(board.node(row, col + 1)));
		assertFalse(centerNode.neighbours().contains(board.node(row + 1, col + 1)));
	}
	
	@Test
	public void testGroup() throws InvalidMoveException {
		board.layStone(Stone.BLACK, row, col);
		assertTrue(centerNode.group().contains(centerNode));
		assertEquals(1, centerNode.groupSize());
		
		board.layStone(Stone.BLACK, row + 1, col);
		assertTrue(centerNode.group().contains(board.node(row + 1, col)));
		assertEquals(2, centerNode.groupSize());
		
		board.layStone(Stone.BLACK, row, col + 1);
		assertTrue(centerNode.group().contains(board.node(row, col + 1)));
		assertEquals(3, centerNode.groupSize());
		
		board.layStone(Stone.BLACK, row + 1, col + 1);
		assertTrue(centerNode.group().contains(board.node(row + 1, col + 1)));
		assertEquals(4, centerNode.groupSize());
	}
	
	@Test
	public void testLiberties() throws InvalidMoveException {
		cornerNode.layStone(Stone.BLACK);
		assertEquals(2, cornerNode.liberties());
		
		edgeNode.layStone(Stone.BLACK);
		assertEquals(3, edgeNode.liberties());
		
		centerNode.layStone(Stone.BLACK);
		assertEquals(4, centerNode.liberties());
		board.layStone(Stone.BLACK, row + 1, col);
		assertEquals(6, centerNode.liberties());
		board.layStone(Stone.WHITE, row, col + 1);
		assertEquals(5, centerNode.liberties());
		
	}
	

}
