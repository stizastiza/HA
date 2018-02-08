package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardSquare implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	char x;
	int y;
	BoardPiece piece;
	
	public BoardSquare(char x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean Equals(char x, int y) {
		if (this.x == x && this.y == y) {
			return true;
		}
		return false;
	}
}
