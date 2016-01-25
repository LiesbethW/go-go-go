package test.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Message;
import network.protocol.Presenter;
import test.network.TestNetworkSetup;

public class SettingOptionsTest {
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
	}
	
	public void letClientsSendNames() {
		client1.send(Presenter.newPlayer(name1));
		client2.send(Presenter.newPlayer(name2));
		SystemTestSuite.waitForProcessing();
	}
	
	@Test
	public void setOptions() {
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		assertEquals(0, handler1.getOptions().size());
		
		client1.send(new Message("OPTIONS", "CHAT", "CHALLENGE"));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.getOptions().contains("CHAT"));
		assertTrue(handler1.getOptions().contains("CHALLENGE"));
	}
	
	@Test
	public void cannotSetRandomOptions() {
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		assertEquals(0, handler1.getOptions().size());
		
		client1.send(new Message("OPTIONS", "CHAT", "RANDOM", "CHALENGE"));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.getOptions().contains("CHAT"));
		assertFalse(handler1.getOptions().contains("RANDOM"));
		assertFalse(handler1.getOptions().contains("CHALENGE"));
		assertEquals(1, handler1.getOptions().size());
	}
	
	@Test
	public void cannotSetOptionsWhenNameIsNotGiven() {
		handler1 = server.clients().get(0);
		client1.send(new Message("OPTIONS", "CHAT", "CHALLENGE"));
	}

}
