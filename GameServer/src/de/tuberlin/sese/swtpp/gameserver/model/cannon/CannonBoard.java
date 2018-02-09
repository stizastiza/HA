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
	
	/**
	 * Constructor
	 */
	
	public CannonBoard() {
		//TODO: i don't need a constructor - it could be shorter here
		//TODO: move constructor code to class declarations and members, because constructor is also a method
		//TODO: and it`s count of lines is also limited
		this.pieces = new LinkedList<BoardPiece>();
		
		this.signMap = new HashMap<Character, Integer>();
		this.signMap.put('a', 0);
		this.signMap.put('b', 1);
		this.signMap.put('c', 2);
		this.signMap.put('d', 3);
		this.signMap.put('e', 4);
		this.signMap.put('f', 5);
		this.signMap.put('g', 6);
		this.signMap.put('h', 7);
		this.signMap.put('i', 8);
		this.signMap.put('j', 9);
		
		this.squares = new HashMap<Character, BoardSquare[]>();
		// TODO: make shorter with for (each)
		this.squares.put('a', new BoardSquare[10]);
		this.squares.put('b', new BoardSquare[10]);
		this.squares.put('c', new BoardSquare[10]);
		this.squares.put('d', new BoardSquare[10]);
		this.squares.put('e', new BoardSquare[10]);
		this.squares.put('f', new BoardSquare[10]);
		this.squares.put('g', new BoardSquare[10]);
		this.squares.put('h', new BoardSquare[10]);
		this.squares.put('i', new BoardSquare[10]);
		this.squares.put('j', new BoardSquare[10]);
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
	
	// TODO: ANPASSEN!!!! MOVE FUNCTION (!not limited!): kann man zB die Cannone nicht bewegen
	public void makeMove(char fromX, int fromY, char toX, int toY, boolean capture) {
		
		BoardPiece previousPiece = this.squares.get(fromX)[fromY].piece;
		previousPiece.square = this.squares.get(toX)[toY];
		
		if(capture && this.squares.get(toX)[toY].piece != null) {
			this.squares.get(toX)[toY].piece.square = null;
		}
		
		this.squares.get(toX)[toY].piece = previousPiece;
		this.squares.get(fromX)[fromY].piece = null;
		
		
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
			while (emptyCounter != 0) {
				FEN += 1;
				emptyCounter--;
			}
			if (num != 0) {
				FEN += '/';
			}
		}
		FEN += " "+this.currentMove;
		return FEN;
	}
	
	/**
	 * load another FEN string into the game board. Parser-Preparation for setBoard function.
	 */
	public void loadFEN(String FEN) {
		this.setBoardFree();
		String[] FENArray = FEN.split(" ");
		String[] boardArray = FENArray[0].split("/");
		for (int lines = 0; lines <= 9; lines++) {
			// TODO: take a look if i should mirror a line (i take here from the first line before slash,
			// TODO: but the first line after slash is actually a tenth line
			String line = boardArray[lines];
			int colsY = 0;
			// TODO: if line.lentgh = 0
			for (int cols=1; cols<=line.length(); cols++) {
				char letter = line.charAt(cols-1); // for example (1): cols = 2, letter = 1 or w.
				// if letter is a digit:
				if (!(""+letter).matches("[wWbB]")) {
					colsY = colsY + Integer.parseInt(""+letter);
					continue;
				}
				char name = letter == 'w' ? 'w' : letter == 'W' ? 'W' : letter == 'b' ? 'b' : letter == 'B' ? 'B' : null;
				char x = this.signs[colsY];
				int y = this.digits[lines];
				this.addPiece(name, x, y);
				colsY++;
			}
		}
		this.currentMove = FENArray[1].equals("b") ? 'b' : 'w';
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
	
	
	
	
	
	
}
