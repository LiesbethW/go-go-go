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
import exceptions.UnknownCommandException;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class Client extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientSideClientController controller;

	public Client(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
				System.out.println("Client received: " + messageString);
				handle(messageString);
			} catch(IOException e) {
				System.out.println("Could not read incoming messages.");
				shutdown();
			}
		}
	}
	
	public void handle(String messageString) {
		try {
			Message message = Interpreter.digest(messageString);
			controller.process(message);
		} catch (UnknownCommandException | NotApplicableCommandException e) {
			handleException(e);
		}
		
	}
	
	public void handleException(GoException e) {
		System.err.println(Presenter.exceptionMessage(e));
	}
	
	public void send(Message message) {
		try {
			System.out.println("Client sends: " + message.toString());
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
