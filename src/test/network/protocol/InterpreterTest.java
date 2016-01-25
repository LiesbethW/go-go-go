package test.network.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import exceptions.InvalidArgumentException;
import exceptions.InvalidMoveException;
import exceptions.UnknownCommandException;
import game.Board;
import game.Stone;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class InterpreterTest {

	@Test
	public void testDigest() throws UnknownCommandException {
		String message = "NEWPLAYER Jan";
		assertEquals(message, Interpreter.digest(message).toString());
		
		String message2 = "QUIT";
		assertEquals(message2, Interpreter.digest(message2).toString());
		
		String message3 = "CHAT Hee hallo, hoe gaat het? Heeft er iemand zin"
				+ "om een spelletje Go met mij te spelen?";
		assertEquals(message3, Interpreter.digest(message3).toString());
	}
	
	@Test
	public void testCommandPartOfProtocol() throws UnknownCommandException {
		assertTrue(Interpreter.commandPartOfProtocol("PLAY"));
		assertTrue(Interpreter.commandPartOfProtocol("WHITE"));
		assertTrue(Interpreter.commandPartOfProtocol("UnknownCommand"));
	}
	
	@Test(expected = UnknownCommandException.class)
	public void testCommandNotPartOfProtocol() throws UnknownCommandException {
		assertFalse(Interpreter.commandPartOfProtocol("Hellloooo"));
	}
	
	@Test
	public void colorTest() throws InvalidArgumentException {
		assertEquals(Stone.BLACK, Interpreter.color("BLACK"));
		assertEquals(Stone.WHITE, Interpreter.color("WHITE"));
	}
	
	@Test(expected = InvalidArgumentException.class)
	public void invalidColorTest() throws InvalidArgumentException {
		Interpreter.color("oranje");
	}
	
	@Test
	public void integerTest() throws InvalidArgumentException {
		assertEquals(4, Interpreter.integer("4"));
		assertEquals(0, Interpreter.integer("0"));
	}
	
	@Test
	public void boardFromStringTest() throws InvalidMoveException, InvalidArgumentException {
		Board board = new Board(2);
		board.layStone(Stone.BLACK, 1, 0);
		board.layStone(Stone.WHITE, 0, 1);
		Message message = Presenter.boardMessage(board);
		assertEquals("EWBE", message.args()[0]);
		Board boardFromMessage = Interpreter.board(message);
		assertEquals(board, boardFromMessage);
	}

}
