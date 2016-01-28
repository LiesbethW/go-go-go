package userinterface;

import java.util.HashSet;

import controllers.Client;
import game.Board;
import network.protocol.Message;

public interface View {
	
	public void showMenu();
	
	public void showOptions(HashSet<String> options);
	
	public void showMessage(String message);
	
	public void showBoard(Board board);
	
	public void renderState(Client client, Message message);
	
}
