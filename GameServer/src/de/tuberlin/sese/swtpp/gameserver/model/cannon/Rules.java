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
	public boolean MoveParser(CannonBoard board, String moveString, int MoveCount) {
		if (MoveCount < 2) {
			if (!parseCity(board, moveString)) {
				return false;
			}
			return true;
		} else {
			boolean m = this.parseMSoldier(board, moveString); //versuchen eine Figur zu bewegen? liegt sie auf dem Spielbrett?
			boolean result = m == false ? false : this.SoldierCanMove(board, this.fromX, this.fromY, this.toX, this.toY) ? true : false;
			return result;
		}
	}
	public boolean parseCity(CannonBoard board ,String moveString){
		if (moveString.length() != 5) {
			return false;
		}
		char [] Positions = new char[5];
		moveString.getChars(0, moveString.length(), Positions, 0);
		BoardSquare Position1 = new BoardSquare(Positions[0], Character.getNumericValue(Positions[1]));
		BoardSquare Position2 = new BoardSquare(Positions[3], Character.getNumericValue(Positions[4]));
		if (board.currentMove == 'w' && (!Position1.Equals(Position2.x, Position2.y) || !(letter.get(Position1.x) > 0) || !(letter.get(Position1.x) < 9) || Position1.y != 9)) {
			return false;
		}
		if (board.currentMove == 'b' && (!Position1.Equals(Position2.x, Position2.y) || !(letter.get(Position1.x) > 0) || !(letter.get(Position1.x) < 9) || Position1.y != 0)) {
			return false;
		}
		return true;
	}
	public boolean parseMSoldier(CannonBoard board ,String moveString) {
		if (moveString.length() != 5) {
			return false;
		}
		char [] Positions = new char[5];
		moveString.getChars(0, moveString.length(), Positions, 0);
		if (Positions[2] != '-') {
			return false;
		}
		if (board.currentMove == 'w' && (!this.isSoldier(board, Positions[0], Character.getNumericValue(Positions[1])) || board.squares.get(Positions[0])[Character.getNumericValue(Positions[1])].piece.name != 'w') ) {
			return false;
		}
		if (board.currentMove == 'b' && (!this.isSoldier(board, Positions[0], Character.getNumericValue(Positions[1])) || board.squares.get(Positions[0])[Character.getNumericValue(Positions[1])].piece.name != 'b')  ) {
			return false;
		}
		this.fromX = Positions[0];
		this.fromY = Positions[1];
		this.toX = Positions[3];
		this.toY = Positions[4];
		return true;
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
		 * (0) Gehort der Zug der Menge der moglichen Moves? TODO: getLegalMoves
		 */
		
		return false;
	}
	
	public boolean SoldierRetreats() {
		return false;
	}
	
	
	public List<MoveTupel> getLegalMoves(CannonBoard board) {
		List<MoveTupel> result = new LinkedList<MoveTupel>();
		for (BoardPiece p: board.pieces) {
			if (p.name != board.currentMove) {
				continue;
			}
			// for each of 8(?) possible moves, check:
			int mod = board.currentMove == 'w' ? -1 : 1;
			List<MoveTupel> Moves = this.constructPossibleMoves(board, p, mod);
			for (MoveTupel move: Moves) {
				if (move != null) {
					result.add(move);
				}
			}
		}
		return result; 
	}
	//TODO: + Cannonen!
	public List<MoveTupel> constructPossibleMoves(CannonBoard board, BoardPiece p, int mod) {
		// mit Koordinaten von p alle moeglichen Bewegungen angeben und die der String[] Array hinzufugen und zuruckgeben!
		/*
		 * (1) Geht er nach hinten oder nach vorne, oder schlagt er?
		 * (2) Ist er blockiert? (FALLS ER NACH HINTEN ODER NACH VORNE !GEHT!)
		 * (2.1) (FALLS ER SCHLAGT): ist Capture moglich? this.Capture == true!
		 * (3) (FALLS ER NACH HINTEN GEHT) Ist er bedroht? Steht ihm jemand auf dem Weg?
		 */
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		possibleMoves.addAll(this.getFrontalMoves(board, p, mod));
		possibleMoves.addAll(this.getSideMoves(board, p, mod));
		possibleMoves.addAll(this.getRetreatMoves(board, p, mod));
		return possibleMoves;
	}
	public List<MoveTupel> getFrontalMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		/*(1)*/ char position1x = this.getKey(this.letter.get(x)-mod);
				int position1y = y+mod;
				if (position1x != '0' && position1y>0 && position1y<9 && board.squares.get(position1x)[position1y].piece.name != p.name) {
					MoveTupel
					possibleMoves.add()
				}
		/*(2)*/ char position2x = x;
				int position2y = y+mod;
				
		/*(3)*/ char position3x = this.getKey(this.letter.get(x)+mod);
				int position3y = y+mod;
				
		return possibleMoves;
	}
	public List<MoveTupel> getSideMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		/*(4)*/ char position4x = this.getKey(this.letter.get(x)-mod);
				int position4y = y;
				if (position4x != '0' && position4y>0 && position4y<9 && board.squares.get(position4x)[position4y].piece.name != p.name && board.squares.get(position4x)[position4y].piece != null) {
					MoveTupel e4 = new MoveTupel(x, y, position4x, position4y);
					possibleMoves.add(e4);
				}
		/*(5)*/ char position5x = this.getKey(this.letter.get(x)-mod);
				int position5y = y;
				if (position5x != '0' && position5y>0 && position5y<9 && board.squares.get(position5x)[position5y].piece.name != p.name && board.squares.get(position5x)[position5y].piece != null) {
					MoveTupel e5 = new MoveTupel(x, y, position5x, position5y);
					possibleMoves.add(e5);
				}
		return possibleMoves;
	}
	
	public List<MoveTupel> getRetreatMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		
		return possibleMoves;
	}
	
	
	/*
	public boolean isCannone() {
		return false;
	}
	public boolean CannonCanShoot() {
		return false;
	}
	public boolean CannonCanMove() {
		return false;
	}
	*/
	
	
	
	// Coordination:
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
	 public char getKey(int value) {
		 for (char i: this.letter.keySet()) {
			 if (this.letter.get(i) == value) {
				 return i;
			 }
		 }
		 return '0';
	 }

}
