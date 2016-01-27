package controllers;

import java.util.Arrays;
import java.util.HashMap;

import exceptions.GoException;
import exceptions.NotSupportedCommandException;
import network.protocol.Constants;
import network.protocol.Interpreter;
import network.protocol.Message;
import network.protocol.Presenter;
import userinterface.View;

public class ClientCommandHandler implements Constants {
	private Client client;
	private View view;
	private HashMap<String, Command> methodMap;	
	
	public ClientCommandHandler(Client client, View view) {
		this.client = client;
		this.view = view;
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
			message.author().handleException(e);
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
        methodMap.put(CHALLENGEDENIED, simpleDigest());
        methodMap.put(CHALLENGEACCEPTED, simpleDigest());
        methodMap.put(WAITFOROPPONENT, simpleDigest());
        methodMap.put(CANCELLED, simpleDigest());
        methodMap.put(GAMESTART, simpleDigest());
        methodMap.put(FAILURE, failureCommand());
	}
	
	protected Command chatCommand() {
		return new Command() {
            public void runCommand(Message message) { 
            	view.showMessage(String.join(DELIMITER, message.args()));
            }
        };
	}
	
	protected Command optionCommand() {
		return new Command() {
			public void runCommand(Message message) {
				view.showOptions(Interpreter.options(message));
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
				if (Arrays.asList(message.args()).contains(Presenter.chatOpt())) {
					client.enableChat();
				}
				if (Arrays.asList(message.args()).contains(Presenter.challengeOpt())) {
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
