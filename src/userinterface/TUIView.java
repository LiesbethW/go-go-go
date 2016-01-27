package userinterface;

import java.io.PrintStream;
import java.util.List;

import game.Board;

public class TUIView implements View {
	private PrintStream out;
	
	public TUIView(PrintStream out) {
		this.out = out;
	}
	
	public void showMenu() {
		//TODO
	}
	
	public void showOptions(List<String> options) {
		//TODO		
	}
	
	public void showMessage(String message) {
		show(message);		
	}
	
	public void showBoard(Board board) {
		show((new BoardView(board)).render());
	}
	
	private void show(String string) {
		out.println(string);
	}

}
