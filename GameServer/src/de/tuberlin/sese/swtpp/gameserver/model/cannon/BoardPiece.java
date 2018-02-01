package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardPiece implements Serializable {
	char name; // 'b' 'w'
	BoardSquare square;
	
	public BoardPiece(char name) {
		this.name = name; 
	}
	
}

