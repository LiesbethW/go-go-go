package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import controllers.ClientHandler;
import exceptions.BoardSizeException;
import network.protocol.Message;

public class Server extends Thread {
	private static final String USAGE 
		= "usage: " + Server.class.getName() + " <port> [<boardsize>]";	
	public static final Boolean CHAT = false;
	public static final Boolean CHALLENGE = false;
	public static final Boolean OBSERVER = false;
	public static final Boolean COMPUTERPLAYER = false;
	public static final int BOARDSIZE = 9;
	public static final int MIN_BOARDSIZE = 3;
	public static final int MAX_BOARDSIZE = 19; 
	
	/**
	 * Start a new Go! server.
	 * @param port (A positive integer below 49151,
	 * try not to use a reserved port.) and optionally
	 * the board size (between 3 and 19).
	 */
	public static void main(String[] args) {
		// Quit if there is a wrong number of arguments given
		if (args.length < 1 || args.length > 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		int boardSize = BOARDSIZE;
		if (args.length == 2) {
			try {
				boardSize = Integer.parseInt(args[1]);
				if (boardSize < MIN_BOARDSIZE || boardSize > MAX_BOARDSIZE) {
					throw new BoardSizeException(boardSize);
				}
			} catch (NumberFormatException e) {
				System.out.println(USAGE);
				System.out.println("ERROR: ");
				System.exit(0);
			} catch (BoardSizeException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		
		try {
			Server server = new Server(Integer.parseInt(args[0]), boardSize);
			server.start();
			server.waitForConnectingClients();
		} catch (NumberFormatException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: port " + args[0] + " is not an integer.");
    		System.exit(0);
		}

		
	}
	
	
	
	
	private int port;
	private ServerSocket serverSocket;
	private List<ClientHandler> clients;
	private int boardSize;
	private ConcurrentLinkedQueue<Message> commandQueue;
	
	public Server(int port, int boardSize) {
		this.port = port;
		this.boardSize = boardSize;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("ERROR: could not create a server socket on port " + port);
			System.exit(0);
		}
		clients = new ArrayList<ClientHandler>();
		commandQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public void run() {
		while (true) {
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
		}
	}
	
	public void process(Message message) {
		// TODO
		if (!clients().contains(message.author())) {
			System.out.println("Client has been disconnected");
		}
		System.out.println(message);
	}
	
	public void enqueue(Message message) {
		commandQueue.add(message);
	}
	
	public int getPort() {
		return serverSocket.getLocalPort();
	}
	
	public void waitForConnectingClients() {
		while(true) {
			try {
				ClientHandler newClient = new ClientHandler(this, serverSocket.accept());
				addClient(newClient);
			} catch (IOException e) {
				System.out.println("An error occured while waiting for a connection");
				System.exit(0);
			}
		}
	}
	
	public List<ClientHandler> clients() {
		return clients;
	}
	
	public void addClient(ClientHandler client) {
		clients.add(client);
	}
	
	public void removeClient(ClientHandler client) {
		clients.remove(client);
	}

}