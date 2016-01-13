package test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.Node;

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
	}
	
	

}
