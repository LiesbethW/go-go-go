package test.network;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import network.Server;

public class ServerTest {
	private int port = 1929;
	
	@Test
	public void setUp() {
		Server server = new Server(port, Server.BOARDSIZE);
		assertNotNull(server);
	}

}
