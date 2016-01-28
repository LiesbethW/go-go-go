package controllers;

import java.util.Arrays;
import java.util.HashMap;

import exceptions.GoException;
import exceptions.InvalidArgumentException;
import exceptions.InvalidMoveException;
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
	
	/**
	 * Create a new commandHandler for this client.
	 * @param client
	 * @param interactionController
	 */
	public ClientCommandHandler(Client client, InteractionController interactionController) {
		this.client = client;
		this.interactionController = interactionController;
		initializeMethodMap();
	}
	
	/**
	 * Select the method for a certain command and run it.
	 * @param message
	 */
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

	/**
	 * Create the mapping from protocol command to executable Command.
	 */
	public void initializeMethodMap() {
		methodMap = new HashMap<String, Command>();
		
		methodMap.put(NEWPLAYERACCEPTED, simpleDigest());
        methodMap.put(CHAT, chatCommand());
        methodMap.put(OPTIONS, optionCommand());
        methodMap.put(EXTENSIONS, setExtensionsCommand());
        methodMap.put(GETEXTENSIONS, getExtensionsCommand());
        methodMap.put(AVAILABLEPLAYERS, setAvailablePlayersCommand());
        methodMap.put(YOURECHALLENGED, simpleDigest());
        methodMap.put(YOUVECHALLENGED, simpleDigest());
        methodMap.put(CHALLENGEDENIED, challengeDeniedCommand());
        methodMap.put(CHALLENGEACCEPTED, challengeAcceptedCommand());
        methodMap.put(WAITFOROPPONENT, simpleDigest());
        methodMap.put(CANCELLED, simpleDigest());
        methodMap.put(GAMESTART, simpleDigest());
        methodMap.put(GAMEOVER, gameOverCommand());
        methodMap.put(FAILURE, failureCommand());
        methodMap.put(BOARD, boardCommand());
        
        methodMap.put(MOVE, moveCommand());
        
        methodMap.put(NEWPLAYER, newPlayerCommand());
        methodMap.put(PLAY, playCommand());
        methodMap.put(CANCEL, cancelCommand());
        methodMap.put(CHALLENGE, challengeCommand());
        methodMap.put(GETOPTIONS, getOptionsCommand());
	}
	
	protected Command gameOverCommand() {
		return new Command() {
			public void runCommand(Message message) throws GoException {
				client.digest(message);
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
	
	protected Command challengeCommand() {
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
	        				client.setWhosTurnItIs(Interpreter.color(message.args()[0]).opponent());
	        				client.getBoard().layStone(color, row, col);
						} catch (InvalidArgumentException e) {
							client.handleException(e);
						} catch (InvalidMoveException e) {
							System.err.println("Received an invalid move. Retreiving correct board from the server.");
							client.send(Presenter.getBoard());
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
				interactionController.showMenu(Interpreter.options(message));
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
	
	protected Command boardCommand() {
		return new Command() {
			public void runCommand(Message message) {
				try {
					client.setBoard(Interpreter.board(message));
					client.setBoardSize(client.getBoard().size());
				} catch (InvalidArgumentException e) {
					System.err.println("The received board message was invalid. This server is mad!");
				}
			}
		};
	}
	
	protected Command setAvailablePlayersCommand() {
		return new Command() {
			public void runCommand(Message message) {
				client.setAvailableOpponents(Arrays.asList(message.args()));
			}
		};
	}
	
	protected Command getOptionsCommand() {
		return new Command() {
			public void runCommand(Message message) {
				client.send(Presenter.getOptions());
			}
		};
	}

}
