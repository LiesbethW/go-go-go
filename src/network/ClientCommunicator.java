package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import controllers.ClientHandler;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import exceptions.UnknownCommandException;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class ClientCommunicator extends Thread {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientHandler controller;
	
	public ClientCommunicator(ClientHandler controller, Socket socket) throws IOException {
		this.socket = socket;
		this.controller = controller;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void run() {
    	String messageString;
        try {
        	while ((messageString = in.readLine()) != null) {
        		handle(messageString);
        	}
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        	shutdown();
        }
	}
	
	public void handle(String messageString) {
		System.out.println("ClientCommunicator: received " + messageString);
		try {
			Message message = Interpreter.digest(messageString);
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
	
	public void shutdown() {
		try {
			socket.close();
			in.close();
			out.close();			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		controller.kill();
	}
	
}
