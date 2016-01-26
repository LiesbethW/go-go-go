package test.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import network.protocol.Message;
import network.protocol.Presenter;
import test.helperclasses.TestNetworkSetup;

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
		client1 = network.addDummyClient();
		client2 = network.addDummyClient();
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
		assertEquals(0, handler1.getExtensions().size());
		
		client1.send(Presenter.extensions(new ArrayList<String>(Arrays.asList(
				"CHAT", "CHALLENGE"))));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.getExtensions().contains("CHAT"));
		assertTrue(handler1.getExtensions().contains("CHALLENGE"));
		assertEquals(2, handler1.getExtensions().size());
	}
	
	@Test
	public void cannotSetRandomOptions() {
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		assertEquals(0, handler1.getExtensions().size());
		
		client1.send(Presenter.extensions(new ArrayList<String>(Arrays.asList(
				"CHAT", "RANDOM", "CHALENGE"))));
		SystemTestSuite.waitForProcessing();
		assertTrue(handler1.getExtensions().contains("CHAT"));
		assertFalse(handler1.getExtensions().contains("RANDOM"));
		assertFalse(handler1.getExtensions().contains("CHALENGE"));
		assertEquals(1, handler1.getExtensions().size());
	}
	
	@Test
	public void cannotSetOptionsWhenNameIsNotGiven() {
		handler1 = server.clients().get(0);
		client1.send(new Message("OPTIONS", "CHAT", "CHALLENGE"));
	}

}
