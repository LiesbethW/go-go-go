package test.network.protocol;

import static network.protocol.Constants.ARGUMENTSMISSING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import exceptions.ArgumentsMissingException;
import exceptions.GoException;
import exceptions.UnknownCommandException;
import network.Client;
import network.ClientCommunicator;
import network.Server;
import network.protocol.CommandSet;
import network.protocol.Message;
import network.protocol.ServerSideInterpreter;
import test.network.TestNetworkSetup;

public class ServerSideInterpreterTest {
	private Server server;
	private Client client;
	private ClientCommunicator clientCommunicator;
	private ServerSideInterpreter interpreter;
	
	@Before
	public void setUp() throws UnknownHostException, IOException {
		TestNetworkSetup network = TestNetworkSetup.newNetwork();
		server = network.server();
		client = network.client();
		clientCommunicator = network.clientCommunicator();
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
		assertEquals(message3, interpreter.digest(message3).toString());
	}
	
	@Test
	public void testCommandPartOfProtocol() throws UnknownCommandException {
		assertTrue(interpreter.commandPartOfProtocol("PLAY"));
		assertTrue(interpreter.commandPartOfProtocol("WHITE"));
		assertTrue(interpreter.commandPartOfProtocol("UnknownCommand"));
	}
	
	@Test(expected = UnknownCommandException.class)
	public void testCommandNotPartOfProtocol() throws UnknownCommandException {
		assertFalse(interpreter.commandPartOfProtocol("Hellloooo"));
	}
	
	@Test
	public void printException() {
		try {
			throw new ArgumentsMissingException();
		} catch (GoException e) {
			assertEquals("exceptions.ArgumentsMissingException", e.toString());
			assertTrue(e.toString().contains("ArgumentsMissing"));
			assertTrue(CommandSet.errorSet().stream().anyMatch(s -> e.toString().contains(s)));
			assertEquals("ArgumentsMissing", 
					CommandSet.errorSet().stream().filter(s -> e.toString().contains(s)).
					findFirst().get());
		}
	}
	
	@Test
	public void testExceptionMessage() {
		Message exceptionMessage;
		try {
			throw new ArgumentsMissingException();
		} catch (GoException e) {
			assertEquals(ARGUMENTSMISSING, CommandSet.exceptionCommand(e));
			exceptionMessage = interpreter.exceptionMessage(e);
		}
		assertEquals("FAILURE ArgumentsMissing", exceptionMessage.toString());
	}

}
