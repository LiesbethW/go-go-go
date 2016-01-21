package network.protocol;

import static network.protocol.Constants.DELIMITER;

import network.ClientCommunicator;

public class Message {
	private ClientCommunicator author;
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
	
	public Message(String command, String[] args, ClientCommunicator author) {
		this(command, args);
		setAuthor(author);
	}
	
	public String command() {
		return command;
	}
	
	public String[] args() {
		return args;
	}
	
	public ClientCommunicator author() {
		return author;
	}
	
	public void setAuthor(ClientCommunicator author) {
		this.author = author;
	}
	
	public String toString() {
		String arguments = String.join(DELIMITER, args);
		String message = String.join(DELIMITER, command, arguments);
		return message.trim();
	}

}
