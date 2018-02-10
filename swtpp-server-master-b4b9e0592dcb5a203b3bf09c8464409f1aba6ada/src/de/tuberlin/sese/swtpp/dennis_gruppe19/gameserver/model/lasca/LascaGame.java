package de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.lasca;

import java.io.Serializable;

import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.Game;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.Move;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class LascaGame extends Game implements Serializable{

	private static final long serialVersionUID = 8461983069685628324L;
	
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	private final String initialState = "b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w w";
	String[][] spielfeld = new String[7][7]; //[row][column]
	/**
	 * ________________
	 * |[0,0]|[0,1]|
	 * |  a7 |  b7 | ...
	 * |_____|_____|____
	 * |[1,0]|[1,1]|
	 * |  a6 |  b6 | ...
	 * |_____|_____|____
	 * |...  | ... | 
	 */
	
    private String currentPlayer = null;
    private String playedStoneEndPosition = null;
    private String playedStoneStartPosition = null;
    private int direction = 0;
    private boolean gotPromoted = false;
	
	/************************
	 * constructors
	 ***********************/
	
	public LascaGame() {
		super();
		
		// initialize internal game model (state/ board here)
		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				spielfeld[i][j] = "";
			}
		}
		setState(initialState);
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
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// this method is public only for test coverage
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
	
	@Override
	public void setState(String state) {
		String[] splitState = state.split(" ");
		if(splitState.length == 2){
			this.currentPlayer = splitState[1];
			Player nextPlayer = this.currentPlayer.equals("w") ? this.whitePlayer : this.blackPlayer;
			this.setNextPlayer(nextPlayer);
		}


		String figureState = splitState[0];
		String[] rows = figureState.split("/");
		for(int i = 0; i < rows.length; i++){
			String[] columns = rows[rows.length - 1 - i].split(",",4);
			for (int j = 0; j < columns.length; j++) {
				this.spielfeld[rows.length - 1 - i][i % 2 + j*2] = columns[j];
			}
		}
            
	}
	
	
	@Override
	public String getState() {

        StringBuilder b = new StringBuilder();
        for(int i = 0; i < this.spielfeld.length;  i++){
            for(int j = i % 2; j < this.spielfeld[i].length; j = j + 2){
                b.append(spielfeld[i][j]);
                if(j+2 < this.spielfeld[i].length){
                	b.append(",");
                }
            }
            if(i + 1  < this.spielfeld.length) b.append("/");
        }
        
        String state = b.toString();
        return state; 
	}
	
	
	

	@Override
	public boolean tryMove(String moveString, Player player) {
		String startPosition = moveString.split("-")[0];
		String endPosition   = moveString.split("-")[1];
		String playStone = this.getPosition(startPosition);
		this.currentPlayer = player.equals(this.blackPlayer) ? "b" : "w";
		this.direction = currentPlayer.equals("b") ? 1 : -1;
		
		this.loadLastMove(player);
		
		if(playStone.isEmpty()) return false;

		boolean validMove = this.correctStone(startPosition, endPosition, playStone);
		validMove &= this.isValidMove(startPosition, endPosition);

		if(validMove) this.performMove(startPosition, endPosition);

		if(this.checkFinished()) this.finish(player);

		if(validMove) finishMove(canContinueAttack(startPosition, endPosition, currentPlayer), startPosition, endPosition);

		return validMove;
	}
	
	
	private void loadLastMove(Player currentPlayer){
		//Falls gleicher Spieler letzten move aus History laden
		if(!history.isEmpty()){
			Move previousMove = history.get(history.size()-1);
			if(currentPlayer == previousMove.getPlayer()){
				this.playedStoneStartPosition = previousMove.getMove().split("-")[0];
				this.playedStoneEndPosition = previousMove.getMove().split("-")[1];
			}
		}
	}
	
	
	public boolean correctStone(String startPosition, String endPosition, String playStone){
		//Falscher Spieler
		if(!playStone.toLowerCase().startsWith(currentPlayer)){
			return false;
		}
		//Falscher Stein, falls Angriff fortgesetzt werden muss
		if(this.playedStoneEndPosition != null && !this.playedStoneEndPosition.equals(startPosition)){
			return false;
		}
		//TODO: This somewhere else
		if(this.playedStoneStartPosition != null && this.playedStoneStartPosition.equals(endPosition)){
			return false;
		}
		return true;
	}
	
	
	public void finishMove(boolean samePlayerNextTurn, String startPosition, String endPosition){
		if(!samePlayerNextTurn){
			Player nextPl = isWhiteNext() ? this.blackPlayer : this.whitePlayer;
			this.setNextPlayer(nextPl);
			this.currentPlayer = nextPl == this.whitePlayer ? "w" : "b";
			this.playedStoneEndPosition = null;
			this.playedStoneStartPosition = null;
			this.gotPromoted = false;
		} 
		else {
			this.playedStoneEndPosition = endPosition;
			this.playedStoneStartPosition = startPosition;
		}
	}
	

	/**
	 * Ermittelt aus einem Positionsstring den Index im Spielfeld und gibt den Eintrag des
	 * Spielfelds an der Stelle zurück. zb. a1 = [6][0]
	 * @param positionString
	 * @return
	 */
	public String getPosition(String positionString){
		int column = this.convertColumnToInt(positionString);
		int row    = this.convertRowToInt(positionString);		
		return this.spielfeld[row][column];
	}
	
	
	public int convertColumnToInt(String positionString){
		String columnStr = positionString.substring(0,1);
		int column = columnStr.charAt(0) - 'a';
		return column;
	}
	
	
	public int convertRowToInt(String positionStr){
		String rowStr = positionStr.substring(1,2);
		int row = this.spielfeld[0].length - Integer.parseInt(rowStr);
		return row;
	}
	
	
	public boolean isValidMove(String startPos, String endPos){
		boolean valid = true;
		//Zielposition leer
		valid &= this.getPosition(endPos).isEmpty();

		
		//Abstand korrekt
		int rowDiff    = this.convertRowToInt(endPos) - this.convertRowToInt(startPos);
		int columnDiff = this.convertColumnToInt(endPos) - this.convertColumnToInt(startPos);
		valid &= Math.abs(rowDiff) == Math.abs(columnDiff);
		valid &= Math.abs(columnDiff) <= 2;
		valid &= columnDiff != 0;
		
		//Wenn Abstand = 2, dann wurde ein Gegner geschlagen
		String attackedStone = this.spielfeld[this.convertRowToInt(startPos) + rowDiff/2][this.convertColumnToInt(startPos) + columnDiff/2];

		//Test ob Gegner geschlagen wurde
		if(Math.abs(rowDiff) == 2) valid &= !attackedStone.substring(0,1).equalsIgnoreCase(this.currentPlayer);

		
		//Wenn Abstand != 2, dann kann auch keiner geschlagen werden
		if(Math.abs(rowDiff) == 1) valid &= !canAttack(this.currentPlayer);
		
		//Richtung korrekt, falls kein Offizier
		char top = this.getPosition(startPos).charAt(0); 
		if(Character.isLowerCase(top)){
			//Gleiches Vorzeichen -> immer größer 0
			valid &= direction * rowDiff > 0;	
		}
		return valid;
	}
	
	
	/**
	 * Checks if a player can attack
	 * @param player
	 * @return
	 */
	public boolean canAttack(String player){
		boolean canAttack = false;
		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				if(spielfeld[i][j].toLowerCase().startsWith(player)){
					canAttack |= canAttack(i,j,player, this.direction);
				}
			}
		}
		return canAttack;
	}
	
	
	/**
	 * checks if a specific stone can attack
	 * @param row
	 * @param column
	 * @param player
	 * @param dir
	 * @return
	 */
	public boolean canAttack(int row, int column, String player, int dir){
		String opponent = player.equals("w") ? "b" : "w";
		boolean canAttack = false;
		
		if(row + 2*dir < this.spielfeld.length){
			if(row + (2*dir) >= 0){
				if(column+2 < this.spielfeld[1].length){
					boolean adjacentOpponent = spielfeld[row+dir][column+1].toLowerCase().startsWith(opponent);
					boolean targetFree = spielfeld[row+(2*dir)][column+2].isEmpty();
					canAttack |= (adjacentOpponent && targetFree);
				}
				if(column-2 >= 0){
					boolean adjacentOpponent = spielfeld[row+dir][column-1].toLowerCase().startsWith(opponent);
					boolean targetFree = spielfeld[row+(2*dir)][column-2].isEmpty();
					canAttack |= (adjacentOpponent && targetFree);
				}
			}
			
		}
		
		if(Character.isUpperCase(spielfeld[row][column].charAt(0)) && dir == this.direction){
			canAttack |= canAttack(row, column, player, -dir);
		}
		return canAttack;
	}
	
	
	public boolean canContinueAttack(String startPosition, String endPosition, String player){
		int startColumn = this.convertColumnToInt(startPosition);
		int startRow    = this.convertRowToInt(startPosition);
		int endColumn   = this.convertColumnToInt(endPosition);
		int endRow      = this.convertRowToInt(endPosition);
		
		//Hat nicht angegriffen
		if(Math.abs(startColumn - endColumn) == 1) return false;
		
		//Falls zum Offizier befördert wurde ist der Zug zuende, egal ob weiter geschlagen werden kann
		if(this.gotPromoted) return false;
		
		this.spielfeld[startRow][startColumn] = "#LOCKED";
		boolean canContinue = this.canAttack(endRow, endColumn, player, this.direction);
		this.spielfeld[startRow][startColumn] = "";
		
		return canContinue;
	}
	
	
	public boolean canMove(int row, int column, int dir){
		boolean canMove = false;
		//Bewegen nach vorne rechts
		if(row+dir < spielfeld.length && row+dir >= 0 && column + 1 < spielfeld[1].length)
			canMove |= spielfeld[row+dir][column+1].isEmpty();
		//Bewegen nach vorne links
		if(row+dir < spielfeld.length && row+dir >= 0 && column - 1 >= 0)
			canMove |= spielfeld[row+dir][column-1].isEmpty();
		//Falls Offizier auch Rückwärts laufen überprüfen
		if(Character.isUpperCase(spielfeld[row][column].charAt(0)) && dir == this.direction){
			canMove |= canMove(row,column,-dir);
		}
		return canMove;
	}
	
	
	public void performMove(String startPosition, String endPosition) {
		int startColumn = this.convertColumnToInt(startPosition);
		int startRow    = this.convertRowToInt(startPosition);
		int endColumn   = this.convertColumnToInt(endPosition);
		int endRow      = this.convertRowToInt(endPosition);
		
		//TODO: Hier Brettstatus nach oder vor dem Move? Aktuell davor
		Move move = new Move(startPosition + "-" + endPosition, this.getState(), this.nextPlayer);
		this.history.add(move);
		
		//Bewegen
		this.spielfeld[endRow][endColumn] = this.spielfeld[startRow][startColumn];
		this.spielfeld[startRow][startColumn] = "";
		
		//Schlagen
		if(Math.abs(endColumn - startColumn) > 1){
			int betweenColumn = (startColumn + endColumn) / 2;
			int betweenRow    = (startRow + endRow) / 2;
			String betweenStone = this.spielfeld[betweenRow][betweenColumn];
			this.spielfeld[endRow][endColumn] += betweenStone.charAt(0);
			betweenStone = betweenStone.substring(1);
			this.spielfeld[betweenRow][betweenColumn] = betweenStone;
		}
		
		//Zum Offizier befördern, Spieler muss nicht geprüft werden, da nur zulässige Züge ausgeführt werden
		if(endRow == this.spielfeld.length-1 || endRow == 0 ){
			this.spielfeld[endRow][endColumn] = this.spielfeld[endRow][endColumn].substring(0, 1).toUpperCase() + this.spielfeld[endRow][endColumn].substring(1);
			this.gotPromoted = true;
		}
	}
	
	
	public boolean checkFinished(){
		//Swap the direction because we check if the next Player can do something
		this.direction *=-1;
		boolean finished = true;
		String nextPlayer = isWhiteNext() ? "b" : "w";
		
		boolean canAttack = this.canAttack(nextPlayer);
		
		boolean canMove = false;
		for (int i = 0; i < spielfeld.length; i++) {
			for (int j = 0; j < spielfeld[i].length; j++) {
				if(this.spielfeld[i][j].toLowerCase().startsWith(nextPlayer)){
					boolean curMove = this.canMove(i, j, this.direction);
					canMove |= curMove;
				}
			}
		}
		
		finished &= !canMove;
		finished &= !canAttack;
		//Swap the direction back
		this.direction *=-1;
		return finished; 
	}
	

}
