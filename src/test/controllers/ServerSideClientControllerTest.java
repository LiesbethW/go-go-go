package test.controllers;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import controllers.ServerSideClientController;
import network.Server;
import test.network.TestNetworkSetup;

public class ServerSideClientControllerTest {
	private Server server;
	private ServerSideClientController controller;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		server = network.server();
		controller = network.clientCommunicator().controller();
	}
	
	@Test
	public void testSetUp() {
		assertNotNull(server);
		assertNotNull(controller);
	}

}
