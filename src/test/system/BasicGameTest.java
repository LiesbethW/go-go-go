package test.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.Client;
import controllers.ClientHandler;
import game.Stone;
import network.Server;
import network.protocol.Presenter;
import test.helperclasses.TestNetworkSetup;

public class BasicGameTest {
	private TestNetworkSetup network;
	private Server server;
	private Client client1;
	private ClientHandler handler1;
	private String name1 = "Jan";
	private List<String> receivedInput1;
	
	private Client client2;
	private ClientHandler handler2;
	private String name2 = "Piet";
	private List<String> receivedInput2;
	
	private Client client3;
	private ClientHandler handler3;
	private String name3 = "Els";
	
	private Client black;
	private Client white;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		network = TestNetworkSetup.newNetwork();
		server = network.server();
		receivedInput1 = new ArrayList<String>();
		receivedInput2 = new ArrayList<String>();
		client1 = network.addDummyClient(receivedInput1);
		client2 = network.addDummyClient(receivedInput2);
		client3 = network.addClient();
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		handler2 = server.findClientByName(name2);
		handler3 = server.findClientByName(name3);
	}
	
	public void letClientsSendNames() {
		client1.send(Presenter.newPlayer(name1));
		client2.send(Presenter.newPlayer(name2));
		client3.send(Presenter.newPlayer(name3));
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
	}
	
	public void startGame() {
		client1.send(Presenter.play());
		client2.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
	}
	
	public String lastMessage(Client client) {
		if (client.equals(client1)) {
			return receivedInput1.get(receivedInput1.size() - 1);
		} else if (client.equals(client2)) {
			return receivedInput2.get(receivedInput2.size() - 1);
		} else {
			return null;
		}
	}
	
	public void setColors(Client black, Client white) {
		this.black = black;
		this.white = white;
	}
	
	public Stone getColor(Client client) {
		if (client.equals(black)) {
			return Stone.BLACK;
		} else if (client.equals(white)) {
			return Stone.WHITE;
		} else {
			return null;
		}
	}
	
	public Client getClient(Stone color) {
		if (color.equals(Stone.BLACK)) {
			return black;
		} else if (color.equals(Stone.WHITE)) {
			return white;
		} else {
			return null;
		}
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(handler1);
		assertNotNull(handler2);
	}
	
	@Test
	public void testGameStart() {
		// Before playing a game the clients are in the servers client list
		assertTrue(server.clients().contains(handler1));
		assertTrue(server.clients().contains(handler2));
		
		client1.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.waitingForOpponent());
		
		client2.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.isPlaying());
		assertTrue(handler2.isPlaying());
		
		// When the players have started a game, they are no longer
		// in the servers client list.
		assertFalse(server.clients().contains(handler1));
		assertFalse(server.clients().contains(handler2));
		
		// After one of the clients ends the game, both clients are
		// back on the servers client list and ready to play a new
		// game
		client1.send(Presenter.stopGame());
		SystemTestSuite.waitForProcessing();
		assertTrue(server.clients().contains(handler1));
		assertTrue(server.clients().contains(handler2));
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());		
	}
	
	@Test
	public void cancelWaitingForOpponentTest() {
		client3.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler3.waitingForOpponent());
		SystemTestSuite.waitForProcessing();
		assertTrue(client3.waitingForOpponent());
		
		client3.send(Presenter.cancel());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler3.readyToPlay());
		SystemTestSuite.waitForProcessing();
		assertTrue(client3.readyToPlay());
	}
	
	@Test
	public void playBasicGame() {
		startGame();
		if (lastMessage(client1).endsWith(Stone.BLACK.name())) {
			black = client1;
			white = client2;
		} else if (lastMessage(client2).endsWith(Stone.BLACK.name())) {
			black = client2;
			white = client1;
		} else {
			System.err.println("It seems that the GAMESTART message has not been received yet.");
		}
		// Black sends a valid move
		black.send(Presenter.clientMove(3, 2));
		SystemTestSuite.waitForProcessing();
		assertTrue(lastMessage(black).equals((Presenter.serverMove(getColor(black), 3, 2)).toString()));

		// White sends and invalid move
		white.send(Presenter.clientMove(10, 1));
		SystemTestSuite.waitForProcessing();
		assertTrue(lastMessage(white).startsWith("FAILURE InvalidMove"));
		
		// White sends a new valid move
		white.send(Presenter.clientMove(3,3));
		SystemTestSuite.waitForProcessing();
		assertTrue(lastMessage(black).equals((Presenter.serverMove(getColor(white), 3, 3)).toString()));

		// Black sends a pass
		black.send(Presenter.clientPass());
		SystemTestSuite.waitForProcessing();
		assertTrue(lastMessage(white).equals((Presenter.serverPass(getColor(black))).toString()));
		
		// White sends a pass
		white.send(Presenter.clientPass());
		
		// Both receive gameover
		SystemTestSuite.waitForProcessing();
		assertTrue(lastMessage(black).startsWith(Presenter.GAMEOVER));
		assertTrue(lastMessage(white).startsWith(Presenter.GAMEOVER));
		assertFalse(handler1.isPlaying());
		assertFalse(handler2.isPlaying());
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());
	}


}
