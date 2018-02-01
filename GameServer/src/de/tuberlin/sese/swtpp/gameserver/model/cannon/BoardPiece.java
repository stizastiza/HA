package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardPiece implements Serializable {
	String name; // 'b' 'w'
	String color; // aus dem Namen wissen wir, um welche Farbe es geht!
	BoardSquare square;
	
	public BoardPiece(String name, String color) {
		this.name = name; 
		this.color = color; 
	}
	
}

