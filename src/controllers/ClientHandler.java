package controllers;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import controllers.states.AbstractServerSideClientState;
import controllers.states.State;
import controllers.states.serverside.Challenged;
import controllers.states.serverside.NewClient;
import controllers.states.serverside.Playing;
import controllers.states.serverside.ReadyToPlay;
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
	private HashMap<String, Boolean> options;
	private boolean alive;
	
	// The states
	private AbstractServerSideClientState newClient;
	private AbstractServerSideClientState readyToPlay;
	private AbstractServerSideClientState waitingForOpponent;
	private AbstractServerSideClientState waitForChallengeResponse;
	private AbstractServerSideClientState challenged;
	private AbstractServerSideClientState playing;
	
	public ClientHandler(Server server, Socket socket) throws IOException {
		alive = true;
		name = null;
		options = new HashMap<String, Boolean>();
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
		if (!state.applicable(message.command())) {
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
		state = currentState().accept(message);
		currentState().accept(message);
	}
	
	public void handleException(GoException e) {
		clientCommunicator.handleException(e);
	}
	
	public State currentState() {
		return state;
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
	 * Send a message to the client.
	 * @param message
	 */
	public void send(Message message) {
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
	
	/**
	 * Gets this client's options.
	 * @return
	 */
	public HashMap<String, Boolean> getOptions() {
		return options;
	}
	
	/**
	 * Set this client's options.
	 * @param options
	 */
	public void setOptions(HashMap<String, Boolean> options) {
		this.options = options;
	}
	
	/**
	 * Does this client have the option to chat
	 * @return
	 */
	public boolean canChat() {
		return options.containsKey(Presenter.chatOpt()) && options.get(Presenter.chatOpt());
	}
	
	/**
	 * Does this client have the option to challenge
	 * @return
	 */
	public boolean canChallenge() {
		return options.containsKey(Presenter.challengeOpt()) && options.get(Presenter.challengeOpt());
	}
	
	
	
	/**
	 * The 'living' status of this client.
	 * @return
	 */
	public boolean alive() {
		return alive;
	}
	
	/**
	 * Clientcommunicator or Server can
	 * mark this ClientHandler as dead for graceful
	 * execution.
	 */
	public void kill() {
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
		playing = new Playing(this);
		
		HashSet<AbstractServerSideClientState> activeStates = new HashSet<>();
		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, playing));
		
		activeStates.stream().forEach(state -> state.addCommand(GETOPTIONS));
		activeStates.stream().forEach(state -> state.addCommand(OPTIONS));
		activeStates.stream().forEach(state -> state.addCommand(CHAT));
		
		HashSet<AbstractServerSideClientState> allStates = new HashSet<>(activeStates);
		allStates.add(newClient);
		
		allStates.stream().forEach(state -> state.addCommand(FAILURE));
		activeStates.stream().forEach(state -> state.addCommand(QUIT));
		
		newClient.addCommand(NEWPLAYER);
		newClient.addTransition(NEWPLAYER, readyToPlay);
		
		readyToPlay.addCommand(CHALLENGE);
		readyToPlay.addCommand(PLAY);
		readyToPlay.addTransition(PLAY, waitingForOpponent);
		readyToPlay.addTransition(YOUVECHALLENGED, waitForChallengeResponse);
		readyToPlay.addTransition(YOURECHALLENGED, challenged);
		
		waitingForOpponent.addTransition(PLAY, playing);
		waitingForOpponent.addTransition(QUIT, readyToPlay);
		
		waitForChallengeResponse.addTransition(CHALLENGEACCEPTED, playing);
		waitForChallengeResponse.addTransition(CHALLENGEDENIED, readyToPlay);
		
		challenged.addCommand(CHALLENGEACCEPTED);
		challenged.addCommand(CHALLENGEDENIED);
		challenged.addTransition(CHALLENGEACCEPTED, playing);
		challenged.addTransition(CHALLENGEDENIED, readyToPlay);
		
		playing.addCommand(MOVE);
		playing.addCommand(GETBOARD);
		playing.addCommand(TERRITORYSCORING);
		playing.addTransition(QUIT, readyToPlay);
	}
	
}
