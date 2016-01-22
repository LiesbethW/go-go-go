package controllers;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.GoException;
import exceptions.NameTakenException;
import network.Server;
import network.protocol.Constants;
import network.protocol.Message;
import network.protocol.Presenter;

public class CommandHandler extends Thread implements Constants {
	private Server server;
	private ConcurrentLinkedQueue<Message> commandQueue;
	private HashMap<String, Command> methodMap;
	
	public interface Command {
		public void runCommand(Message message) throws GoException;
	}
	
	public CommandHandler(Server server, ConcurrentLinkedQueue<Message> commandQueue) {
		this.server = server;
		this.commandQueue = commandQueue;
		initializeMethodMap();
	}

	public void run() {
		while (true) {
			if (commandQueue.peek() != null) {
				process(commandQueue.poll());
			}
		}
	}
	
	public void process(Message message) {
		System.out.println("CommandHandler: processing " + message.toString());
		if (!server.clients().contains(message.author())) {
			System.out.println("Client has been disconnected");
		}
		try {
			methodMap.get(message.command()).runCommand(message);
		} catch (GoException e) {
			message.author().handleException(e);
		}
		
	}
	
	public void initializeMethodMap() {
		methodMap = new HashMap<String, Command>();
		
		methodMap.put(NEWPLAYER, new Command() {
            public void runCommand(Message message) throws GoException { 
            		if (server.findClientByName(message.args()[0]) != null) {
            			throw new NameTakenException();
            		} else {
            			System.out.println("Digesting..");
            			message.author().digest(message);
            		}
            	};
        });

        methodMap.put(CHAT, new Command() {
            public void runCommand(Message message) throws GoException { 
            	for (ClientHandler client : server.clientsThatCanChat()) {
            		client.send(Presenter.chat(message.author().name(), message.args()));
            	}
            };
        });
		
	}
}
