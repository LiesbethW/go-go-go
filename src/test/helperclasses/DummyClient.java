package test.helperclasses;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import controllers.Client;
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
	
	public void process(Message message) {
		receivedInput.add(message.toString());
	}

}
