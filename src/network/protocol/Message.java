package network.protocol;

import static network.protocol.Constants.DELIMITER;

import controllers.Client;
import controllers.ClientHandler;

public class Message {
	private ClientHandler author;
	private Client user;
	private String command;
	private String[] args;
	
	public Message(String command, String[] args) {
		this.command = command;
		this.args = args;
	}

	public Message(String command) {
		this(command, "");
	}
	
	public Message(String command, String arg) {
		this(command, new String[]{arg});
	}
	
	public Message(String command, String arg1, String arg2) {
		this(command, new String[]{arg1, arg2});
	}
	
	public Message(String command, String arg1, String arg2, String arg3) {
		this(command, new String[]{arg1, arg2, arg3});
	}
	
	public Message(String command, String[] args, ClientHandler author) {
		this(command, args);
		setAuthor(author);
	}
	
	public String command() {
		return command;
	}
	
	public String[] args() {
		return args;
	}
	
	public ClientHandler author() {
		return author;
	}
	
	public Client user() {
		return user;
	}
	
	public void setAuthor(ClientHandler author) {
		this.author = author;
	}
	
	public void setUser(Client user) {
		this.user = user;
	}
	
	public String toString() {
		String arguments = String.join(DELIMITER, args);
		String message = String.join(DELIMITER, command, arguments);
		return message.trim();
	}

}
