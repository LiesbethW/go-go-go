package controllers;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.ArgumentsMissingException;
import exceptions.CorruptedAuthorException;
import exceptions.GoException;
import exceptions.NotSupportedCommandException;
import exceptions.OtherPlayerCannotChatException;
import game.Game;
import game.HumanPlayer;
import game.Player;
import network.Server;
import network.protocol.Constants;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class GameController extends Thread implements Constants {
	private Server server;
	
	private Game game;
	private HumanPlayer player1;
	private HumanPlayer player2;
	
	private ConcurrentLinkedQueue<Message> commandQueue;
	private HashMap<String, Command> methodMap;
	
	/**
	 * Create a new game controller.
	 * @param client1
	 * @param client2
	 * @param server
	 * @param boardSize
	 */
	public GameController(ClientHandler client1, ClientHandler client2, Server server, int boardSize) {
		commandQueue = new ConcurrentLinkedQueue<Message>();
		player1 = new HumanPlayer(client1);
		player2 = new HumanPlayer(client2);
		game = new Game(player1, player2, boardSize);
		this.server = server;
	}
	
	/**
	 * Play the game: send gamestart message to players,
	 * handle commands while the game is playing. Send
	 * game over when the game ends and hand the clients
	 * back to the server.
	 */
	public void run() {
		System.out.println("Game has started");
		initializeMethodMap();
		sendGameStart();
		while (!game.gameOver()) {
			checkClientsAreLiving();
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}	
		sendGameOver();
		server.endGame(player1.client(), player2.client());
	}
	
	/**
	 * Select the method for a certain command and run it.
	 * @param message
	 */
	public void process(Message message) {
		System.out.println("GameController: processing " + message.toString());
		try {
			if (!methodMap.containsKey(message.command())) {
				System.err.printf("CommandHandler tries to process %s, but that seems"
						+ " to not be implemented.%n", message.command());
				throw new NotSupportedCommandException(message.command());
			}
			methodMap.get(message.command()).runCommand(message);
		} catch (GoException e) {
			message.author().handleException(e);
		}
		
	}
	
	/**
	 * Put the message on the queue for the commmand handler.
	 * @param message
	 * @throws CorruptedAuthorException
	 */
	public void enqueue(Message message) throws CorruptedAuthorException {
		if (getPlayer(message) == null) {
			System.err.println("This can never happen, only clients that are in this game can"
					+ "add commands to this queue.");
			throw new CorruptedAuthorException();
		} else {
			commandQueue.add(message);
		}
	}
	
	/**
	 * Check the connection state of the players.
	 * End the game if one of them is no longer connected.
	 */
	public void checkClientsAreLiving() {
		if (player1.client().dead()) {
			game.endGameEarly(player1);
		} else if (player2.client().dead()) {
			game.endGameEarly(player2);
		}
	}
	
	/**
	 * Get the player that sent this message.
	 * @param message
	 * @return
	 */
	private Player getPlayer(Message message) {
		if (message.author() == player1.client()) {
			return player1;
		} else if (message.author() == player2.client()) {
			return player2;
		} else {
			return null;
		}
	}
	
	/**
	 * Send a GAMESTART message to both players.
	 */
	public void sendGameStart() {
		player1.digest(Presenter.gameStart(player2.getName(), game.getBoard().size(), player1.getColor()));
		player2.digest(Presenter.gameStart(player1.getName(), game.getBoard().size(), player2.getColor()));
	}
	
	/**
	 * Send GAMEOVER message to the players.
	 */
	public void sendGameOver() {
		if (game.winner() != null) {
			((HumanPlayer) game.winner()).digest(Presenter.victory());
			((HumanPlayer) game.otherPlayer(game.winner())).digest(Presenter.defeat());
		} else {
			player1.digest(Presenter.draw());
			player2.digest(Presenter.draw());
		}
	}
	
	/**
	 * Send a move to both players
	 * @param player that made the move
	 * @param row
	 * @param col
	 */
	public void sendMove(Player player, int row, int col) {
		Message moveMessage = Presenter.serverMove(player.getColor(), row, col);
		player1.send(moveMessage);
		player2.send(moveMessage);
	}
	
	/**
	 * Send a pass to both players
	 * @param player that made the pass.
	 */
	public void sendPass(Player player) {
		Message passMessage = Presenter.serverPass(player.getColor());
		player1.send(passMessage);
		player2.send(passMessage);
	}
	
	/**
	 * Create the mapping from protocol command to executable Command.
	 */
	private void initializeMethodMap() {
		methodMap = new HashMap<String, Command>();
        methodMap.put(CHAT, chatCommand());
        methodMap.put(MOVE, moveCommand());
        methodMap.put(GETBOARD, getBoardCommand());
        methodMap.put(STOPGAME, stopGameCommand());
        methodMap.put(QUIT, quitCommand());
        methodMap.put(GETEXTENSIONS, CommandHandler.getExtensionsCommand());
        methodMap.put(EXTENSIONS, CommandHandler.extensionsCommand());

	}
	
	protected Command quitCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				game.endGameEarly(getPlayer(message));
				message.author().kill();
			}
		};
	}

	protected Command stopGameCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				game.endGameEarly(getPlayer(message));
			}
		};
	}	
	
	protected Command chatCommand() {
		return new Command() {
            public void runCommand(Message message) throws GoException { 
        		if (!((HumanPlayer) game.otherPlayer(getPlayer(message))).client().canChat()) {
        			throw new OtherPlayerCannotChatException();
        		} else {
        			player1.send(Presenter.chat(message.author().name(), message.args()));
        			player2.send(Presenter.chat(message.author().name(), message.args()));
        		}
            }
        };
	}

	protected Command moveCommand() {
		return new Command() {
        	public void runCommand(Message message) throws GoException {
        		try {
            		if (message.args()[0].equals(Presenter.PASS)) {
            			game.pass(getPlayer(message));
            			sendPass(getPlayer(message));
            		} else {
        				Player player = getPlayer(message);
        				int row = Interpreter.integer(message.args()[0]);
        				int col = Interpreter.integer(message.args()[1]);
            			game.makeMove(player, row, col);
            			sendMove(player, row, col);
            		}        			
        		} catch (IndexOutOfBoundsException e) {
    				throw new ArgumentsMissingException("USAGE: \"MOVE int int\" or \"MOVE PASS\"");
    			}

        	}
        };
	}

	protected Command getBoardCommand() {
		return new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().send(Presenter.boardMessage(game.getBoard()));
        	}
        };
	}

}
