package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import controllers.ClientSideClientController;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import network.protocol.ClientSideInterpreter;
import network.protocol.Message;

public class Client extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientSideInterpreter interpreter;
	private ClientSideClientController controller;

	public Client(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		interpreter = new ClientSideInterpreter(this);
		controller = new ClientSideClientController(this);
	}
	
	public void run() {
		while(true) {
			String messageString;
			try {
				messageString = in.readLine();
				if (messageString == null) {
					throw new IOException();
				}
				handle(messageString);
			} catch(IOException e) {
				System.out.println("Could not read incoming messages.");
				shutdown();
			}
		}
	}
	
	public void handle(String messageString) {
		Message message = interpreter.digest(messageString);
		try {
			controller.digest(message);
		} catch (NotApplicableCommandException e) {
			handleException(e);
		}
		
	}
	
	public void handleException(GoException e) {
		send(interpreter.exceptionMessage(e));
	}
	
	public void send(Message message) {
		try {
			out.write(message.toString());
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}
	
	private void shutdown(){
		try {
			socket.close();
			in.close();
			out.close();			
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}
}
