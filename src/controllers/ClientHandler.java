package controllers;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import controllers.states.State;
import controllers.states.serverside.Challenged;
import controllers.states.serverside.NewClient;
import controllers.states.serverside.Playing;
import controllers.states.serverside.ReadyToPlay;
import controllers.states.serverside.StartPlaying;
import controllers.states.serverside.WaitForChallengeResponse;
import controllers.states.serverside.WaitingForOpponent;
import exceptions.CorruptedAuthorException;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import network.ClientCommunicator;
import network.Server;
import network.protocol.Message;
import network.protocol.Presenter;


public class ClientHandler implements FSM, network.protocol.Constants {	
	private ClientCommunicator clientCommunicator;
	private State state;
	private Server server;
	private GameController gameController;
	private String name;
	private List<String> extensions;
	private boolean alive;
	private ClientHandler opponent;
	
	// The states
	private State newClient;
	private State readyToPlay;
	private State waitingForOpponent;
	private State waitForChallengeResponse;
	private State challenged;
	private State startPlaying;
	private State playing;
	
	public ClientHandler(Server server, Socket socket) throws IOException {
		alive = true;
		name = null;
		extensions = new ArrayList<String>();
		this.clientCommunicator = new ClientCommunicator(this, socket);
		clientCommunicator.start();
		this.server = server;
		initializeStates();
		state = newClient;
	}

	/**
	 * Process the received message: send 
	 * @param message
	 * @throws NotApplicableCommandException
	 */
	public void process(Message message) throws NotApplicableCommandException {
		if (!currentState().applicable(message.command())) {
			throw new NotApplicableCommandException(message.command());
		}
		message.setAuthor(this);
		if (state.equals(playing) && gameController != null) {
			try {
				gameController.enqueue(message);
			} catch (CorruptedAuthorException e) {
				System.err.println(e.getMessage());
			}		
		} else {
			server.enqueue(message);
		}
	}
	
	public void digest(Message message) throws NotApplicableCommandException {
		currentState().leave(message);
		setState(currentState().accept(message));
		currentState().enter(message);
	}
	
	public void handleException(GoException e) {
		clientCommunicator.handleException(e);
	}
	
	public State currentState() {
		return state;
	}
	
	private void setState(State state) {
		this.state = state;
	}
	
	public boolean newClient() {
		return currentState().equals(newClient);
	}
	
	public boolean readyToPlay() {
		return currentState().equals(readyToPlay);
	}
	
	public boolean waitingForOpponent() {
		return currentState().equals(waitingForOpponent);
	}
	
	public boolean waitingForChallengeResponse() {
		return currentState().equals(waitForChallengeResponse);
	}
	
	public boolean isChallenged() {
		return currentState().equals(challenged);
	}
	
	public boolean canStartPlaying() {
		return currentState().equals(startPlaying);
	}
	
	public boolean isPlaying() {
		return currentState().equals(playing);
	}
	
