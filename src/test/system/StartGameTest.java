package test.system;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Presenter;
import test.network.TestNetworkSetup;

public class StartGameTest {
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
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(handler1);
		assertNotNull(handler2);
	}
	
	@Test
	public void testGameStart() {
		client1.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.waitingForOpponent());
		
		client2.send(Presenter.play());
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.isPlaying());
		assertTrue(handler2.isPlaying());
	}

}
