package game;

import network.protocol.Message;

public abstract class Player {
	private String name;
	private Stone color;
	private int score;
	
	/**
	 * Create a new player with the given
	 * name.
	 * @param name
	 */
	public Player(String name) {
		this.name = name;
		score = 0;
	}
	
	/**
	 * Return the name of this Player.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public abstract void send(Message message);
	
	public abstract void takeTurn(Game game);
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public Stone getColor() {
		return color;
	}
	
	public void setColor(Stone stone) {
		color = stone;
	}
	
	public String toString() {
		return String.format("Player %s, plays with color %s", getName(), getColor().name());
	}
	
	private Stone myStone() {
		return getColor();
	}
	
}
