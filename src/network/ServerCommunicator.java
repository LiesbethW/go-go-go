package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import controllers.Client;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import exceptions.UnknownCommandException;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class ServerCommunicator extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private Client controller;
	private boolean alive;

	public ServerCommunicator(InetAddress host, int port, Client controller) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.controller = controller;
		alive = true;
	}
	
	public void run() {
		while(alive) {
			String messageString;
			try {
				messageString = in.readLine();
				if (messageString == null) {
					throw new IOException();
				}
				handle(messageString);
			} catch(IOException e) {
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
			alive = false;
			System.out.print("The connection to the server has closed. Shutting down the programm.");
			System.exit(0);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}
}
