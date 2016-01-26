package network.protocol;

import java.util.Arrays;
import java.util.HashSet;

import exceptions.GoException;

public class CommandSet implements Constants {
	public static HashSet<String> supportedCommands = new HashSet<String>(Arrays.asList(VERSION, 
			NEWPLAYER, NEWPLAYERACCEPTED, GETOPTIONS, OPTIONS, GETEXTENSIONS, EXTENSIONS, PLAY,
			WAITFOROPPONENT, GAMESTART, MOVE, GETBOARD, BOARD, QUIT, GAMEOVER, CANCEL, STOPGAME,
			CHAT, CHALLENGE, AVAILABLEPLAYERS, YOUVECHALLENGED, YOURECHALLENGED, CANCELLED,
			GETHINT, HINT, AVAILABLESTRATEGIES, CHALLENGEACCEPTED, CHALLENGEDENIED, FAILURE));
	public static HashSet<String> supportedArguments = new HashSet<String>(Arrays.asList(WHITE, 
			BLACK, PASS, VICTORY, DEFEAT, DRAW, W, B, E, CHAT, CHALLENGE, OBSERVER, COMPUTERPLAYER));
	public static HashSet<String> notSupportedSet = new HashSet<String>(Arrays.asList(OBSERVER,
			COMPUTERPLAYER, OBSERVE, NOGAMESPLAYING, CURRENTGAMES, OBSERVEDGAME,
			PRACTICE, COMPUTER));
	public static HashSet<String> errors = new HashSet<String>(Arrays.asList(UNKNOWNCOMMAND,
			NOTAPPLICABLECOMMAND, ARGUMENTSMISSING, NOTSUPPORTEDCOMMAND,
			INVALIDNAME, NAMETAKEN, NAMENOTALLOWED, INVALIDMOVE, NOTYOURTURN,
			ILLEGALARGUMENT, OTHERPLAYERCANNOTCHAT, PLAYERNOTAVAILABLE,
			GAMENOTPLAYING));
	
	public static Boolean contains(String command) {
		return supported(command) || notSupportedSet.stream().anyMatch(c -> c.equals(command))
				|| errors.stream().anyMatch(c -> c.equals(command));
	}
	
	public static Boolean supportedCommand(String command) {
		return supportedCommands.stream().anyMatch(c -> c.equals(command));
	}
	
	public static Boolean supported(String word) {
		return supportedCommands.stream().anyMatch(c -> c.equals(word)) || 
				supportedArguments.stream().anyMatch(c -> c.equals(word));
	}
	
	public static String exceptionCommand(GoException e) {
		return errors.stream().filter(s -> e.toString().contains(s)).findFirst().orElse("");
	}
	
	public static HashSet<String> commandSet() {
		return supportedCommands;
	}
	
	public static HashSet<String> errorSet() {
		return errors;
	}

}
