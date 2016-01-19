package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import network.protocol.ClientSideInterpreter;
import network.protocol.Message;

public class Client extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientSideInterpreter interpreter;

	public Client(InetAddress host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		interpreter = new ClientSideInterpreter(this);
	}
	
	public void run() {
		while(true) {
			String message;
			try {
				message = in.readLine();
				if (message == null) {
					throw new IOException();
				}
				handle(message);
			} catch(IOException e) {
				System.out.println("Could not read incoming messages.");
				shutdown();
			}
		}
	}
	
	public void handle(String message) {
		Message event = interpreter.digest(message);
		
		// TODO
		System.out.println(event);
	}
	
	private void shutdown(){
		try {
			socket.close();
			in.close();
			out.close();			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
