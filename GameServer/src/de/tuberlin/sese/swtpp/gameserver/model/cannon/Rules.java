package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.*;

public class Rules implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	Map<Character, Integer> letter;
	char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
	// Location members:
	char fromX, toX;
	int fromY, toY;
	/**
	 * Constructor
	 */
	public Rules() {
		this.letter = new HashMap<Character, Integer>();
		letter.put('a', 0);
		letter.put('b', 1);
		letter.put('c', 2);
		letter.put('d', 3);
		letter.put('e', 4);
		letter.put('f', 5);
		letter.put('g', 6);
		letter.put('h', 7);
		letter.put('i', 8);
		letter.put('j', 9);
	}
	/**
	 * this method uses other boolean checks to give an exact answer back
	 * to tryMove():
	 * @param board
	 * @param moveString
	 * @return true/false
	 */
	public boolean MoveParser(CannonBoard board, String moveString) {
		// liegt moveString innerhalb des moglichen Bereiches, + isSoldier + isCity
		if (!this.parseMString(board, moveString)) {
			return false;
		}
		
		
		return true;	
	}
	
	public BoardSquare Soldier(CannonBoard board, char fromX, int fromY, char toX, int toY) {
		BoardSquare result = null;
		if (!this.isSoldier(board, fromX, fromY) && !this.SoldierCanMove(board, fromX, fromY, toX, toY)) {
			return null;
		}
		List<BoardPiece> legalSoldiers = new LinkedList<BoardPiece>();
		int toXnum = letter.get(toX); // TODO: to short: ubergebe sofort einen int
		BoardPiece Soldier;
		int soldierX, soldierY;
		int mod = board.currentMove == 'w' ? 1 : -1;
		List<Integer> Soldiers = board.getPiece(board.currentMove, fromX, -1);
		for (int i = 0; i<Soldiers.size(); i++) {
			Soldier = board.pieces.get(Soldiers.get(i));
			soldierX = letter.get(Soldier.square.x);
			soldierY = Soldier.square.y;
			// Check if soldier could move to the given square	
		}
		return result;
	}
	public boolean soldierMoves(CannonBoard board, String moveString) {
		// parseMString checks, if (1) it`s a soldier, (2) squares exist
		// and also sets all location members of the class:
		if (!this.parseMString(board ,moveString)) {
			return false;
		}
		// can a soldier move? 
		if (!this.SoldierCanMove(board, this.fromX, this.fromY, this.toX, this.toY)) {
			return false;
		}
		return true;
	}
	
	public BoardPiece City(CannonBoard board, char fromX, int fromY, char toX, int toY) {
		BoardPiece result = null;
		
		
		return result;
	}
	
	public boolean Capture(CannonBoard board, char toX, int toY) {
		boolean result = false;
		if (board.currentMove == 'w') {
			result = board.squares.get(toX)[toY].piece.name == 'b' ? true : false;
		} else {
			result = board.squares.get(toX)[toY].piece.name == 'w' ? true : false;
		}
		return result;
	}
	
	public boolean isSoldier(CannonBoard board, char fromX, int fromY) {
		//(0):
		boolean result0 = this.contains(board.signs, fromX) && this.contains(board.digits, fromY) ? true : false;
		//(1):
		boolean result = result0 && (board.squares.get(fromX)[fromY].piece.name == 'w' || board.squares.get(fromX)[fromY].piece.name == 'b') ? true : false;
		return result;
	}
	
	public boolean SoldierCanMove(CannonBoard board, char fromX, int fromY, char toX, int toY) {
		/* TODO: Regeln:
		 * (~) liegt toX und toY innerhalb des Spielfeldes?
		 * (0) Gehort der Zug der Menge der moglichen Moves?
		 * (1) Geht er nach hinten oder nach vorne, oder schlagt er?
		 * (2) Ist er blockiert? (FALLS ER NACH HINTEN ODER NACH VORNE !GEHT!)
		 * (2.1) (FALLS ER SCHLAGT): ist Capture moglich? this.Capture == true!
		 * (3) (FALLS ER NACH HINTEN GEHT) Ist er bedroht? Steht ihm jemand auf dem Weg?
		 */
		return false;
	}
	public boolean SoldierRetreats() {
		return false;
	}
	
	// TODO: Die Methode erstellt einen Map von moglichen Moves von allen Figuren auf dem Spielbrett.
	public Map<BoardPiece, BoardSquare[]> getLegalMoves(CannonBoard board) {
		Map<BoardPiece, BoardSquare[]> result = new HashMap<BoardPiece, BoardSquare[]>();
		for (BoardPiece Figure: board.pieces) {
			this.addMoves(result);
		}
		return null; 
	}
	
	public boolean parseMString(CannonBoard board ,String moveString) {
		if (moveString.length() != 5) {
			return false;
		}
		//TODO: mit dem IDE uberprufen, ob die Funktion das Richtige macht.
		char [] Positions = new char[5];
		moveString.getChars(0, moveString.length(), Positions, 0);
		//TODO: mit dem IDE uberprufen, ob ich einen char als int verwenden darf. Und ob die Verorderung und Verurderung klappt:
		if ((!isSoldier(board, Positions[0], Positions[1]) || !isCity(board, Positions[0], Positions[1])) && !this.contains(board.signs, Positions[3]) && !this.contains(board.digits, Positions[4])) {
			return false;
		}
		this.fromX = Positions[0];
		this.fromY = Positions[1];
		this.toX = Positions[3];
		this.toY = Positions[4];
		return true;
	}
	
	public boolean isCannone() {
		return false;
	}
	public boolean CannonCanShoot() {
		return false;
	}
	public boolean CannonCanMove() {
		return false;
	}
	
	public boolean contains(int[] arr, int item) {
		      for (int n : arr) {
		         if (item == n) {
		            return true;
		         }
		      }
		      return false;
		   }
	   public boolean contains(char[] arr, char item) {
		      for (char n : arr) {
		         if (item == n) {
		            return true;
		         }
		      }
		      return false;
		   }

}
