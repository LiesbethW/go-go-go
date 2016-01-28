package controllers;

import java.util.Arrays;
import java.util.HashMap;

import exceptions.GoException;
import exceptions.InvalidArgumentException;
import exceptions.NotSupportedCommandException;
import game.Stone;
import network.protocol.Constants;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;
import userinterface.InteractionController;

public class ClientCommandHandler implements Constants {
	private Client client;
	private InteractionController interactionController;
	private HashMap<String, Command> methodMap;	
	
	public ClientCommandHandler(Client client, InteractionController interactionController) {
		this.client = client;
		this.interactionController = interactionController;
		initializeMethodMap();
	}
	
	public void process(Message message) {
		System.out.println("ClientCommandHandler: processing " + message.toString());
		try {
			if (!methodMap.containsKey(message.command())) {
				throw new NotSupportedCommandException(String.format("CommandHandler "
						+ "tries to process %s, but that seems to not be implemented.%n", 
						message.command()));
			}
			methodMap.get(message.command()).runCommand(message);
		} catch (GoException e) {
			client.handleException(e);
		}
		
	}
	
	public void initializeMethodMap() {
		methodMap = new HashMap<String, Command>();
		
		methodMap.put(NEWPLAYERACCEPTED, simpleDigest());
        methodMap.put(CHAT, chatCommand());
        methodMap.put(OPTIONS, optionCommand());
        methodMap.put(EXTENSIONS, setExtensionsCommand());
        methodMap.put(GETEXTENSIONS, getExtensionsCommand());
        methodMap.put(YOURECHALLENGED, simpleDigest());
        methodMap.put(YOUVECHALLENGED, simpleDigest());
        methodMap.put(CHALLENGEDENIED, challengeDeniedCommand());
        methodMap.put(CHALLENGEACCEPTED, challengeAcceptedCommand());
        methodMap.put(WAITFOROPPONENT, simpleDigest());
        methodMap.put(CANCELLED, simpleDigest());
        methodMap.put(GAMESTART, simpleDigest());
        methodMap.put(GAMEOVER, gameOverCommand());
        methodMap.put(FAILURE, failureCommand());
        
        methodMap.put(MOVE, moveCommand());
        
        methodMap.put(NEWPLAYER, newPlayerCommand());
        methodMap.put(PLAY, playCommand());
        methodMap.put(CANCEL, cancelCommand());
	}
	
	protected Command gameOverCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.send(message);
			}
		};
	}	
	
	protected Command playCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.send(message);
			}
		};
	}		
	
	protected Command cancelCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.send(message);
			}
		};
	}
	
	protected Command newPlayerCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				if (message.args().length != 1) {
					throw new InvalidArgumentException("Choose a name without spaces.");
				} else {
					client.setPlayerName(message.args()[0]);
					client.send(message);
				}
			}
		};
	}
	
	protected Command moveCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				if (message.user() != null) {
					client.send(message);
				} else {
					if (message.args()[0].equals(Presenter.PASS)) {
						client.setWhosTurnItIs(Interpreter.color(message.args()[0]).opponent());
					} else {
						try {
							Stone color = Interpreter.color(message.args()[0]);
	        				int row = Interpreter.integer(message.args()[1]);
	        				int col = Interpreter.integer(message.args()[2]);
	        				client.getBoard().layStone(color, row, col);
	        				client.setWhosTurnItIs(Interpreter.color(message.args()[0]).opponent());
						} catch (InvalidArgumentException e) {
							client.handleException(e);
						}						
					}
				}
			}
		};
	}
	
	protected Command challengeDeniedCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.digest(message);
			}
		};
	}
	
	protected Command challengeAcceptedCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.digest(message);
			}
		};
	}
	
	protected Command chatCommand() {
		return new Command() {
            public void runCommand(Message message) { 
            	if (message.user() != null) {
            		client.send(message);
            	} else {
            		client.addChatMessage(String.join(DELIMITER, message.args()));
            	}
            }
        };
	}
	
	protected Command optionCommand() {
		return new Command() {
			public void runCommand(Message message) {
				interactionController.showOptions(Interpreter.options(message));
			}
		};
	}
	
	protected Command simpleDigest() {
		return new Command() {
            public void runCommand(Message message) throws GoException { 
            	client.digest(message);
            }
        };
	}	
	
	protected Command setExtensionsCommand() {
		return new Command() {
			public void runCommand(Message message) {
				if (Arrays.asList(message.args()).contains(Presenter.chatOpt().toString())) {
					client.enableChat();
				}
				if (Arrays.asList(message.args()).contains(Presenter.challengeOpt().toString())) {
					client.enableChallenge();
				}
			}
		};
	}
	
	protected Command getExtensionsCommand() {
		return new Command() {
			public void runCommand(Message message) {
				client.send(Presenter.extensions(client.SUPPORTED_EXTENSIONS));
			}
		};
	}
	
	protected Command failureCommand() {
		return new Command() {
			public void runCommand(Message message) {
				System.err.println("Client received: " + message.toString());
			}
		};
	}

}
