package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable{


	/** TODO ALLGEMEIN:
	 * TODO: was bedeutet serialVersionUID ???
	 * CannonGame.java: CannonGame Constructor, tryMove
	 * CannonBoard.java: aus dem Konstruktor die Zeilen in den Kopf verschieben, 
	 * CannonBoard.java: makeMove(), loadFEN()
	 * Rules.java: getCannonFrontal/RetreatMoves(), GameOver()
	 * jUnit Tests
	 * mit dem Eclipse Plugin EMMA sich auskennen
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
		List<Move> history = new LinkedList<Move>();
		this.setHistory(history);
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
		//game.status != finished // DO I HAVE ANY LEGAL MOVES?
		if (this.getStatus().equals("Finished")) {
			return false;
		}
		// is it done by the right player?
		if (this.getNextPlayer() != player) {
			return false;
		}
		// IT HAS TO BE CHECKED IF THE MOVE CAN BE PERFORMED:
		if (!this.rules.MoveParser(this.Board, moveString, this.getMoveCount())) {
			return false;
		}
		this.Board.makeMove(moveString);
		// TODO: update history (add move to history)
		this.history.add(e);
		// TODO: set next player.
		this.updateNext();
		this.Board.switchMove();
		// TODO: (!!!) wird uberpruft, ob das Spiel durch diesen Zug zum Ende ist
		// (!!!): die Stadt wird geschlagen? hat der gegn. Spieler keine legale Moves mehr (sie konnen sich nicht bewegen/es gibt nichts zu bewegen)?
		
		if (this.rules.GameOver(this.Board)) {
			this.finish(player);
		}
		
		
		
		return true;
	}
	

}
