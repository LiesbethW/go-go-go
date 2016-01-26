package test.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Presenter;
import test.helperclasses.TestNetworkSetup;

public class ApplyingNewClientTest {
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
		handler1 = network.clientHandler();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(client1);
		assertNotNull(handler1);
	}
	
	@Test
	public void testInitialState() {
		assertTrue(handler1.newClient());
		assertNull(handler1.name());
		assertNull(server.findClientByName(name1));
	}
	
	@Test
	public void testSendName() {
		client1.send(Presenter.newPlayer(name1));
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			
		}
		assertEquals(name1, handler1.name());
		assertEquals(handler1, server.findClientByName(name1));
		assertFalse(handler1.newClient());
		assertTrue(handler1.readyToPlay());
	}
	
	@Test
	public void testSendingTakenName() throws IOException {
		//Client 1 sends name
		client1.send(Presenter.newPlayer(name1));
		SystemTestSuite.waitForProcessing();
		
		assertEquals(name1, handler1.name());
		
		//Make second client
		client2 = network.addClient();
		client2.send(Presenter.newPlayer(name1));
		SystemTestSuite.waitForProcessing();
		
		//Client 2 tries to send the same name
		handler2 = server.clients().get(1);		
		assertNotEquals(handler2.name(), name1);
		assertTrue(handler2.newClient());
		
		//Client 2 does a second trial with another name
		client2.send(Presenter.newPlayer(name2));
		SystemTestSuite.waitForProcessing();
		assertEquals(handler2, server.findClientByName(name2));
		assertTrue(handler2.readyToPlay());
	}

}
