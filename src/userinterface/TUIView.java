package userinterface;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import controllers.Client;
import exceptions.InvalidArgumentException;
import game.Board;
import network.protocol.Constants;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;

public class TUIView implements View, Constants {
	private PrintStream out;
	private InteractionController interactionController;
	private HashMap<String, String> optionRender;
	
	public TUIView(PrintStream out, InteractionController interactionController) {
		this.out = out;
		this.interactionController = interactionController;
		initializeOptionRender();
	}
	
	private void show(String string) {
		out.println(string);
	}
	
	public void renderState(Client client, Message message) {
		cleanSlate();
		
		if (client.canChat() && client.latestChatMessages().size() > 0) {
			showChatMessages(client.latestChatMessages());
			spacing();
		}
		
		if (client.isPlaying()) {
			showBoard(client.getBoard());
			spacing();
		}
		
		showMenu(interactionController.menuOptions());
		spacing();
		
		if (message != null && renderMessage(message) != null) {
			show(renderMessage(message));
		} else {
			show(stateMessage(client));
		}
		
		spacing();
		showOptions(client);
		
	}
	
	
	public String renderBoard(Board board) {
		return new BoardView(board).render();
	}
	
	public void showChatMessages(List<String> chatMessages) {
		show("----------------------------------- CHAT ------------------------------------");
		for (String message : chatMessages) {
			show(message);
		}
		show("-----------------------------------------------------------------------------");
	}
	
	public void showOptions(Client client) {

	}
	
	public void showOptions(HashMap<Integer, String> options) {
		//TODO
	}
	
	public void showMenu(HashSet<String> menuItems) {
		String[] menu = new String[8];
		Arrays.fill(menu, "");
		menu[0] = "----------------------------------- MENU ------------------------------------";
		menu[7] = "-----------------------------------------------------------------------------";
		menu[6] = optionRender.get(QUIT);
		menuItems.remove(QUIT);
		int i = 1;
		for (String item : menuItems) {
			menu[i] = optionRender.get(item);
			i++;
		}
		show(String.join("\n", menu));
	}
	
	public void showMessage(String message) {
		show(message);		
	}
	
	public void showBoard(Board board) {
		show(renderBoard(board));
	}
	
	public String renderMessage(Message message) {
		if (message.command().equals(Presenter.FAILURE)) {
			try {
				return Interpreter.exception(message).getMessage();
			} catch (InvalidArgumentException e) {
				return null;
			}
		} else if (message.command().equals(Presenter.challengeAccepted().toString()) 
				&& message.user() == null) {
			return "Your challenge was accepted!";
		} else if (message.command().equals(Presenter.challengeDenied().toString()) 
				&& message.user() == null) {
			return "Your challenge was declined.";
		} else if (message.command().equals(MOVE) && message.args().length > 1 
				&& message.args()[1].equals(PASS)) {
			return String.format("%s passed.", message.args()[0].toLowerCase());
		} else if (message.command().equals(GAMEOVER)) {
			if (message.args()[0].equals(VICTORY)) {
				return "Game over. You've won!";
			} else if (message.args()[0].equals(DEFEAT)) {
				return "Game over. You've lost.";
			} else {
				return "Game over. It's a draw!";
			}
		}
		return null;
	}
	
	public String stateMessage(Client client) {
		if (client.newClient()) {
			return "Welcome! Please enter your name";
		} else if (client.readyToPlay()) {
			return "Are you ready to start playing a game?";
		} else if (client.waitingForOpponent()) {
			return "Wait for another player to play against you.";
		} else if (client.isChallenged()) {
			return String.format("You've been challenged by %s, do you want to accept or decline?", client.opponent());
		} else if (client.waitingForChallengeResponse()) {
			return (String.format("You've challenged %s, wait for his/her response.", client.opponent()));
		} else if (client.canStartPlaying()) {
			return "Starting game...";
		} else if (client.isPlaying() && client.myTurn()) {
			return String.format("Your turn! Make a move. (You play with %s)", client.getColor().toString());
		} else if (client.isPlaying() && !client.myTurn()) {
			return String.format("Wait for %s to make a move.", client.opponent());
		} else {
			return "What would you like to do?";
		}
	}
	
	/**
	 * A lot of empty lines to get a 'clean' command line.
	 */
	public void cleanSlate() {
		String[] newlines = new String[50];
		Arrays.fill(newlines, "\n");
		String space = String.join("\n", newlines);
		show(space);
	}
	
	/**
	 * A new line
	 */
	public void spacing() {
		show("\n");
	}	
	
	/**
	 * Make hash for how options are displayed.
	 */
	public void initializeOptionRender() {
		optionRender = new HashMap<String, String>();
		
//		optionRender.put(NEWPLAYER, "N - Enter your name");
//		optionRender.put(MOVE, "M - Make a move");
//		optionRender.put(CHAT, "C - Send a chat message");
//		optionRender.put(PLAY, "P - Play with a random opponent");
//		optionRender.put(CHALLENGE, "S - select an opponent to challenge");
//		optionRender.put(CHALLENGEACCEPTED, "A - Accept the challenge");
//		optionRender.put(CHALLENGEDENIED, "D - Decline the challenge");
//		optionRender.put(CANCEL, "R - Return, stop waiting for opponent");
//		optionRender.put(STOPGAME, "E - End this game");
//		optionRender.put(QUIT, "Q - Quit");
//		optionRender.put(PASS, "P - Pass");
//		optionRender.put(HINT, "H - Get a hint.");
		
		optionRender.put(NEWPLAYER, "NEWPLAYER <name> - (Give your name to start)");
		optionRender.put(MOVE, "MOVE <row> <col> - (Give the coordinates of your move)");
		optionRender.put(CHAT, "CHAT <message> - (Send a chat message)");
		optionRender.put(PLAY, "PLAY - (Play with a random opponent)");
		optionRender.put(CHALLENGE, "CHALLENGE <name> - (Select an opponent to challenge)");
		optionRender.put(CHALLENGEACCEPTED, "CHALLENGEACCEPTED - (Accept the challenge)");
		optionRender.put(CHALLENGEDENIED, "CHALLENGEDENIED - (Decline the challenge)");
		optionRender.put(CANCEL, "CANCEL - (Return, stop waiting for opponent)");
		optionRender.put(STOPGAME, "STOPGAME - (End this game)");
		optionRender.put(QUIT, "QUIT - (Quit)");
		optionRender.put(PASS, "MOVE PASS - (Pass your turn)");
		optionRender.put(HINT, "HINT - (Get a hint)");
	}

}
