package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CannonBoard implements Serializable {
	// Board notation: 
	final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
	Map<Character, Integer> letter;
	
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
		//TODO: move constructor code to class declarations and members
		this.pieces = new LinkedList<BoardPiece>();
		
		this.letter = new HashMap<Character, Integer>();
		this.letter.put('a', 0);
		this.letter.put('b', 1);
		this.letter.put('c', 2);
		this.letter.put('d', 3);
		this.letter.put('e', 4);
		this.letter.put('f', 5);
		this.letter.put('g', 6);
		this.letter.put('h', 7);
		this.letter.put('i', 8);
		this.letter.put('j', 9);
		
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
	
	
	
}
