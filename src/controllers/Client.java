package controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import controllers.states.State;
import controllers.states.clientside.Challenged;
import controllers.states.clientside.NewClient;
import controllers.states.clientside.Playing;
import controllers.states.clientside.ReadyToPlay;
import controllers.states.clientside.StartPlaying;
import controllers.states.clientside.WaitForChallengeResponse;
import controllers.states.clientside.WaitingForOpponent;
import exceptions.GoException;
import exceptions.NotApplicableCommandException;
import game.Board;
import game.Stone;
import network.ServerCommunicator;
import network.protocol.Constants;
import network.protocol.Message;
import network.protocol.Presenter;
import userinterface.InteractionController;

public class Client extends Observable implements FSM, Constants {
	public static List<String> SUPPORTED_EXTENSIONS = new ArrayList<String>(
			Arrays.asList(Presenter.chatOpt(), Presenter.challengeOpt()));
	private static final String USAGE
    		= "usage: java controllers.Client <address> <port>";

	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		InetAddress host= null;
		int port = 0;
	
		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: no valid hostname!");
			System.exit(0);
		}
	
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("ERROR: no valid portnummer!");
			System.exit(0);
		}
	
		try {
			Client client = new Client(host, port);
			client.run();
		} catch (IOException e) {
			System.out.println("ERROR: couldn't construct a client object!");
			System.exit(0);
		}
	
	}
	
	// Client attributes
	private ServerCommunicator communicator;
	private State state;
	private ConcurrentLinkedQueue<Message> commandQueue;
	private ClientCommandHandler commandHandler;
	private InteractionController interactionController;
	private List<String> enabledExtensions;
	
	// The states
	private State newClient;
	private State readyToPlay;
	private State waitingForOpponent;
	private State waitForChallengeResponse;
	private State challenged;
	private State startPlaying;
	private State playing;
	
	// Player attributes
	private String name;
	private String opponent;
	private Stone color;
	private int boardSize;
	private Board board;
	private Stone whosTurn;
	private List<String> chatMessages;
	
	public Client(InetAddress host, int port) throws IOException {
		this.communicator = new ServerCommunicator(host, port, this);
		initializeStates();
		state = newClient;
		enabledExtensions = new ArrayList<String>();
		interactionController = new InteractionController(this);
		addObserver(interactionController);
		commandQueue = new ConcurrentLinkedQueue<Message>();
		commandHandler = new ClientCommandHandler(this, interactionController);
	}
	
	/**
	 * Check the queue for new commands. If there is a command, let it
	 * be processed by the commandhandler and notify observers.
	 */
	public void run() {
		communicator.start();
		interactionController.start();
		setChanged();
		notifyObservers(null);
		while (true) {
			if (commandQueue.peek() != null) {
				Message message = commandQueue.poll();
				commandHandler.process(message);
				setChanged();
				notifyObservers(message);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}

	@Override
	public void digest(Message message) throws NotApplicableCommandException {
		currentState().leave(message);
		setState(currentState().accept(message));
		currentState().enter(message);			
	}
	
	/**
	 * Check if the message is applicable in the current state, then
	 * place it on the queue for the commandhandler.
	 * @param message
	 * @throws NotApplicableCommandException
	 */
	public void process(Message message) throws NotApplicableCommandException {
		if (!currentState().applicable(message.command())) {
			throw new NotApplicableCommandException(message.command(), 
					currentState());
		} else {
			enqueue(message);
		}
	}
	
	/**
	 * Enqueue a new message for the command handler.
	 * @param message
	 */
	public void enqueue(Message message) {
		commandQueue.add(message);
	}
	
	/**
	 * Send a message to the server.
	 * @param message
	 */
	public void send(Message message) {
		communicator.send(message);
	}

	/**
	 * Return the current State of the application.
	 */
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
	
	public boolean canStartPlaying() {
		return currentState().equals(startPlaying);
	}
	
	public boolean isPlaying() {
		return currentState().equals(playing);
	}
	
	/**
	 * Get the options for the user.
	 * @return
	 */
	public HashSet<String> getOptions() {
		HashSet<String> allCommands = currentState().applicableCommands();
		allCommands.remove(Presenter.EXTENSIONS);
		allCommands.remove(Presenter.GETEXTENSIONS);
		allCommands.remove(Presenter.OPTIONS);
		allCommands.remove(Presenter.GETOPTIONS);
		allCommands.remove(Presenter.FAILURE);
		return allCommands;
	}
	
	public String name() {
		return name;
	}
	
	public void setPlayerName(String name) {
		this.name = name;
	}
	
	public Stone getColor() {
		return color;
	}
	
	public void setColor(Stone color) {
		this.color = color;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public int boardSize() {
		return this.boardSize;
	}
	
	public void setBoardSize(int size) {
		this.boardSize = size;
	}	
	
	public String opponent() {
		return this.opponent;
	}
	
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
	
	public void letBlackBegin() {
		this.whosTurn = Stone.BLACK;
	}
	
	public void setWhosTurnItIs(Stone color) {
		this.whosTurn = color;
	}
	
	public boolean myTurn() {
		return this.color == this.whosTurn;
	}
	
	/**
	 * Does this client have the option to chat
	 * @return
	 */
	public boolean canChat() {
		return enabledExtensions.contains(Presenter.chatOpt());
	}
	
	public List<String> latestChatMessages() {
		if (chatMessages.size() < 5) {
			return chatMessages;
		} else {
			return chatMessages.subList(chatMessages.size() - 5, chatMessages.size());			
		}
	}
	
	public void addChatMessage(String chatMessage) {
		this.chatMessages.add(chatMessage);
	}
	
	public void handleException(GoException e) {
		System.err.println(e.getMessage());
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
				waitForChallengeResponse, challenged, startPlaying, playing));
		
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
		
		waitingForOpponent.addCommand(CANCEL);
		waitingForOpponent.addTransition(CANCELLED, readyToPlay);
		waitingForOpponent.addTransition(GAMESTART, playing);
		
		playing.addCommand(MOVE);
		playing.addTransition(MOVE, playing);
		playing.addTransition(BOARD, playing);
		playing.addTransition(GAMEOVER, readyToPlay);
	}
	
	public void enableChat() {
		enabledExtensions.add(Presenter.chatOpt());
		
		chatMessages = new ArrayList<String>();
		
		HashSet<State> activeStates = new HashSet<>();
		activeStates.addAll(Arrays.asList(readyToPlay, waitingForOpponent, 
				waitForChallengeResponse, challenged, startPlaying, playing));	
		
		activeStates.stream().forEach(state -> state.addCommand(CHAT));		
	}
	
	public void enableChallenge() {
		enabledExtensions.add(Presenter.challengeOpt());
		
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
