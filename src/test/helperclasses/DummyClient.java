package test.helperclasses;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import controllers.Client;
import exceptions.NotApplicableCommandException;
import network.protocol.Message;

public class DummyClient extends Client {
	private List<String> receivedInput;
	
	public DummyClient(InetAddress host, int port) throws IOException {
		super(host, port);
		receivedInput = new ArrayList<String>();
	}
	
	public DummyClient(InetAddress host, int port, List<String> inputStack) throws IOException {
		super(host, port);
		receivedInput = inputStack;
	}
	
	@Override
	public void process(Message message) throws NotApplicableCommandException {
		receivedInput.add(message.toString());
		
	}
	
	@Override
	public void enqueue(Message message) {
		receivedInput.add(message.toString());
		
	}

}
