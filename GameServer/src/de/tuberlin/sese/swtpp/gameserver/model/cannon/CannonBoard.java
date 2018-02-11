package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CannonBoard implements Serializable {
	private static final long serialVersionUID = 5424778147226994452L;
	// Board notation: 
	final int[] digits = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	final char[] signs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
	Map<Character, Integer> signMap;
	
	// 'w' or 'b' - Zeigt wer dran ist (wird am Ende des FEN-Strings hinzugefuegt)
	char currentMove;
	// Pieces (Spielsteine) of the board:
	List<BoardPiece> pieces;
	// Squares of the board:
	Map<Character, BoardSquare[]> squares;
	char fromX, toX;
	int fromY, toY;
	boolean cityMove, cannonMove, cannonShoot;
	
	/**
	 * Constructor
	 */
	
	public CannonBoard() {
		this.pieces = new LinkedList<BoardPiece>();
		this.squares = new HashMap<Character, BoardSquare[]>();
		for (char c: this.signs) {
			this.squares.put(c, new BoardSquare[10]);
		}
		this.signMap = new HashMap<Character, Integer>();
		signMap.put('a', 0);
		signMap.put('b', 1);
		signMap.put('c', 2);
		signMap.put('d', 3);
		signMap.put('e', 4);
		signMap.put('f', 5);
		signMap.put('g', 6);
		signMap.put('h', 7);
		signMap.put('i', 8);
		signMap.put('j', 9);
		// fill fileds: for example [a][0..9]
		for (char keyVar: this.squares.keySet()) {
			for (int j=0; j<=9; j++) {
				this.squares.get(keyVar)[j] = new BoardSquare(keyVar, j);
			}
		}
	}
	
	
	//Create piece objects and place them to their positions
	public void addPiece(char name, char x, int y) {
		BoardPiece newPiece = new BoardPiece(name);
		newPiece.square = this.squares.get(x)[y];
		this.pieces.add(newPiece);
		this.squares.get(x)[y].piece = newPiece;
	}
	
	public List<Integer> getPiece(char name, char x, int y) {
		List<Integer> result = new LinkedList<Integer>();
		for(int i=0 ; i<this.pieces.size() ; i++) {
			if(this.pieces.get(i).name == name
					&& this.pieces.get(i).square != null 
					&& ((x!=0 && this.pieces.get(i).square.x==x) || x==0) 
					&& ((y!=-1 && this.pieces.get(i).square.y==y) || y==-1)) {
				result.add(i);
			}
		}
		return result;
	}
	
	/**
	 * Switches the current move
	 */
	public void switchMove() {
		if(this.currentMove == 'w') {
			this.currentMove = 'b';
		} else {
			this.currentMove = 'w';
		}
	}
	
	public void makeMove(String MoveString) {
		this.parseLocalString(MoveString);
		if (this.cityMove) {
			this.makeMoveCity();
		}
		else if (this.cannonMove) {
			this.makeMoveCannon();
		}
		else if (this.cannonShoot) {
			this.cannonShoot();
		} else {
		BoardPiece previousPiece = this.squares.get(this.fromX)[this.fromY].piece;
		BoardPiece targetPiece = this.squares.get(this.toX)[this.toY].piece;
		
		
		if(targetPiece != null && targetPiece.name != this.currentMove && targetPiece.name != Character.toUpperCase(this.currentMove)) {
			targetPiece.square = null;
			this.pieces.remove(targetPiece);		
			}
		previousPiece.square = this.squares.get(this.toX)[this.toY];
		this.squares.get(this.toX)[this.toY].piece = previousPiece;
		this.squares.get(this.fromX)[this.fromY].piece = null;
		
		}
		//this.switchMove();
	}
	public void makeMoveCity() {
		BoardPiece City = new BoardPiece(Character.toUpperCase(this.currentMove));
		this.squares.get(this.toX)[this.toY].piece = City;
		City.square = this.squares.get(this.toX)[this.toY];
	}
	public void makeMoveCannon() {
		BoardPiece CannonLeader = this.squares.get(this.fromX)[this.fromY].piece;
		CannonLeader.square = this.squares.get(this.toX)[this.toY];
		this.squares.get(this.toX)[this.toY].piece = CannonLeader; 
		this.squares.get(this.fromX)[this.fromY].piece = null;
	}
	public void cannonShoot() {
		BoardPiece previousPiece = this.squares.get(this.toX)[this.toY].piece;
		previousPiece.square = null;
		this.squares.get(this.toX)[this.toY].piece = null;
	}
	
	public void parseLocalString(String MoveString) {
		char [] Positions = new char[5];
		MoveString.getChars(0, MoveString.length(), Positions, 0);
		this.fromX = Positions[0];
		this.fromY = Character.getNumericValue(Positions[1]);
		this.toX = Positions[3];
		this.toY = Character.getNumericValue(Positions[4]);
		this.cityMove = this.fromX == this.toX && this.fromY == this.toY ? true : false;
		this.cannonMove = this.difference(this.signMap.get(this.fromX), this.signMap.get(this.toX)) == 3 || this.difference(fromY, toY) == 3 ? true : false;
		this.cannonShoot = this.difference(this.signMap.get(this.fromX), this.signMap.get(this.toX)) > 3 || this.difference(fromY, toY) > 3 ? true : false;
	}
	
	public int difference(int k, int m) {
		if (k>m) {
			return k-m;
		}
		else if (m>k) {
			return m-k;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * @return current board state as a FEN-String. Parser-Preparation for getBoard function.
	 */
	public String boardFEN() {
		String FEN = "";
		for (int num=9; num>=0; num--) {
			int emptyCounter = 0;
			for (char keyVar: this.squares.keySet()) {
				if(this.squares.get(keyVar)[num].piece != null) {
					if(emptyCounter != 0) {
						FEN += emptyCounter;
						emptyCounter = 0;
					}
					char pieceName = this.squares.get(keyVar)[num].piece.name;
					FEN += pieceName;
				}
				else {
					emptyCounter++;
				}	
			}
			if (emptyCounter != 0) {
				FEN = emptyCounter < 10 ? FEN+emptyCounter : FEN;
				emptyCounter = 0;
			}
			if (num != 0) {
				FEN += '/';
			}
		}
		//FEN += " "+this.currentMove;
		return FEN;
	}
	
	/**
	 * load another FEN string into the game board. Parser-Preparation for setBoard function.
	 */
	public void loadFEN(String FEN) {
		this.setBoardFree();
		String[] parts = FEN.split(" ");
		String[] boardArray = parts[0].split("/");
	    String[] actBoardArray = new String[10];
	    for (int i = 0; i<=8; i++) {
	        actBoardArray[i] = boardArray[i];
	    }
	    if (boardArray.length < 10) {
	        actBoardArray[9] = "";
	    } else {
	        actBoardArray[9] = boardArray[9];
	    }
		for (int lines = 0; lines <= 9; lines++) {
			String line = actBoardArray[lines];
			int colsY = 0;
			for (int cols=1; cols<=line.length(); cols++) {
				char letter = line.charAt(cols-1); // for example (1): cols = 2, letter = 1 or w.
				// if letter is a digit:
				if (!(""+letter).matches("[wWbB]")) {
					colsY = colsY + Character.getNumericValue(letter);
					continue;
				}
				char name = letter == 'w' ? 'w' : letter == 'W' ? 'W' : letter == 'b' ? 'b' : letter == 'B' ? 'B' : null;
				char x = this.signs[colsY];
				int y = this.digits[9-lines];
				this.addPiece(name, x, y);
				colsY++;
			}
		}
	}
	
	/**
	 * Set game board free:
	 */
	public void setBoardFree() {
		for (char keyVar: this.squares.keySet()) {
			for (int j=0; j<=9; j++) {
				this.squares.get(keyVar)[j].piece = null;
			}
		}
		this.pieces = new LinkedList<BoardPiece>();
	}
	
	 public char getKey(int value) {
		 for (char i: this.signMap.keySet()) {
			 if (this.signMap.get(i) == value) {
				 return i;
			 }
		 }
		 return '0';
	 }
	
	
	
	
}
