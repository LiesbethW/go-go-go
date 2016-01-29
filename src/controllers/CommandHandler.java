package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import exceptions.GoException;
import exceptions.InvalidArgumentException;
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
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
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
		
		methodMap.put(NEWPLAYER, newPlayerCommand());
        methodMap.put(CHAT, chatCommand());
        methodMap.put(EXTENSIONS, extensionsCommand());
        methodMap.put(GETEXTENSIONS, getExtensionsCommand());
        methodMap.put(GETOPTIONS, getOptionsCommand());
        methodMap.put(PLAY, playCommand());
        methodMap.put(CANCEL, cancelCommand());
        methodMap.put(CHALLENGE, challengeCommand());
        methodMap.put(CHALLENGEACCEPTED, simpleDigest());   
        methodMap.put(CHALLENGEDENIED, simpleDigest());       
		methodMap.put(QUIT, quitCommand());
        
	}
	
	public static Command simpleDigest() {
		return new Command() {
			public void runCommand(Message message) throws NotApplicableCommandException {
				message.author().digest(message);
			}
		};
	}

	public static Command cancelCommand() {
		return new Command() {
			public void runCommand(Message message) throws NotApplicableCommandException {
				message.author().digest(Presenter.cancelled());
			}
		};
	}

	protected Command challengeCommand() {
		return new Command() {
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
        };
	}

	protected Command playCommand() {
		return new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().digest(message);
        		System.out.println(message.author().currentState().toString());
        		if (server.clientsWaitingForOpponent().size() >= 2) {
        			ClientHandler client1 = server.clientsWaitingForOpponent().get(0);
        			ClientHandler client2 = server.clientsWaitingForOpponent().get(1);
        			client1.setOpponent(client2);
        			client2.setOpponent(client1);
        			client1.digest(Presenter.play());
        			client2.digest(Presenter.play());
        		}
        	}
        };
	}

	protected static Command getExtensionsCommand() {
		return new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().send(Presenter.extensions(Server.EXTENSIONS));
        	}
        };
	}

	protected static Command extensionsCommand() {
		return new Command() {
        	public void runCommand(Message message) throws GoException {
        		message.author().setExtensions(Interpreter.extensions(message));
        	}
        };
	}

	protected Command chatCommand() {
		return new Command() {
            public void runCommand(Message message) throws GoException { 
            	for (ClientHandler client : server.clientsThatCanChat()) {
            		client.send(Presenter.chat(message.author().name(), message.args()));
            	}
            };
        };
	}

	protected Command newPlayerCommand() {
		return new Command() {
            public void runCommand(Message message) throws GoException { 
            		if (message.args().length != 1) {
            			throw new InvalidArgumentException();
            		}
            		if (server.findClientByName(message.args()[0]) != null) {
            			throw new NameTakenException();
            		} else {
            			message.author().digest(message);
            		}
            	};
        };
	}
	
	protected Command quitCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				message.author().kill();
			};
		};
	}
	
	protected Command getOptionsCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				message.author().send(Presenter.options(message.author().
						currentState().applicableCommands()));
			}
		};
	}
}
