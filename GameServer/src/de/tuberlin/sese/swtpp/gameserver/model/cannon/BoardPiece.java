package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class BoardPiece implements Serializable {
	String name;
	String color;
	BoardSquare square;
	
	public BoardPiece(String name, String color) {
		this.name = name; 
		this.color = color; 
	}
	
}

