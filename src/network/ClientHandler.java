package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import network.protocol.ClientState;

public class ClientHandler extends Thread {
	private Server server;
	private Socket socket;
	private ClientState clientState;
	private BufferedReader in;
	private BufferedWriter out;
	
	public ClientHandler(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		this.clientState = ClientState.NewClient;
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void run() {
		while (true) {
			String message;
			try {
				{
					message = in.readLine();
					
				} while (message != null);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				shutdown();
			}
		}
	}
	
	public ClientState state() {
		return clientState;
	}
	
	public void setState(ClientState state) {
		clientState = state;
	}
	
	private void shutdown() {
		server.removeHandler(this);
	}
	
}
