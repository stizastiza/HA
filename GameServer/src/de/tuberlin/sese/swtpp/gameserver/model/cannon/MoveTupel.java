package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

public class MoveTupel implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	char fromX, toX;
	int fromY, toY;
	/**
	 * CONSTRUCTOR
	 */
	public MoveTupel (char fromX, int fromY, char toX, int toY) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
	public boolean equals(MoveTupel another) {
		if (another.fromX == this.fromX && another.fromY == this.fromY && another.toX == this.toX && another.toY == this.toY) {
			return true;
		}
		return false;
	}
}
