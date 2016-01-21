package test.controllers;

import static network.protocol.Constants.MOVE;
import static network.protocol.Constants.NEWPLAYERACCEPTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientSideClientController;
import controllers.FSM;
import controllers.ServerSideClientController;
import controllers.states.clientside.NewClient;
import controllers.states.clientside.ReadyToPlay;
import exceptions.NotApplicableCommandException;
import network.Client;
import network.ClientCommunicator;
import network.Server;
import network.protocol.Message;
import test.network.TestNetworkSetup;

public class FSMTest {
	private FSM stateMachine;
	private FSM stateMachine2;
	private Client client;
	private ClientCommunicator clientCommunicator;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		Server server = network.server();
		client = network.client();
		clientCommunicator = network.clientCommunicator();
		stateMachine = new ClientSideClientController(client);
		stateMachine2 = new ServerSideClientController(clientCommunicator, server);
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(client);
		assertNotNull(stateMachine);
		assertNotNull(stateMachine2);
	}
	
	@Test
	public void testInitialState() {
		assertEquals(stateMachine.currentState().getClass(), (new NewClient()).getClass());
	}
	
	@Test
	public void testDigest() throws NotApplicableCommandException {
		String name = null;
		Message message = new Message(NEWPLAYERACCEPTED, name);
		assertEquals(stateMachine.currentState(), new NewClient());
		stateMachine.digest(message);
		assertEquals(stateMachine.currentState(), new ReadyToPlay());
	}
	
	@Test(expected = NotApplicableCommandException.class)
	public void testNotDigestable() throws NotApplicableCommandException {
		Message message = new Message(MOVE, new String[]{"3", "4"});
		assertEquals(new NewClient(), stateMachine.currentState());
		stateMachine.digest(message);		
	}

}
