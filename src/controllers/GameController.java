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
	
	public GameController(ClientHandler client1, ClientHandler client2, Server server, int boardSize) {
		commandQueue = new ConcurrentLinkedQueue<Message>();
		player1 = new HumanPlayer(client1);
		player2 = new HumanPlayer(client2);
		game = new Game(player1, player2, boardSize);
	}
	
	public void run() {
		System.out.println("Game has started");
		initializeMethodMap();
		sendGameStart();
		while (!game.gameOver()) {
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}	
		sendGameOver();
	}
	
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
	
	public void enqueue(Message message) throws CorruptedAuthorException {
		if (getPlayer(message) == null) {
			System.err.println("This can never happen, only clients that are in this game can"
					+ "add commands to this queue.");
			throw new CorruptedAuthorException();
		} else {
			commandQueue.add(message);
		}
	}
	
	private Player getPlayer(Message message) {
		if (message.author() == player1.client()) {
			return player1;
		} else if (message.author() == player2.client()) {
			return player2;
		} else {
			return null;
		}
	}
	
	public void sendGameStart() {
		player1.digest(Presenter.gameStart(player2.getName(), game.getBoard().size(), player1.getColor()));
		player2.digest(Presenter.gameStart(player1.getName(), game.getBoard().size(), player2.getColor()));
	}
	
	public void sendGameOver() {
		if (game.winner() != null) {
			((HumanPlayer) game.winner()).digest(Presenter.victory());
			((HumanPlayer) game.otherPlayer(game.winner())).digest(Presenter.defeat());
		} else {
			player1.digest(Presenter.draw());
			player2.digest(Presenter.draw());
		}
		server.endGame(player1.client(), player2.client());
	}
	
	private void initializeMethodMap() {
		methodMap = new HashMap<String, Command>();

        methodMap.put(CHAT, chatCommand());
        methodMap.put(MOVE, moveCommand());
        methodMap.put(GETBOARD, getBoardCommand());
        methodMap.put(QUIT, quitCommand());
        methodMap.put(GETOPTIONS, CommandHandler.getOptionsCommand());
        methodMap.put(OPTIONS, CommandHandler.optionsCommand());

	}
	
	protected Command quitCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				message.author().digest(Presenter.defeat());
				((HumanPlayer) game.otherPlayer(getPlayer(message))).digest(Presenter.victory());
				server.endGame(player1.client(), player2.client());
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
        		if (message.equals(Presenter.clientPass())) {
        			game.pass(getPlayer(message));
        		} else {
        			try {
            			game.makeMove(getPlayer(message), Interpreter.integer(message.args()[0]), 
            					Interpreter.integer(message.args()[1]));
        			} catch (IndexOutOfBoundsException e) {
        				throw new ArgumentsMissingException("USAGE: \"MOVE int int\" or \"MOVE PASS\"");
        			}
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
