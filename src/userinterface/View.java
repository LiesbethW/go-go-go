package userinterface;

import java.util.HashMap;
import java.util.HashSet;

import controllers.Client;
import game.Board;
import network.protocol.Message;

public interface View {
	
	public void showOptions(HashMap<Integer, String> options);
	
	public void showMenu(HashSet<String> menuItems);
	
	public void showMessage(String message);
	
	public void showBoard(Board board);
	
	public void renderState(Client client, Message message);
	
}
