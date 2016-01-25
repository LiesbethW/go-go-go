package controllers;

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
import network.Client;
import network.protocol.Constants;
import network.protocol.Message;
import network.protocol.Presenter;
import userinterface.TUIView;
import userinterface.View;

public class ClientSideClientController implements FSM, Constants {
	public static List<String> SUPPORTED_OPTIONS = new ArrayList<String>(
			Arrays.asList(Presenter.chatOpt(), Presenter.challengeOpt()));
	
	private Client client;
	private State state;
	private View view;
	private List<String> enabledOptions;
	private ClientCommandHandler commandHandler;
	
	// The states
	private State newClient;
	private State readyToPlay;
	private State waitingForOpponent;
	private State waitForChallengeResponse;
	private State challenged;
	private State startPlaying;
	private State playing;
	
	public ClientSideClientController(Client client) {
		this.client = client;
		initializeStates();
		state = newClient;
		enabledOptions = new ArrayList<String>();
		view = new TUIView(System.out);
		commandHandler = new ClientCommandHandler(this, view);
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
		client.send(message);
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
		
		activeStates.stream().forEach(state -> state.addTransition(OPTIONS, state));
		activeStates.stream().forEach(state -> state.addTransition(CHAT, state));
		
		HashSet<State> allStates = new HashSet<>(activeStates);
		allStates.add(newClient);
		
		allStates.stream().forEach(state -> state.addCommand(GETOPTIONS));
		allStates.stream().forEach(state -> state.addCommand(OPTIONS));
		allStates.stream().forEach(state -> state.addCommand(QUIT));
		allStates.stream().forEach(state -> state.addTransition(FAILURE, state));
		
		newClient.addCommand(NEWPLAYER);
		newClient.addTransition(NEWPLAYERACCEPTED, readyToPlay);
		
		readyToPlay.addCommand(PLAY);
		readyToPlay.addTransition(WAITFOROPPONENT, waitingForOpponent);
		
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
		
		waitForChallengeResponse.addTransition(CHALLENGEDENIED, readyToPlay);
		waitForChallengeResponse.addTransition(CHALLENGEACCEPTED, startPlaying);	
		
		challenged.addCommand(CHALLENGEACCEPTED);
		challenged.addCommand(CHALLENGEDENIED);
		challenged.addTransition(CHALLENGEDENIED, readyToPlay);
		challenged.addTransition(CHALLENGEACCEPTED, startPlaying);
		
		startPlaying.addTransition(GAMESTART, playing);
	}
}
