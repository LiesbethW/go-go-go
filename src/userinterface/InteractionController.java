package userinterface;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controllers.Client;
import exceptions.GoException;
import network.protocol.Interpreter;
import network.protocol.Message;

public class InteractionController extends Thread implements Observer {
	private Client client;
	private View view;
	private UserListener userListener;
	
	public InteractionController(Client client) {
		this.client = client;
		view = new TUIView(System.out);
		userListener = new UserListener();
	}
	
	public void update(Observable observable, Object object) {
		if (observable instanceof Client && (object == null || object instanceof Message)) {
			Client client = (Client) observable;
			Message message = (Message) object;
			view.renderState(client, message);
		} else {
			System.err.println("The InteractionController should receive a Message.");
		}
	}
	
	public void run() {
		
		while (true) {
			processUserInput(userListener.readString());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public void processUserInput(String input) {
		try {
			Message message = Interpreter.digest(input);
			message.setUser(client);
			client.process(message);
		} catch (GoException e) {
			System.out.print(e.toString());
			System.out.println(e.getMessage());
		}
		
	}
	
	public void showChat(String message) {
		view.showMessage(message);
	}
	
	public void showOptions(List<String> options) {
		
	}

}
