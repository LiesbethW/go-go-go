package test.network;

import network.Server;

public class TestServer extends Thread {
	private Server server;
	
	public static Server newServer() {
		TestServer testServer = new TestServer();
		testServer.start();
		return testServer.server();
	}
	
	public TestServer() {
		server = new Server(0, 9);
	}
	
	public void run() {
		server.start();
		server.waitForConnectingClients();
	}
	
	public int getPort() {
		return server.getPort();
	}
	
	public Server server() {
		return server;
	}

}
