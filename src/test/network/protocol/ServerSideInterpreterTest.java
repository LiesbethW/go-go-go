package test.network.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import exceptions.ArgumentsMissingException;
import exceptions.GoException;
import exceptions.UnknownCommandException;
import network.Client;
import network.ClientCommunicator;
import network.Server;
import network.protocol.Message;
import network.protocol.ServerSideInterpreter;
import test.network.TestServer;

public class ServerSideInterpreterTest {
	private Server server;
	private Client client;
	private ClientCommunicator clientCommunicator;
	private ServerSideInterpreter interpreter;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		server = TestServer.newServer();
		client = new Client(InetAddress.getByName("localhost"), server.getPort());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
		}
		clientCommunicator = server.clients().get(0);
		interpreter = new ServerSideInterpreter(clientCommunicator);
	}
	
	@Test
	public void setUpTest() {
		assertNotNull(client);
		assertNotNull(server.clients());
		assertEquals(1, server.clients().size());
		assertNotNull(clientCommunicator);
		assertNotNull(interpreter);
	}
	
	@Test
	public void testDigest() {
		String message = "NEWPLAYER Jan";
		assertEquals(message, interpreter.digest(message).toString());
		
		String message2 = "QUIT";
		assertEquals(message2, interpreter.digest(message2).toString());
		
		String message3 = "CHAT Hee hallo, hoe gaat het? Heeft er iemand zin"
				+ "om een spelletje Go met mij te spelen?";
	}
	
	@Test
	public void testCommandPartOfProtocol() {
		// TODO
	}
	
	@Test(expected = UnknownCommandException.class)
	public void testCommandNotPartOfProtocol() throws UnknownCommandException {
		
	}
	
	@Test
	public void testCheckFormat() {
		
	}
	
	@Test(expected = ArgumentsMissingException.class)
	public void testMissingArguments() throws ArgumentsMissingException {
		
	}
	
	@Test
	public void testExceptionMessage() {
		Message exceptionMessage;
		try {
			throw new ArgumentsMissingException();
		} catch (GoException e) {
			exceptionMessage = interpreter.exceptionMessage(e);
		}
		assertEquals("FAILURE ArgumentsMissing", exceptionMessage.toString());
	}

}