	/**
	 * Set the gamecontroller if this client is
	 * playing (currentState().equals(new Playing()).
	 * @param gameController
	 */
	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}
	
	/**
	 * Set the gamecontroller to null (when client
	 * leaves the playing state).
	 */
	public void removeGameController() {
		gameController = null;
	}
	
	/**
	 * Send a message to the client.
	 * @param message
	 */
	public void send(Message message) {
		System.out.printf("Send to %s: %s%n", name(), message.toString());
		clientCommunicator.send(message);
	}
	
	/**
	 * Set the name of this client.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * The name with which this client has
	 * applied.
	 * @return Name
	 */
	public String name() {
		return name;
	}
	
	public ClientHandler getOpponent() {
		return opponent;
	}
	
	public void setOpponent(String name) {
		setOpponent(server.findClientByName(name));
	}
	
	public void setOpponent(ClientHandler client) {
		opponent = client;
	}
	
	public void removeOpponent() {
		opponent = null;
	}
	
	/**
	 * Gets this client's options.
	 * @return
	 */
	public List<String> getExtensions() {
		return extensions;
	}
	
	/**
	 * Set this client's options.
	 * @param options
	 */
	public void setExtensions(List<String> options) {
		if (options.contains(Presenter.chatOpt())) {
			enableChat();
		}
		if (options.contains(Presenter.challengeOpt())) {
			enableChallenge();
		}
	}
	
	/**
	 * Does this client have the option to chat
	 * @return
	 */
	public boolean canChat() {
		return extensions.contains(Presenter.chatOpt());
	}
	
	/**
	 * Does this client have the option to challenge
	 * @return
	 */
	public boolean canChallenge() {
		return extensions.contains(Presenter.challengeOpt());
	}
	
	
	
	/**
	 * The 'living' status of this client.
	 * @return
	 */
	public boolean dead() {
		return !alive;
	}
	
	/**
	 * Clientcommunicator or Server can
	 * mark this ClientHandler as dead for graceful
	 * execution.
	 */
	public void kill() {
		System.out.printf("Client %s has been killed.%n", name());
		alive = false;
	}
	
	/**
	 * For testing purposes: return this client's
	 * communicator.
	 * @return
	 */
	public ClientCommunicator clientCommunicator() {
		return clientCommunicator;
	}
	
	/**
	 * Setup the states, possible commands and
	 * state transitions.
	 */
	public void initializeStates() {
		newClient = new NewClient(this);
		readyToPlay = new ReadyToPlay(this);
		waitingForOpponent = new WaitingForOpponent(this);
		waitForChallengeResponse = new WaitForChallengeResponse(this);
		challenged = new Challenged(this);
		startPlaying = new StartPlaying(this);
		playing = new Playing(this);
		
		HashSet<State> allStates = new HashSet<>();
		allStates.addAll(Arrays.asList(newClient, readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, startPlaying, playing));
		
		allStates.stream().forEach(state -> state.addCommand(GETOPTIONS));
		allStates.stream().forEach(state -> state.addCommand(OPTIONS));
		allStates.stream().forEach(state -> state.addCommand(GETEXTENSIONS));
		allStates.stream().forEach(state -> state.addCommand(EXTENSIONS));
		allStates.stream().forEach(state -> state.addCommand(FAILURE));
		allStates.stream().forEach(state -> state.addCommand(QUIT));
		
		newClient.addCommand(NEWPLAYER);
		newClient.addTransition(NEWPLAYER, readyToPlay);
		
		readyToPlay.addCommand(PLAY);
		readyToPlay.addTransition(PLAY, waitingForOpponent);
		
		waitingForOpponent.addCommand(CANCEL);
		waitingForOpponent.addTransition(CANCELLED, readyToPlay);
		waitingForOpponent.addTransition(PLAY, startPlaying);
		waitingForOpponent.addTransition(QUIT, readyToPlay);
		
		startPlaying.addTransition(GAMESTART, playing);
		
		playing.addCommand(MOVE);
		playing.addCommand(GETBOARD);
		playing.addCommand(STOPGAME);
		playing.addTransition(GAMEOVER, readyToPlay);
	}
	
	public void enableChat() {
		if (!extensions.contains(Presenter.chatOpt())) {
			extensions.add(Presenter.chatOpt());
		}
		
		HashSet<State> activeStates = new HashSet<>();
		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, playing));	
		
		activeStates.stream().forEach(state -> state.addCommand(CHAT));
	}
	
	public void enableChallenge() {
		if (!extensions.contains(Presenter.challengeOpt())) {
			extensions.add(Presenter.challengeOpt());
		}
		
		readyToPlay.addCommand(CHALLENGE);
		readyToPlay.addTransition(YOUVECHALLENGED, waitForChallengeResponse);
		readyToPlay.addTransition(YOURECHALLENGED, challenged);
		
		waitForChallengeResponse.addCommand(CANCEL);
		waitForChallengeResponse.addTransition(CANCELLED, readyToPlay);
		waitForChallengeResponse.addTransition(CHALLENGEACCEPTED, startPlaying);
		waitForChallengeResponse.addTransition(CHALLENGEDENIED, readyToPlay);
		
		challenged.addCommand(CHALLENGEACCEPTED);
		challenged.addCommand(CHALLENGEDENIED);
		challenged.addTransition(CANCELLED, readyToPlay);
		challenged.addTransition(CHALLENGEACCEPTED, startPlaying);
		challenged.addTransition(CHALLENGEDENIED, readyToPlay);
	}
	
}
