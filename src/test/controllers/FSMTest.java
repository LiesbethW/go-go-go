package test.controllers;

import static network.protocol.Constants.MOVE;
import static network.protocol.Constants.NEWPLAYERACCEPTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientSideClientController;
import controllers.FSM;
import controllers.states.clientside.NewClient;
import exceptions.NotApplicableCommandException;
import network.Client;
import network.Server;
import network.protocol.Message;
import test.network.TestNetworkSetup;

public class FSMTest {
	private FSM stateMachine;
	private FSM stateMachine2;
	private Client client;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		Server server = network.server();
		client = network.client();
		stateMachine = new ClientSideClientController(client);
		stateMachine2 = network.clientHandler();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(client);
		assertNotNull(stateMachine);
		assertNotNull(stateMachine2);
	}
	
	@Test
	public void testInitialState() {
		assertEquals(stateMachine.currentState().getClass(), (new NewClient((ClientSideClientController) stateMachine)).getClass());
	}
	
	@Test
	public void testDigest() throws NotApplicableCommandException {
		String name = null;
		Message message = new Message(NEWPLAYERACCEPTED, name);
		assertTrue(((ClientSideClientController) stateMachine).newClient());
		stateMachine.digest(message);
		assertTrue(((ClientSideClientController) stateMachine).readyToPlay());
	}
	
	@Test(expected = NotApplicableCommandException.class)
	public void testNotDigestable() throws NotApplicableCommandException {
		Message message = new Message(MOVE, new String[]{"3", "4"});
		assertTrue(((ClientSideClientController) stateMachine).newClient());
		stateMachine.digest(message);
	}

}
