package test.network.protocol;

import static network.protocol.Constants.ARGUMENTSMISSING;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import exceptions.ArgumentsMissingException;
import exceptions.GoException;
import exceptions.InvalidMoveException;
import game.Board;
import game.Stone;
import network.protocol.CommandSet;
import network.protocol.Message;
import network.protocol.Presenter;

public class PresenterTest {

	@Test
	public void exceptionMessageTest() {
		Message exceptionMessage;
		try {
			throw new ArgumentsMissingException();
		} catch (GoException e) {
			assertEquals(ARGUMENTSMISSING, CommandSet.exceptionCommand(e));
			exceptionMessage = Presenter.exceptionMessage(e);
		}
		assertEquals("FAILURE ArgumentsMissing", exceptionMessage.toString());		
	}
	
	@Test
	public void gameStartMessageTest() {
		Message message = Presenter.gameStart("Jan", 9, Stone.BLACK);
		assertEquals("GAMESTART Jan 9 BLACK", message.toString());
	}
	
	@Test
	public void boardMessageTest() throws InvalidMoveException {
		Board board = new Board(3);
		board.layStone(Stone.BLACK, 0, 0);
		board.layStone(Stone.WHITE, 1, 0);
		Message message = Presenter.boardMessage(board);
		assertEquals("BOARD BEEWEEEEE 0 0", message.toString());
	}
	
	@Test
	public void serverMoveTest() {
		Message message = Presenter.serverMove(Stone.BLACK, 2, 6);
		assertEquals("MOVE BLACK 2 6", message.toString());
	}
	
	@Test
	public void clientMoveTest() {
		Message message = Presenter.clientMove(9, 3);
		assertEquals("MOVE 9 3", message.toString());
	}
	
	@Test
	public void serverPassTest() {
		Message message = Presenter.serverPass(Stone.WHITE);
		assertEquals("MOVE WHITE PASS", message.toString());
	}
	
	@Test
	public void clientPassTest() {
		Message message = Presenter.clientPass();
		assertEquals("MOVE PASS", message.toString());
	}

}
