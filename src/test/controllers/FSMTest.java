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

import controllers.Client;
import controllers.FSM;
import controllers.states.clientside.NewClient;
import exceptions.NotApplicableCommandException;
import network.Server;
import network.ServerCommunicator;
import network.protocol.Message;
import test.helperclasses.TestNetworkSetup;

public class FSMTest {
	private FSM stateMachine;
	private FSM stateMachine2;
	private ServerCommunicator client;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		Server server = network.server();
		stateMachine = network.client();
		stateMachine2 = network.clientHandler();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(stateMachine);
		assertNotNull(stateMachine2);
	}
	
	@Test
	public void testInitialState() {
		assertEquals(stateMachine.currentState().getClass(), (new NewClient((Client) stateMachine)).getClass());
	}
	
	@Test
	public void testDigest() throws NotApplicableCommandException {
		String name = null;
		Message message = new Message(NEWPLAYERACCEPTED, name);
		assertTrue(((Client) stateMachine).newClient());
		stateMachine.digest(message);
		assertTrue(((Client) stateMachine).readyToPlay());
	}
	
	@Test(expected = NotApplicableCommandException.class)
	public void testNotDigestable() throws NotApplicableCommandException {
		Message message = new Message(MOVE, new String[]{"3", "4"});
		assertTrue(((Client) stateMachine).newClient());
		stateMachine.digest(message);
	}

}
