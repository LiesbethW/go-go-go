package test.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import controllers.ClientHandler;
import network.Client;
import network.Server;

public class TestNetworkSetup extends Thread {
	private Server server;
	private Client client;
	private ClientHandler clientHandler;
	
	public static Server newServer() {
		TestNetworkSetup testServer = new TestNetworkSetup();
		testServer.start();
		return testServer.server();
	}
	
	public static TestNetworkSetup newNetwork() throws UnknownHostException, IOException {
		TestNetworkSetup testServer = new TestNetworkSetup();
		testServer.start();
		testServer.client = new Client(InetAddress.getByName("localhost"), testServer.server().getPort());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
		}
		testServer.clientHandler = testServer.server.clients().get(0);
		return testServer;
	}
	
	public TestNetworkSetup() {
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
	
	public Client client() {
		return client;
	}
	
	public ClientHandler clientHandler() {
		return clientHandler;
	}

}
