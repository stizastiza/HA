package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CannonBoard implements Serializable {
	// Board notation: 
	final int[] digits = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	final char[] signs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
	Map<Character, Integer> signMap;
	
	// FEN-Notation, piece to move now:
	String currentMove;
	// String currentState?
	// possible moves:
	//TODO: ADD SPECIALITIES
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
	public void addPiece(String name, String color, char x, int y) {
		BoardPiece newPiece = new BoardPiece(name, color);
		newPiece.square = this.squares.get(x)[y];
		this.pieces.add(newPiece);
		this.squares.get(x)[y].piece = newPiece;
	}
	
	// public List<Integer> getPiece(String name, String color, char x, int y) {}
	
	// switchMove() does the same as updateNext()
	
	// MOVE FUNCTION (!not limited!):
	public void makeMove(char fromX, int fromY, char toX, int toY, boolean capture) {
		
		BoardPiece previousPiece = this.squares.get(fromX)[fromY].piece;
		previousPiece.square = this.squares.get(toX)[toY];
		
		if(capture && this.squares.get(toX)[toY].piece != null) {
			this.squares.get(toX)[toY].piece.square = null;
		}
		
		this.squares.get(toX)[toY].piece = previousPiece;
		this.squares.get(fromX)[fromY].piece = null;
		
	}
	
	
	
	
}
