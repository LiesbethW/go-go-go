package controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import controllers.states.State;
import controllers.states.clientside.Challenged;
import controllers.states.clientside.NewClient;
import controllers.states.clientside.Playing;
import controllers.states.clientside.ReadyToPlay;
import controllers.states.clientside.StartPlaying;
import controllers.states.clientside.WaitForChallengeResponse;
import controllers.states.clientside.WaitingForOpponent;
import exceptions.NotApplicableCommandException;
import game.Stone;
import network.ServerCommunicator;
import network.protocol.Constants;
import network.protocol.Message;
import network.protocol.Presenter;
import userinterface.TUIView;
import userinterface.View;

public class Client implements FSM, Constants {
	public static List<String> SUPPORTED_EXTENSIONS = new ArrayList<String>(
			Arrays.asList(Presenter.chatOpt(), Presenter.challengeOpt()));
	
	// Client attributes
	private ServerCommunicator communicator;
	private State state;
	private View view;
	private List<String> enabledOptions;
	private ClientCommandHandler commandHandler;
	
	// Player attributes
	private String name;
	private Stone color;
	private int boardSize;
	
	// The states
	private State newClient;
	private State readyToPlay;
	private State waitingForOpponent;
	private State waitForChallengeResponse;
	private State challenged;
	private State startPlaying;
	private State playing;
	
	public Client(InetAddress host, int port) throws IOException {
		this.communicator = new ServerCommunicator(host, port, this);
		initializeStates();
		state = newClient;
		enabledOptions = new ArrayList<String>();
		view = new TUIView(System.out);
		commandHandler = new ClientCommandHandler(this, view);
		communicator.start();
	}

	@Override
	public void digest(Message message) throws NotApplicableCommandException {
		currentState().leave(message);
		setState(currentState().accept(message));
		currentState().enter(message);			
	}
	
	public void process(Message message) throws NotApplicableCommandException {
		if (!currentState().applicable(message.command())) {
			throw new NotApplicableCommandException(message.command(), 
					currentState());
		} else {
			commandHandler.process(message);
		}
	}
	
	public void send(Message message) {
		communicator.send(message);
	}

	@Override
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
	
	public boolean isPlaying() {
		return currentState().equals(playing);
	}	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Stone getColor() {
		return color;
	}
	
	public void setColor(Stone color) {
		this.color = color;
	}
	
	public int boardSize() {
		return this.boardSize;
	}
	
	public void setBoardSize(int size) {
		this.boardSize = size;
	}

	public void initializeStates() {
		newClient = new NewClient(this);
		readyToPlay = new ReadyToPlay(this);
		waitingForOpponent = new WaitingForOpponent(this);
		waitForChallengeResponse = new WaitForChallengeResponse(this);
		challenged = new Challenged(this);
		startPlaying = new StartPlaying(this);
		playing = new Playing(this);
		
		HashSet<State> activeStates = new HashSet<>();
		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, playing));
		
		activeStates.stream().forEach(state -> state.addTransition(CHAT, state));
		
		HashSet<State> allStates = new HashSet<>(activeStates);
		allStates.add(newClient);
		
		allStates.stream().forEach(state -> state.addCommand(GETEXTENSIONS));
		allStates.stream().forEach(state -> state.addCommand(EXTENSIONS));
		allStates.stream().forEach(state -> state.addCommand(GETOPTIONS));
		allStates.stream().forEach(state -> state.addCommand(OPTIONS));
		allStates.stream().forEach(state -> state.addCommand(QUIT));
		allStates.stream().forEach(state -> state.addTransition(FAILURE, state));
		allStates.stream().forEach(state -> state.addTransition(EXTENSIONS, state));
		allStates.stream().forEach(state -> state.addTransition(GETEXTENSIONS, state));
		allStates.stream().forEach(state -> state.addTransition(OPTIONS, state));
		allStates.stream().forEach(state -> state.addTransition(GETOPTIONS, state));
		
		newClient.addCommand(NEWPLAYER);
		newClient.addTransition(NEWPLAYERACCEPTED, readyToPlay);
		
		readyToPlay.addCommand(PLAY);
		readyToPlay.addTransition(WAITFOROPPONENT, waitingForOpponent);
		
		waitingForOpponent.addTransition(CANCELLED, readyToPlay);
		waitingForOpponent.addTransition(GAMESTART, playing);
		
		playing.addCommand(MOVE);
		playing.addCommand(GETBOARD);
		playing.addTransition(MOVE, playing);
		playing.addTransition(BOARD, playing);
		playing.addTransition(GAMEOVER, readyToPlay);
	}
	
	public void enableChat() {
		enabledOptions.add(Presenter.chatOpt());
		
		HashSet<State> activeStates = new HashSet<>();
		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, playing));	
		
		activeStates.stream().forEach(state -> state.addCommand(CHAT));		
	}
	
	public void enableChallenge() {
		enabledOptions.add(Presenter.challengeOpt());
		
		readyToPlay.addCommand(CHALLENGE);
		readyToPlay.addTransition(YOUVECHALLENGED, waitForChallengeResponse);
		readyToPlay.addTransition(YOURECHALLENGED, challenged);
		
		waitForChallengeResponse.addCommand(CANCEL);
		waitForChallengeResponse.addTransition(CHALLENGEDENIED, readyToPlay);
		waitForChallengeResponse.addTransition(CHALLENGEACCEPTED, startPlaying);
		waitForChallengeResponse.addTransition(CANCELLED, readyToPlay);
		
		challenged.addCommand(CHALLENGEACCEPTED);
		challenged.addCommand(CHALLENGEDENIED);
		challenged.addTransition(CHALLENGEDENIED, readyToPlay);
		challenged.addTransition(CHALLENGEACCEPTED, startPlaying);
		challenged.addTransition(CANCELLED, readyToPlay);
		
		startPlaying.addTransition(GAMESTART, playing);
	}
}
