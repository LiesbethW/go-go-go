package test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import game.Stone;

public class StoneTest {
	private Stone stone;
	private Stone noStone;
	
	@Before
	public void setUp() {
		stone = Stone.BLACK;
		noStone = Stone.NONE;
	}
	
	@Test
	public void testSetUp() {
		assertEquals(Stone.BLACK, stone);
		assertEquals(Stone.NONE, noStone);
	}
	
	@Test
	public void opponentTest() {
		assertEquals(Stone.WHITE, stone.opponent());
		assertEquals(noStone, noStone.opponent());
	}
	
	@Test
	public void isOpponentTest() {
		assertFalse(stone.isOpponent(Stone.BLACK));
		assertFalse(stone.isOpponent(Stone.NONE));
		assertTrue(stone.isOpponent(Stone.WHITE));
	}
	
	@Test
	public void toStringTest() {
		assertEquals(String.format("%c", (char) 0x2588), stone.toString());
		assertEquals("O", Stone.WHITE.toString());
		assertEquals(String.format("%c", (char) 0x00B7), noStone.toString());
	}
	
	@Test
	public void nameTest() {
		assertEquals("WHITE", Stone.WHITE.name());
		assertEquals("BLACK", Stone.BLACK.name());
	}
	
}
