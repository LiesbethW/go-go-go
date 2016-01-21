package controllers;

import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.CorruptedAuthorException;
import game.Game;
import game.Player;
import network.protocol.Message;

public class GameController extends Thread {
	private Game game;
	private Player player1;
	private Player player2;
	
	private ConcurrentLinkedQueue<Message> commandQueue;
	
	public GameController() {
		commandQueue = new ConcurrentLinkedQueue<Message>();
	}
	
	public void run() {
		while (true) {
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
		}		
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

}
