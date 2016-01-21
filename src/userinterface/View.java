package userinterface;

import java.util.HashSet;

import game.Board;

public interface View {
	
	public void showMenu();
	
	public void showOptions(HashSet<String> options);
	
	public void showMessage(String message);
	
	public void showBoard(Board board);
	
	
}
