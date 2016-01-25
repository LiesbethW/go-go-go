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

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Presenter;
import test.network.TestNetworkSetup;
public class ChallengeTest {
	private TestNetworkSetup network;
	private Server server;
	private Client client1;
	private ClientHandler handler1;
	private String name1 = "Jan";
	
	private Client client2;
	private ClientHandler handler2;
	private String name2 = "Piet";
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		network = TestNetworkSetup.newNetwork();
		server = network.server();
		client1 = network.client();
		client2 = network.addClient();
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		handler2 = server.findClientByName(name2);
	}
	
	public void letClientsSendNames() {
		client1.send(Presenter.newPlayer(name1));
		client2.send(Presenter.newPlayer(name2));
		SystemTestSuite.waitForProcessing();
	}
	
	public void sendChallengeOption(Client client) {
		client.send(Presenter.options(new ArrayList<String>(
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
		sendChallengeOption(client1);
		assertTrue(handler1.canChallenge());
		assertFalse(handler2.canChallenge());
		
		// Client1 tries to challenge client2: that does not work
		client1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		assertFalse(handler2.isChallenged());
		assertTrue(handler2.readyToPlay());
		
		// Client2 reports that it can challenge too.
		sendChallengeOption(client2);
		assertTrue(handler2.canChallenge());
		
		// Now client1 can send a challenge
		client1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
	}
	
	@Test
	public void testDenyingChallenge() {
		// Client1 sends a challenge to client2.
		sendChallengeOption(client1);
		sendChallengeOption(client2);
		client1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		
		// Client2 is challenged, client1 is waiting for response
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client1 can try to send challengedenied, that does not work
		client1.send(Presenter.challengeDenied());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client2 can deny the challenge
		client2.send(Presenter.challengeDenied());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());
	}

	@Test
	public void testAcceptingChallenge() {
		// Client1 sends a challenge to client2.
		sendChallengeOption(client1);
		sendChallengeOption(client2);
		client1.send(Presenter.challenge(handler2.name()));
		SystemTestSuite.waitForProcessing();
		
		// Client2 is challenged, client1 is waiting for response
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client1 can try to send challengeaccepted, that does not work
		client1.send(Presenter.challengeAccepted());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler2.isChallenged());
		assertTrue(handler1.waitingForChallengeResponse());
		
		// Client2 can deny the challenge
		client2.send(Presenter.challengeDenied());
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.canStartPlaying() || handler1.isPlaying());
		assertTrue(handler2.canStartPlaying() || handler2.isPlaying());
	}
	
}
