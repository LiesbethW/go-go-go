package test.controllers;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ClientHandler;
import network.Server;
import test.helperclasses.TestNetworkSetup;

public class ServerSideClientControllerTest {
	private Server server;
	private ClientHandler controller;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		server = network.server();
		controller = network.clientHandler();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(controller);
	}

}
