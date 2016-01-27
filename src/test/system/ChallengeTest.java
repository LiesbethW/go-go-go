package test.system;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import controllers.Client;
import controllers.ClientHandler;
import network.Server;
import network.protocol.Presenter;
import test.helperclasses.TestNetworkSetup;

public class ChallengeTest {
	private TestNetworkSetup network;
	private Server server;
	private Client dumbClient1;
	private ClientHandler handler1;
	private String name1 = "Jan";
	
	private Client dumbClient2;
	private ClientHandler handler2;
	private String name2 = "Piet";
	
	private Client client3;
	private ClientHandler handler3;
	private String name3 = "Els";
	
	private Client client4;
	private ClientHandler handler4;
	private String name4 = "Ineke";
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		network = TestNetworkSetup.newNetwork();
		server = network.server();
		dumbClient1 = network.addDummyClient();
		dumbClient2 = network.addDummyClient();
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		handler2 = server.findClientByName(name2);
	}
	
	public void letClientsSendNames() {
		dumbClient1.send(Presenter.newPlayer(name1));
		dumbClient2.send(Presenter.newPlayer(name2));
		SystemTestSuite.waitForProcessing();
	}
	
	public void sendChallengeOption(Client client) {
		client.send(Presenter.extensions(new ArrayList<String>(
				Arrays.asList(Presenter.challengeOpt()))));
		SystemTestSuite.waitForProcessing();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(handler1);
		assertNotNull(handler2);
	}
	
	@Test
	public void testChallenging(){
		// Client1 reports that it can challenge
		sendChallengeOption(dumbClient1);
		assertTrue(handler1.canChallenge());
		assertFalse(handler2.canChallenge());
		
		// Client1 tries to challenge client2: that does not work
		dumbClient1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		assertFalse(handler2.isChallenged());
		assertTrue(handler2.readyToPlay());
		
		// Client2 reports that it can challenge too.
		sendChallengeOption(dumbClient2);
		assertTrue(handler2.canChallenge());
		
		// Now client1 can send a challenge
		dumbClient1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
	}
	
	@Test
	public void testDenyingChallenge() {
		// Client1 sends a challenge to client2.
		sendChallengeOption(dumbClient1);
		sendChallengeOption(dumbClient2);
		dumbClient1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		
		// Client2 is challenged, client1 is waiting for response
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client1 can try to send challengedenied, that does not work
		dumbClient1.send(Presenter.challengeDenied());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client2 can deny the challenge
		dumbClient2.send(Presenter.challengeDenied());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());
	}

	@Test
	public void testAcceptingChallenge() {
		// Client1 sends a challenge to client2.
		sendChallengeOption(dumbClient1);
		sendChallengeOption(dumbClient2);
		dumbClient1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		
		// Client2 is challenged, client1 is waiting for response
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client1 can try to send challengeaccepted, that does not work
		dumbClient1.send(Presenter.challengeAccepted());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client2 can accept the challenge
		dumbClient2.send(Presenter.challengeAccepted());
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.canStartPlaying() || handler1.isPlaying());
		assertTrue(handler2.canStartPlaying() || handler2.isPlaying());
	}
	
	@Test
	public void testCancellingChallenge() throws UnknownHostException, IOException {
		client3 = network.addClient();
		client3.send(Presenter.newPlayer(name3));
		client4 = network.addClient();
		client4.send(Presenter.newPlayer(name4));
		SystemTestSuite.waitForProcessing();
		handler3 = server.findClientByName(name3);
		handler4 = server.findClientByName(name4);
		
		// Client1 sends a challenge to client2.
		sendChallengeOption(client3);
		sendChallengeOption(client4);
		client3.send(Presenter.challenge(handler4.name()));
		SystemTestSuite.waitForProcessing();
		
		// Client2 is challenged, client1 is waiting for response
		assertTrue(handler4.isChallenged());
		assertTrue(client4.isChallenged());
		assertTrue(handler3.waitingForChallengeResponse());
		assertTrue(client3.waitingForChallengeResponse());
		
		// Client1 can send cancel
		client3.send(Presenter.cancel());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler3.readyToPlay());
		assertTrue(handler4.readyToPlay());
		assertTrue(client3.readyToPlay());
		assertTrue(client4.readyToPlay());
	}
	
}
