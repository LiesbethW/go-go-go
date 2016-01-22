package test.system;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Presenter;
import test.network.TestNetworkSetup;

public class ChatTest {
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
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(client1);
		assertNotNull(client2);
	}
	
	@Test
	public void testChat() {
		letClientsSendNames();
		handler1 = server.findClientByName(name1);
		handler2 = server.findClientByName(name2);
		client1.send(Presenter.chat("Hallo Piet, hoe is het?"));
	}

}
