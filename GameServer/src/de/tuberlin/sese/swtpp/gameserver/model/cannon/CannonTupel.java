package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CannonTupel implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	
	List<BoardPiece> members;
	
	public CannonTupel(BoardPiece one, BoardPiece two, BoardPiece three) {
		this.members = new LinkedList<BoardPiece>();
		members.add(one);
		members.add(two);
		members.add(three);
	}
	
	
	
	
	
	
	
}
