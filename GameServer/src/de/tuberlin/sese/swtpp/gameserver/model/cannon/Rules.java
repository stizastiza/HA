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
		MoveTupel t = new MoveTupel(fromX, fromY, toX, toY);
		if (this.getLegalMoves(board).contains(t)) {
			return true;
		}
		return false;
	}

	public List<MoveTupel> getLegalMoves(CannonBoard board) {
		List<MoveTupel> result = new LinkedList<MoveTupel>();
		for (BoardPiece p: board.pieces) {
			if (p.name != board.currentMove) {
				continue;
			}
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
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		possibleMoves.addAll(this.getFrontalMoves(board, p, mod));
		possibleMoves.addAll(this.getSideMoves(board, p, mod));
		possibleMoves.addAll(this.getRetreatMoves(board, p, mod));
		if (!this.isInCannon(board, p, mod).isEmpty()) {
			possibleMoves.addAll(this.getCannonFrontalMoves(board, p, mod));
			possibleMoves.addAll(this.getCannonRetreatMoves(board, p, mod));
			possibleMoves.addAll(this.getCannonDiagonalMoves(board, p, mod));
			possibleMoves.addAll(this.getCannonSideMoves(board, p, mod));
		}
		return possibleMoves;
	}
	public List<MoveTupel> getFrontalMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		/*(1)*/ char position1x = this.getKey(this.letter.get(x)-mod);
				int position1y = y+mod;
				if (position1x != '0' && position1y>=0 && position1y<=9 && board.squares.get(position1x)[position1y].piece.name != p.name) {
					MoveTupel a = new MoveTupel(x, y, position1x, position1y);
					possibleMoves.add(a);
				}
		/*(2)*/ char position2x = x;
				int position2y = y+mod;
				if (position2y>=0 && position2y<=9 && board.squares.get(position2x)[position2y].piece.name != p.name) {
					MoveTupel b = new MoveTupel(x, y, position2x, position2y);
					possibleMoves.add(b);
				}
		/*(3)*/ char position3x = this.getKey(this.letter.get(x)+mod);
				int position3y = y+mod;
				if (position3x != '0' && position3y>=0 && position3y<=9 && board.squares.get(position3x)[position3y].piece.name != p.name) {
					MoveTupel c = new MoveTupel(x, y, position3x, position3y);
					possibleMoves.add(c);
				}
		return possibleMoves;
	}
	public List<MoveTupel> getSideMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		/*(4)*/ char position4x = this.getKey(this.letter.get(x)-mod);
				int position4y = y;
				if (position4x != '0' && position4y>=0 && position4y<=9 && board.squares.get(position4x)[position4y].piece.name != p.name && board.squares.get(position4x)[position4y].piece != null) {
					MoveTupel e4 = new MoveTupel(x, y, position4x, position4y);
					possibleMoves.add(e4);
				}
		/*(5)*/ char position5x = this.getKey(this.letter.get(x)+mod);
				int position5y = y;
				if (position5x != '0' && position5y>=0 && position5y<=9 && board.squares.get(position5x)[position5y].piece.name != p.name && board.squares.get(position5x)[position5y].piece != null) {
					MoveTupel e5 = new MoveTupel(x, y, position5x, position5y);
					possibleMoves.add(e5);
				}
		return possibleMoves;
	}
	
	public List<MoveTupel> getRetreatMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		char position6x = this.getKey(this.letter.get(x)-(2*mod));
		int position6y = y-(2*mod);
		char position6x1 = this.getKey(this.letter.get(x)-(mod));
				if (menace(board, p, mod) && position6x != '0' && position6y>=0 && position6y<=9 && board.squares.get(position6x)[position6y].piece == null && board.squares.get(position6x1)[position6y+mod].piece == null) {
					MoveTupel e = new MoveTupel(x, y, position6x, position6y);
					possibleMoves.add(e);
				}
		char position7x = this.getKey(this.letter.get(x));
		int position7y = y-(2*mod);
				if (menace(board, p, mod) && position7x != '0' && position7y>=0 && position7y<=9 && board.squares.get(position7x)[position7y].piece == null && board.squares.get(position7x)[position7y+mod].piece == null) {
					MoveTupel e = new MoveTupel(x, y, position7x, position7y);
					possibleMoves.add(e);
				}
		char position8x = this.getKey(this.letter.get(x)+(2*mod));
		int position8y = y-(2*mod);
		char position8x1 = this.getKey(this.letter.get(x)+(mod));
				if (menace(board, p, mod) && position8x != '0' && position8y>=0 && position8y<=9 && board.squares.get(position8x)[position8y].piece == null && board.squares.get(position8x1)[position8y+mod].piece == null) {
					MoveTupel e = new MoveTupel(x, y, position8x, position8y);
					possibleMoves.add(e);
				}
		return possibleMoves;
	}
	
	public boolean menace(CannonBoard board, BoardPiece p, int mod){
		char x = p.square.x;
		int y = p.square.y;
		char positionx1 = this.getKey(this.letter.get(x));
		char positionx2 = this.getKey(this.letter.get(x)-mod); 
		char positionx3 = this.getKey(this.letter.get(x)+mod); 
		int positiony = y-mod;
		for(int i = 0; i>3*mod ;i=i+mod){
			
			if(board.squares.get(positionx1)[positiony+i].piece.name != board.currentMove &&board.squares.get(positionx1)[i].piece.name != 'B' && board.squares.get(positionx2)[i].piece.name != 'W' ){
				return true;
			}
			if(board.squares.get(positionx2)[positiony+i].piece.name != board.currentMove &&board.squares.get(positionx2)[i].piece.name != 'B' && board.squares.get(positionx2)[i].piece.name != 'W' ){
				return true;
			}
			if(board.squares.get(positionx3)[positiony+i].piece.name != board.currentMove &&board.squares.get(positionx3)[i].piece.name != 'B' && board.squares.get(positionx3)[i].piece.name != 'W' ){
				return true;
			}
		}
		return false;
	}
	
	public List<MoveTupel> getCannonFrontalMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		if (this.isInCannon(board, p, mod).contains("Forward")) {
			possibleMoves.addAll(this.getCannonFrontalShoot(board, p, mod));
			char x = p.square.x;
			int y = p.square.y;
			int position1y = y+3*mod;
			if (position1y>=0 && position1y<=9 && board.squares.get(x)[position1y].piece == null) {
				MoveTupel a = new MoveTupel(x, y, x, position1y);
				possibleMoves.add(a);
			}
		}
		return possibleMoves;
	}
	public List<MoveTupel> getCannonRetreatMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		if (this.isInCannon(board, p, mod).contains("Back")) {
			possibleMoves.addAll(this.getCannonBackShoot(board, p, mod));
			char x = p.square.x;
			int y = p.square.y;
			int position1y = y-3*mod;
			if (position1y>=0 && position1y<=9 && board.squares.get(x)[position1y].piece == null) {
				MoveTupel a = new MoveTupel(x, y, x, position1y);
				possibleMoves.add(a);
			}
		}
		return possibleMoves;
	}
	public List<MoveTupel> getCannonSideMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		char x = p.square.x;
		int y = p.square.y;
		if (this.isInCannon(board, p, mod).contains("SideRight")) {
			possibleMoves.addAll(this.getCannonSideRightShoot(board, p, mod));
			char position1x = this.getKey(this.letter.get(x)+3*mod);
			if (position1x != '0' && board.squares.get(position1x)[y].piece == null) {
				MoveTupel a = new MoveTupel(x, y, position1x, y);
				possibleMoves.add(a); 
			}
		}
		if (this.isInCannon(board, p, mod).contains("SideLeft")) {
			possibleMoves.addAll(this.getCannonSideLeftShoot(board, p, mod));
			char position1x = this.getKey(this.letter.get(x)-3*mod);
			if (position1x != '0' && board.squares.get(position1x)[y].piece == null) {
				MoveTupel b = new MoveTupel(x, y, position1x, y);
				possibleMoves.add(b); 
			}
		}
		return possibleMoves;
	}
	public List<MoveTupel> getCannonDiagonalMoves(CannonBoard board, BoardPiece p, int mod) {
		List<MoveTupel> possibleMoves = new LinkedList<MoveTupel>();
		if (this.isInCannon(board, p, mod).contains("DiagonalRightFront")) {
			possibleMoves.addAll(this.getCannonDiagonalRightFront(board, p, mod));
			possibleMoves.addAll(this.getCannonDiagonalRightFrontShoot(board, p, mod));
		}
		if (this.isInCannon(board, p, mod).contains("DiagonalRightBack")) {
			possibleMoves.addAll(this.getCannonDiagonalRightBack(board, p, mod));
			possibleMoves.addAll(this.getCannonDiagonalRightBackShoot(board, p, mod));
		}
		if (this.isInCannon(board, p, mod).contains("DiagonalLeftFront")) {
			possibleMoves.addAll(this.getCannonDiagonalLeftFront(board, p, mod));
			possibleMoves.addAll(this.getCannonDiagonalLeftFrontShoot(board, p, mod));
		}
		if (this.isInCannon(board, p, mod).contains("DiagonalLeftBack")) {
			possibleMoves.addAll(this.getCannonDiagonalLeftBack(board, p, mod));
			possibleMoves.addAll(this.getCannonDiagonalLeftBackShoot(board, p, mod));
		}
		return possibleMoves;
	}
	public List<MoveTupel> getCannonFrontalShoot(CannonBoard board, BoardPiece p, int mod) {
		return possibleMoves;
	}
	public List<MoveTupel> getCannonBackShoot(CannonBoard board, BoardPiece p, int mod) {
		return possibleMoves;
	}
	public List<MoveTupel> getCannonSideLeftShoot(CannonBoard board, BoardPiece p, int mod) {
		return possibleMoves;
	}
	public List<MoveTupel> getCannonSideRightShoot(CannonBoard board, BoardPiece p, int mod) {
		return possibleMoves;
	}
	
	
	public List<String> isInCannon(CannonBoard board, BoardPiece p, int mod) {
		List<String> kind = new LinkedList<String>();
		char x = p.square.x;
		int y = p.square.y;
		/*Forward*/ if (board.squares.get(x)[y+2*mod].piece.name == p.name && board.squares.get(x)[y+mod].piece.name == p.name) {
			kind.add("Forward");
		}
		/*Back*/ if (board.squares.get(x)[y-2*mod].piece.name == p.name && board.squares.get(x)[y-mod].piece.name == p.name) {
			kind.add("Back");
		}
		/*DiagonalRightFront*/ if (board.squares.get(this.getKey(this.letter.get(x)+mod))[y+mod].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)+2*mod))[y+2*mod].piece.name == p.name) {
			kind.add("DiagonalRightFront");
		}
		/*DiagonalRightBack*/ if (board.squares.get(this.getKey(this.letter.get(x)-mod))[y-mod].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)-2*mod))[y-2*mod].piece.name == p.name) {
			kind.add("DiagonalRightBack");
		}
		/*DiagonalLeftFront*/ if (board.squares.get(this.getKey(this.letter.get(x)-mod))[y+mod].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)-2*mod))[y+2*mod].piece.name == p.name) {
			kind.add("DiagonalLeftFront");
		}
		/*DiagonalLeftBack*/ if (board.squares.get(this.getKey(this.letter.get(x)+mod))[y-mod].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)+2*mod))[y-2*mod].piece.name == p.name) {
			kind.add("DiagonalLeftBack");
		}
		/*SideRight*/ if (board.squares.get(this.getKey(this.letter.get(x)+mod))[y].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)+2*mod))[y].piece.name == p.name) {
			kind.add("SideRight");
		}
		/*SideLeft*/ if(board.squares.get(this.getKey(this.letter.get(x)-mod))[y].piece.name == p.name && board.squares.get(this.getKey(this.letter.get(x)-2*mod))[y].piece.name == p.name) {
			kind.add("SideLeft");
		}
		return kind;
	}
	//TODO:
	public boolean GameOver(CannonBoard board, char Player) {
		return false;
	}
	
	
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
