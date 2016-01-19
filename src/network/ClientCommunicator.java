package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import exceptions.GoException;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.ServerSideInterpreter;

public class ClientCommunicator extends Thread {
	private Server server;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Interpreter interpreter;
	
	public ClientCommunicator(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		interpreter = new ServerSideInterpreter(this);
	}
	
	public void run() {
		while (true) {
			String message;
			try {
				{
					message = in.readLine();
				} while (message != null);
				handle(message);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				shutdown();
			}
		}
	}
	
	public void handle(String message) {
		Message event = interpreter.digest(message);
		
		System.out.println(event);
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
	
}
