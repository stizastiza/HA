package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable{


	/**
	 * TODO: was bedeutet serialVersionUID ???
	 */
	private static final long serialVersionUID = 5424778147226994452L;
	
	
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	public CannonBoard Board;
	public Rules rules;
	
	/************************
	 * constructors
	 ***********************/
	
	public CannonGame() {
		super();
		// TODO: add further initializations if necessary
		this.started = true;
		this.Board = new CannonBoard();
		this.rules = new Rules();
		this.setBoard("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/ w");
		this.Board.currentMove = 'w';
		this.setNextPlayer(whitePlayer);
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blackGaveUp()) gameInfo = "black gave up";
			else if(whiteGaveUp()) gameInfo = "white gave up";
			else if(didWhiteDraw() && !didBlackDraw()) gameInfo = "white called draw";
			else if(!didWhiteDraw() && didBlackDraw()) gameInfo = "black called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = blackPlayer.isWinner()? "black won" : "white won";
		}
			
		return gameInfo;
	}	

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) { 
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}
	
	/**
	 * Switch next player
	 */
	private void updateNext() {
		if (nextPlayer == whitePlayer) nextPlayer = blackPlayer;
		else nextPlayer = whitePlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	/**
	 *	@param: state is a FEN-String of new state of the board
	 *  Change a board state to FEN. 
	 */
	@Override
	public void setBoard(String state) {
		this.Board.loadFEN(state);
	}
	
	@Override
	public String getBoard() {
		return this.Board.boardFEN();
	}
	
	
	/**
	 * This method checks if the supplied move is possible to perform 
	 * in the current game state/status and, if so, does it.
	 * The following has to be checked/might be changed:
	 * - it has to be checked if the move can be performed
	 * ---- it is a valid move
	 * ---- it is done by the right player
	 * ---- there is no other move that the player is forced to perform
	 * - if the move can be performed, the following has to be done:
	 * ---- the board state has to be updated (e.g. figures moved/deleted)
	 * ---- the board status has to be updated (check if game is finished)
	 * ---- the next player has to be set (if move is over, it's next player's turn)
	 * ---- history is updated
	 * 
	 * @param move String representation of move
	 * @param player The player that tries the move
	 * @return true if the move was performed
	 */
	@Override
	public boolean tryMove(String moveString, Player player) {
		// TODO: game.status != finished // DO I HAVE ANY LEGAL MOVES?
		// IT HAS TO BE CHECKED IF THE MOVE CAN BE PERFORMED:
		if (!this.rules.MoveParser(this.Board, moveString, this.getMoveCount())) {
			return false;
		}
		// TODO: setBoard() (die w oder b am Ende hinzugefugt abhangig davon wer gerade dran war)
		// TODO: den Schritt mit new Move() Konstruktor erstellen
		// TODO: (!!!) wird uberpruft, ob das Spiel durch diesen Zug zum Ende ist
		// (!!!): die Stadt wird geschlagen? hat der gegn. Spieler keine legale Moves mehr (sie konnen sich nicht bewegen/es gibt nichts zu bewegen)?
		// TODO: set next player.
		// TODO: update history (add move to history)
		
		
		// set board wird hier angewendet
				/*
		 * getBoard auslesen, wer dran ist
		 * move string auslesen, welches das erste feld ist und zweite feld ist -- parsing, also eher in die getBoard methode
		 * was steht auf dem zweiten feld? int setzen um zu wissen ob geschlagen/geschossen wird oder rückzug möglich...
		 * 		case 1 : nix
		 * 		case 2 : eigene figur
		 * 			return false
		 * 		case 3 : gegner
		 * 
		 * überprüfen ob auf dem ersten feld eine figur des spielers steht der dran ist
		 * 		wenn nicht return false
		 * wo ist das zweite feld?
		 * 		case 1: 4 oder 5 felder weg -> kanone
		 * 				überprüfen ob kanone möglich ist
		 * 					nur möglich wenn zweites feld gegner und 3 eigene soldaten in reihe
		 * 	
		 * 		case 2: erstes feld gleich zweites feld -> stadt
		 * 				überprüfen ob für weiß in der letzen und schwarz in der ersten reihe
		 * 				überprüfen ob schon stadt plaziert wurde
		 * 					boolean
		 * 		
		 * 		case 3: zweites feld zwei hinter erstem feld -> rückzug
		 * 				überprüfen ob bedroht wird (diagonal rechts/links oder vorne)
		 * 					wenn ja überprüfen ob weg frei ist (hinter dem soldaten kein weitere soldat)
		 * 		case 4: zweites feld neben oder vor erstem feld -> schlagen/bewegen
		 * 				gegnerische figur auf dem nebenfeld, dann bewegung/schlagen möglich
		 * 				gegnerische figur oder nix vor eigener figur, dann bewegung möglich
		 */
		
		return true;
	}
	

}
