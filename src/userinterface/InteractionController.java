package userinterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controllers.Client;
import exceptions.GoException;
import exceptions.InvalidArgumentException;
import exceptions.InvalidMoveException;
import exceptions.NotApplicableCommandException;
import game.Board;
import game.Stone;
import network.protocol.Constants;
import network.protocol.Interpreter;
import network.protocol.Message;

public class InteractionController extends Thread implements Observer, Constants {
	private Client client;
	private View view;
	private UserListener userListener;
	private HashMap<String, String> allCommands;
	private HashMap<String, String> menuItems;
	private String selectedItem;
	
	public InteractionController(Client client) {
		this.client = client;
		view = new TUIView(System.out);
		userListener = new UserListener();
		initializeAllCommands();
	}
	
	public void update(Observable observable, Object object) {
		if (observable instanceof Client && (object == null || object instanceof Message)) {
			resetMenu();
			Client client = (Client) observable;
			Message message = (Message) object;
			view.renderState(client, message);
		} else {
			System.err.println("The InteractionController should receive a Message.");
		}
	}
	
	public void run() {
		
		while (true) {
			processUserInput(UserListener.readString());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public void processUserInput(String input) {
		try {
			Message message = Interpreter.digest(input);
			message.setUser(client);
			client.process(message);
		} catch (GoException e) {
			System.out.print(e.toString());
			System.out.println(e.getMessage());
		}
		
	}
	
	public int[] getMove() {
		int row = -1;
		int col = -1;
		while (!validMove(client.getColor(), row, col)) {
			try {
				String input = UserListener.readString();
				row = row(UserListener.readInt(input));
				col = col(UserListener.readChar(input));
			} catch (InvalidArgumentException e) {
				System.out.println("Enter a move: for example B6.");
			}
		}
		return new int[]{row, col};
	}
	
	public static int row(int rowInput) {
		return rowInput - 1;
	}
	
	public static int col(char charInput) {
		int value = Character.getNumericValue(Character.toUpperCase(charInput));
		return value - 65;
	}
	
	public boolean validMove(Stone stone, int row, int col) {
		Board boardCopy = client.getBoard().deepCopy();
		try {
			boardCopy.layStone(stone, row, col);
		} catch (InvalidMoveException e) {
			return false;
		}
		return true;
	}
	
	public void selectItem(String input) throws NotApplicableCommandException {
		for (String key : menuItems.keySet()) {
			if (input.equalsIgnoreCase(key)) {
				selectedItem = menuItems.get(key);
				break;
			}
		}
		throw new NotApplicableCommandException(String.format("%s is not a possible "
				+ "option, please choose an option from the menu.", input));
	}
	
	public void resetMenu() {
		selectedItem = null;
		menuItems = new HashMap<String, String>();
		
		for (String command : client.currentState().applicableCommands()) {
			menuItems.put(allCommands.get(command), command);
		}
	}
	
	public void initializeAllCommands() {
		allCommands = new HashMap<String, String>();
		
		allCommands.put(NEWPLAYER, "N");
		allCommands.put(MOVE, "M");
		allCommands.put(CHAT, "C");
		allCommands.put(PLAY, "P");
		allCommands.put(CHALLENGE, "S");
		allCommands.put(CHALLENGEACCEPTED, "A");
		allCommands.put(CHALLENGEDENIED, "D");
		allCommands.put(CANCEL, "R");
		allCommands.put(STOPGAME, "E");
		allCommands.put(QUIT, "Q");
	}
	
	public void showMenu(List<String> options) {
		view.showMenu(new HashSet<String>(options));
	}

}
