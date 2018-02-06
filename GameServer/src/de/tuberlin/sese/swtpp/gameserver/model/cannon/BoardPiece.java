package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardPiece implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	char name; // 'b' 'w'
	BoardSquare square;
	
	public BoardPiece(char name) {
		this.name = name; 
	}
	
}

