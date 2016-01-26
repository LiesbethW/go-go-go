package test.system;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Client;
import network.Server;
import network.protocol.Presenter;
import test.helperclasses.TestNetworkSetup;

public class ChatTest {
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
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		network = TestNetworkSetup.newNetwork();
		server = network.server();
		receivedInput1 = new ArrayList<String>();
		receivedInput2 = new ArrayList<String>();
		client1 = network.addDummyClient(receivedInput1);
		client2 = network.addDummyClient(receivedInput2);
		letClientsSendNames();
	}
	
	public void letClientsSendNames() {
		client1.send(Presenter.newPlayer(name1));
		client2.send(Presenter.newPlayer(name2));
		SystemTestSuite.waitForProcessing();
		SystemTestSuite.waitForProcessing();
	}
	
	public void letClientsSendOptions() {
		client1.send(Presenter.extensions(new ArrayList<String>(Arrays.asList(
				"CHAT", "CHALLENGE"))));
		client2.send(Presenter.extensions(new ArrayList<String>(Arrays.asList(
				"CHAT", "CHALLENGE"))));
		SystemTestSuite.waitForProcessing();
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
		letClientsSendOptions();
		String myChatMessage = "Hee Hallo, wil je met me spelen?";
		client1.send(Presenter.chat(myChatMessage));
		SystemTestSuite.waitForProcessing();
		assertTrue(receivedInput2.size() > 0);
		assertTrue(receivedInput2.get(receivedInput2.size() - 1).endsWith(myChatMessage));
		assertTrue(receivedInput1.get(receivedInput1.size() - 1).endsWith(myChatMessage));
	}
	
	@Test
	public void testCannotChatWithoutSettingOption() {
		// Client 1 has not sent that it can chat: it receives a FAILURE NotApplicableCommand
		String myChatMessage = "Hee Hallo, wil je met me spelen?";
		client1.send(Presenter.chat(myChatMessage));
		SystemTestSuite.waitForProcessing();
		assertFalse(receivedInput2.get(receivedInput2.size() - 1).endsWith(myChatMessage));
		assertTrue(receivedInput1.get(receivedInput1.size() - 1).startsWith("FAILURE NotApplicableCommand"));
	}
	
	@Test
	public void testChatDoesNotChangeState() {
		handler1 = server.findClientByName(name1);
		handler2 = server.findClientByName(name2);
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());
		client1.send(Presenter.chat("Hallo Piet, hoe is het?"));
		assertTrue(handler1.readyToPlay());
		assertTrue(handler2.readyToPlay());
	}

}
