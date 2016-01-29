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
import network.protocol.Presenter;

public class InteractionController extends Thread implements Observer, Constants {
	private Client client;
	private View view;
	private UserListener userListener;
	private HashMap<String, String> allCommands;
	private HashMap<String, String> menuItems;
	private String selectedItem;
	
	public InteractionController(Client client) {
		this.client = client;
		view = new TUIView(System.out, this);
		userListener = new UserListener();
		initializeAllCommands();
		resetMenu();
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
			// Preprocessing.
			if (input.startsWith(MOVE) && !input.startsWith(Presenter.clientPass().toString())) {
				input = getMove(input.substring(4));
			}
			
			if (input.startsWith(HINT)) {
				giveHint();
				return;
			}
		
			Message message = Interpreter.digest(input);
			message.setUser(client);
			client.process(message);
		} catch (GoException e) {
			view.showMessage(e.toString() + e.getMessage());
		}
		
	}
	
	public void giveHint() {
		view.showMessage("This is a hint");
	}

	
	public String getMove(String input) throws InvalidArgumentException {
		int row = -1;
		int col = -1;
		try {
			row = row(UserListener.readInt(input));
			col = col(UserListener.readChar(input));
		} catch (InvalidArgumentException e) {
			throw new InvalidArgumentException("Enter a move: for example B6.");
		}
		return String.join(DELIMITER, MOVE, String.valueOf(row), String.valueOf(col));
	}
	
	public static int row(int rowInput) {
		return rowInput - 1;
	}
	
	public static int col(char charInput) {
		int value = Character.getNumericValue(Character.toUpperCase(charInput));
		return value - 10;
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
	
	public HashSet<String> menuOptions() {
		return new HashSet<String>(menuItems.values());
	}
	
	/**
	 * Create the mapping from letters to the applicable options.
	 */
	public void resetMenu() {
		selectedItem = null;
		menuItems = new HashMap<String, String>();
		
		for (String command : getOptions()) {
			menuItems.put(allCommands.get(command), command);
		}
	}

	/**
	 * Get the options for the user.
	 * @return
	 */
	private HashSet<String> getOptions() {
		HashSet<String> options = client.getOptions();
		if (client.isPlaying()) {
			options.add(PASS);
			options.add(HINT);
		}
		return options;
	}	
	
	/**
	 * Create a mapping from single letters to options.
	 */
	private void initializeAllCommands() {
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
		allCommands.put(PASS, "P");
		allCommands.put(HINT, "H");
	}
	
	public void showMenu(List<String> options) {
		view.showMenu(new HashSet<String>(options));
	}

}
