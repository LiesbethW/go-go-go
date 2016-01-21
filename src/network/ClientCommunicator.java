package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import controllers.ServerSideClientController;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import exceptions.UnknownCommandException;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;
import network.protocol.ServerSideInterpreter;
import network.protocol.StaticInterpreter;

public class ClientCommunicator extends Thread {
	private Server server;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Interpreter interpreter;
	private ServerSideClientController controller;
	
	public ClientCommunicator(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		interpreter = new ServerSideInterpreter();
		controller = new ServerSideClientController(this, server);
	}
	
	public void run() {
		while (true) {
			String messageString;
			try {
				{
					messageString = in.readLine();
				} while (messageString != null);
				handle(messageString);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				shutdown();
			}
		}
	}
	
	public void handle(String messageString) {
		try {
			Message message = StaticInterpreter.digest(messageString);
			controller.process(message);
		} catch (UnknownCommandException | NotApplicableCommandException e) {
			handleException(e);
		}
		
	}
	
	public void handleException(GoException e) {
		send(Presenter.exceptionMessage(e));
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
	
	public String name() {
		return controller.name();
	}
	
	private void shutdown() {
		try {
			socket.close();
			in.close();
			out.close();			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		server.removeClient(this);
	}
	
	// For testing purposes
	public ServerSideClientController controller() {
		return controller;
	}
	
}
