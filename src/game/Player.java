package game;

import network.protocol.Message;

public abstract class Player {
	private String name;
	private Stone color;
	
	/**
	 * Create a new player with the given
	 * name.
	 * @param name
	 */
	public Player(String name) {
		this.name = name;
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
