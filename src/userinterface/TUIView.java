package userinterface;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import controllers.Client;
import game.Board;
import network.protocol.Constants;
import network.protocol.Message;

public class TUIView implements View, Constants {
	private PrintStream out;
	private HashMap<String, String> optionRender;
	
	public TUIView(PrintStream out) {
		this.out = out;
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
		
		showOptions(client.getOptions());
		
		if (message != null && renderMessage(message) != null) {
			show(renderMessage(message));
		} else {
			show(stateMessage(client));
		}
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
	
	public void showMenu() {
		//TODO
	}
	
	public void showOptions(HashSet<String> options) {
		String[] menu = new String[8];
		menu[0] = "----------------------------------- MENU ------------------------------------";
		menu[7] = "-----------------------------------------------------------------------------";
		menu[6] = optionRender.get(QUIT);
		options.remove(QUIT);
		int i = 1;
		for (String option : options) {
			menu[i] = option;
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
		return null;
	}
	
	public String stateMessage(Client client) {
		if (client.newClient()) {
			return "Welcome! Please enter your name";
		}
		
		return "What would you like to do?";
	}
	
	public void cleanSlate() {
		String[] newlines = new String[50];
		Arrays.fill(newlines, "\n");
		String space = String.join("\n", newlines);
		show(space);
	}
	
	public void spacing() {
		show("\n\n");
	}	
	
	public void initializeOptionRender() {
		optionRender = new HashMap<String, String>();
		
		optionRender.put(MOVE, "M - Make a move");
		optionRender.put(CHAT, "C - Send a chat message");
		optionRender.put(PLAY, "P - Play with a random opponent");
		optionRender.put(CHALLENGE, "S - select an opponent to challenge");
		optionRender.put(CHALLENGEACCEPTED, "A - Accept the challenge");
		optionRender.put(CHALLENGEDENIED, "D - Decline the challenge");
		optionRender.put(CANCEL, "R - Return, stop waiting for opponent");
		optionRender.put(STOPGAME, "E - End this game");
		optionRender.put(QUIT, "Q - Quit");
		
		
	}

}
