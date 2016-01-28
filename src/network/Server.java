package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import controllers.ClientHandler;
import controllers.CommandHandler;
import controllers.GameController;
import exceptions.BoardSizeException;
import exceptions.CorruptedStateException;
import network.protocol.Message;
import network.protocol.Presenter;

public class Server extends Thread {
	private static final String USAGE 
		= "usage: " + Server.class.getName() + " <port> [<boardsize>]";	
	public static final int BOARDSIZE = 9;
	public static final int MIN_BOARDSIZE = 3;
	public static final int MAX_BOARDSIZE = 19; 
	
	public static final List<String> EXTENSIONS = new ArrayList<String>(
			Arrays.asList(Presenter.chatOpt(), Presenter.challengeOpt()));
	
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
	private CommandHandler commandHandler;
	
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
		commandHandler = new CommandHandler(this, commandQueue);
	}
	
	public void run() {
		commandHandler.start();
		while(true) {
			try {
				checkForGamesToStart();
				checkForDeadClientsToRemove();
			} catch (CorruptedStateException e) {
				System.err.println(e.getMessage());
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				
			}
		}
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
				System.out.println("A new client has applied.");
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
	
	public List<ClientHandler> clientsThatCanChat() {
		return clients().stream().filter(c -> c.canChat() && !c.isPlaying()).collect(Collectors.toList());
	}
	
	public List<ClientHandler> clientsThatCanBeChallenged() {
		return clients().stream().filter(c -> c.readyToPlay() && c.canChallenge()).collect(Collectors.toList());
	}
	
	public List<ClientHandler> clientsWaitingForOpponent() {
		return clients().stream().filter(c -> c.waitingForOpponent()).collect(Collectors.toList());
	}
	
	public List<ClientHandler> clientsThatCanStartPlaying() {
		return clients().stream().filter(c -> c.canStartPlaying()).collect(Collectors.toList());
	}
	
	public List<ClientHandler> deadClients() {
		return clients().stream().filter(c -> c.dead()).collect(Collectors.toList());
	}
	
	/**
	 * Find clientHandler by client name.
	 * @param name
	 * @return The clientHandler of a client that
	 * has this name, if there is no client by
	 * that name, return null.
	 */
	public ClientHandler findClientByName(String name) {
		if (clients.isEmpty()) {
			return null;
		}
		return clients.stream().filter(c -> !(c.name() == null) 
				&& c.name().equals(name)).findAny().orElse(null);
	}
	
	public void checkForGamesToStart() throws CorruptedStateException {
		if (clientsThatCanStartPlaying().size() >= 2) {
			ClientHandler client1 = clientsThatCanStartPlaying().get(0);
			ClientHandler client2 = client1.getOpponent();
			if (client2.getOpponent() != client1) {
				throw new CorruptedStateException();
			} else {
				startNewGame(client1, client2);
				removeClient(client1);
				removeClient(client2);
			}
		}
	}
	
	public void startNewGame(ClientHandler client1, ClientHandler client2) {
		GameController gameController = new GameController(client1, client2, this, BOARDSIZE);
		client1.setGameController(gameController);
		client2.setGameController(gameController);
		gameController.start();
	}
	
	public void endGame(ClientHandler client1, ClientHandler client2) {
		addClient(client1);
		addClient(client2);
	}
	
	public void checkForDeadClientsToRemove() {
		if (deadClients().size() > 0) {
			for (ClientHandler client : deadClients()) {
				disconnectClient(client);
			}
		}
	}
	
	public void disconnectClient(ClientHandler client) {
		client.clientCommunicator().shutdown();
		System.out.printf("Client %s has disconnected.%n", client.name());
		removeClient(client);
	}

}