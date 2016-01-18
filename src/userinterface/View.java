package userinterface;

import game.Board;

public interface View {
	
	public void showMenu();
	
	public void showMessage(String message);
	
	public void showBoard(Board board);
	
	
}
