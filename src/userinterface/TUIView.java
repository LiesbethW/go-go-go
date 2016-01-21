package userinterface;

import java.io.BufferedWriter;
import java.util.HashSet;

import game.Board;

public class TUIView implements View {
	private BufferedWriter out;
	
	public TUIView(BufferedWriter out) {
		this.out = out;
	}
	
	public void showMenu() {
		//TODO
	}
	
	public void showOptions(HashSet<String> options) {
		//TODO		
	}
	
	public void showMessage(String message) {
		//TODO		
	}
	
	public void showBoard(Board board) {
		out(BoardView(board).render());
	}

}
