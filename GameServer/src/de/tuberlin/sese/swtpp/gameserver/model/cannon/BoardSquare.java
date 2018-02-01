package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardSquare implements Serializable {
	char x;
	int y;
	BoardPiece piece;
	
	public BoardSquare(char x, int y) {
		this.x = x;
		this.y = y;
	}
}
