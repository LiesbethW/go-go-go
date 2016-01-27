package userinterface;

import java.util.List;

import game.Board;

public interface View {
	
	public void showMenu();
	
	public void showOptions(List<String> options);
	
	public void showMessage(String message);
	
	public void showBoard(Board board);
	
	
}
