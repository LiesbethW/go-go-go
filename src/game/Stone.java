package game;

public enum Stone {
	BLACK, WHITE, NONE;
	
	/**
	 * Is this stone the opponent of the
	 * given stone?
	 * @param stone
	 * @return True if this is white and the
	 * given stone is black or vice versa. Return
	 * false in all other situations.
	 */
	public Boolean isOpponent(Stone stone) {
		if (this == NONE || stone == NONE) {
			return false;
		} else if (stone == this) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Get the opponent Stone.
	 */
	public Stone opponent() {
		if (this == WHITE) {
			return BLACK;
		} else if (this == BLACK) {
			return WHITE;
		} else {
			return NONE;
		}
	}
	
	/**
	 * A string representation of this stone
	 * on behalf of the TUI.
	 */
	public String toString() {
		if (this == WHITE) {
			return "O";
		} else if (this == BLACK) {
			return String.format("%c", (char) 0x2588);
		} else {
			return String.format("%c", (char) 0x00B7);
		}
	}
	
}
