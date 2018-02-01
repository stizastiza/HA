package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardPiece implements Serializable {
	String name; // 'b' 'w'
	BoardSquare square;
	
	public BoardPiece(String name) {
		this.name = name; 
	}
	
}

