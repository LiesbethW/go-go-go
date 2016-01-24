package controllers;

import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.CorruptedAuthorException;
import game.Game;
import game.HumanPlayer;
import game.Player;
import network.protocol.Message;
import network.protocol.Presenter;

public class GameController extends Thread {
	private Game game;
	private Player player1;
	private Player player2;
	
	private ClientHandler client1;
	private ClientHandler client2;
	
	private ConcurrentLinkedQueue<Message> commandQueue;
	
	public GameController(ClientHandler client1, ClientHandler client2, int boardSize) {
		commandQueue = new ConcurrentLinkedQueue<Message>();
		player1 = new HumanPlayer(client1);
		player2 = new HumanPlayer(client2);
		game = new Game(player1, player2, boardSize);
		
	}
	
	public void run() {
		sendGameStart();
		while (!game.gameOver()) {
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
		}	
		sendGameOver();
	}
	
	public void process(Message message){
		System.out.println(message);
	}
	
	public void enqueue(Message message) throws CorruptedAuthorException {
		if (getPlayer(message) == null) {
			throw new CorruptedAuthorException();
		} else {
			commandQueue.add(message);
		}
	}
	
	private Player getPlayer(Message message) {
		if (message.author().name() == player1.getName()) {
			return player1;
		} else if (message.author().name() == player2.getName()) {
			return player2;
		} else {
			return null;
		}
	}
	
	public void sendGameStart() {
		player1.send(Presenter.gameStart(player2.getName(), game.getBoard().size(), player1.getColor()));
		player2.send(Presenter.gameStart(player1.getName(), game.getBoard().size(), player2.getColor()));
	}
	
	public void sendGameOver() {
		if (game.winner() != null) {
			game.winner().send(Presenter.victory());
			game.otherPlayer(game.winner()).send(Presenter.defeat());
		} else {
			player1.send(Presenter.draw());
			player2.send(Presenter.draw());
		}
	}

}
