package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.GoException;
import exceptions.NameTakenException;
import exceptions.NotApplicableCommandException;
import exceptions.NotSupportedCommandException;
import exceptions.PlayerNotAvailableException;
import network.Server;
import network.protocol.Constants;
import network.protocol.Interpreter;
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
			if (!methodMap.containsKey(message.command())) {
				System.err.printf("CommandHandler tries to process %s, but that seems"
						+ " to not be implemented.%n", message.command());
				throw new NotSupportedCommandException(message.command());
			}
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
        
        methodMap.put(OPTIONS, new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().setOptions(Interpreter.options(message));
        	}
        });
        
        methodMap.put(GETOPTIONS, new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().send(Presenter.options(server.OPTIONS));
        	}
        });
        
        methodMap.put(CHALLENGE, new Command() {
        	public void runCommand(Message message) throws GoException {
        		if (message.args().length == 0) {
        			List<ClientHandler> opponents = server.clientsThatCanBeChallenged();
        			opponents.remove(message.author());
        			if (opponents.size() == 0) {
        				throw new PlayerNotAvailableException();
        			} else {
        				message.author().send(Presenter.challengableOpponentsList(opponents));
        			}
        		} else {
        			ClientHandler opponent = server.findClientByName(message.args()[0]);
        			if (opponent != null && opponent.canChallenge() && opponent.readyToPlay() && opponent != message.author()) {
        				message.author().digest(Presenter.youveChallenged(opponent.name()));
        				opponent.digest(Presenter.youreChallenged(message.author().name()));	
        			} else {
        				throw new PlayerNotAvailableException();
        			}
        		}
        	}
        });
        
        methodMap.put(CHALLENGEACCEPTED, new Command() {
        	public void runCommand(Message message) throws NotApplicableCommandException {
        		message.author().digest(message);
        		server.startNewGame(message.author(), message.author().getOpponent());
        	}
        });
        
        methodMap.put(CHALLENGEDENIED, new Command() {
        	public void runCommand(Message message) throws NotApplicableCommandException {
        		message.author().digest(message);
        	}
        });       
		
	}
}
