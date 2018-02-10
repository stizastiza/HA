package de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.test.lasca;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.Player;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.User;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.lasca.LascaGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	LascaGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "");
		
		game = (LascaGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	
	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		
		game.setState(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(game.tryMove(move, whitePlayer), expectedResult);
		else 
			assertEquals(game.tryMove(move, blackPlayer), expectedResult);
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(game.getState(), expectedBoard);
		assertEquals(game.isWhiteNext(), whiteNext);

		assertEquals(game.isFinished(), finished);
		if (game.isFinished()) {
			assertEquals(whitePlayer.isWinner(), whiteWon);
			assertEquals(blackPlayer.isWinner(), !whiteWon);
		}
	}
	
	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/


	@Test
	public void exampleTest() {
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false); 
	}



	@Test
	public void whiteWontAttack(){
		//White's turn, d4 can attack c5 and e5 but other move selected
		startGame("b,b,b,b/,,/b,b,b,b/,w,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, false);
		assertGameState("b,b,b,b/,,/b,b,b,b/,w,/w,w,w,w/w,w,w/w,w,w,w", true, false, false); 
	}

	@Test
	public void blackWontAttack(){
		//Black's turn, d4 can attack c3 and e3 but other move selected
		startGame("b,b,b,b/b,b,b/b,b,b,b/,b,/w,w,w,w/,,/w,w,w,w b", false);
		assertMove("a5-b4", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,b,/w,w,w,w/,,/w,w,w,w", false, false, false); 
	}

	@Test
	public void whiteHasNoStones(){
		//Black attacks white's last stone --> Move passes and game finished with black as winner
		startGame("b,b,b,b/b,b,b/b,b,b,b/,w,/,,,/,,/,,,", false);
		assertMove("c5-e3",false,true);
		assertGameState("b,b,b,b/b,b,b/b,,b,b/,,/,,bw,/,,/,,,", true, true, false); 
	} 

	@Test
	public void invalidMoves(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w b", false);
		//wrong column same as start
		assertMove("c5-c4",false,false); 
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w", false, false, false); 
		//Wrong column towards left
		assertMove("c5-a4",false,false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w", false, false, false); 
		//Wrong column towards right
		assertMove("c5-g4",false,false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w", false, false, false); 
		//No Stone on start
		assertMove("b4-c3", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w", false, false, false); 
		//Wrong player
		assertMove("a1-b2", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/,,,/,,/w,w,w,w", false, false, false); 

	}

	@Test
	public void attackOwnStoneBlack(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,b,/,,,/,,/w,w,w,w", false);
		assertMove("c5-e3",false,false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,b,/,,,/,,/w,w,w,w", false, false, false); 
	}

	@Test
	public void blackOffizierBackwards(){
		startGame("b,b,b,b/b,b,b/,,,/,B,/,,,/,,/w,w,w,w", false);
		assertMove("d4-c5",false,true);
		assertGameState("b,b,b,b/b,b,b/,B,,/,,/,,,/,,/w,w,w,w", true, false, false); 
	}

	@Test
	public void whiteMoveOffizierBackwards(){
		startGame("b,b,b,b/b,b,b/,,,/,W,/,,,/,,/w,w,w,w", true);
		assertMove("d4-c3",true,true);
		assertGameState("b,b,b,b/b,b,b/,,,/,,/,W,,/,,/w,w,w,w", false, false, false);
	}

	@Test
	public void moveBackwards(){
		startGame("b,b,b,b/b,b,b/,,,/,w,/,,,/,,/w,w,w,w", true);
		assertMove("d4-c3",true,false);
		assertGameState("b,b,b,b/b,b,b/,,,/,w,/,,,/,,/w,w,w,w", true, false, false); 
	}

	@Test
	public void attackOnOppositeEndBlack(){
		//Checks edge cases for canAttack (out of board fields)
		startGame("b,b,b,b/b,b,b/,,,/,w,/,,,/,,/,B,,", false);
		assertMove("b6-a5",false,true);
	}

	@Test
	public void attackOnOppositeEndWhite(){
		//Checks edge cases for canAttack (out of board fields)
		startGame("W,,,/,,/,,,/,,/w,,,/,,/,B,,", false);
		assertMove("a3-b4",true,true);
	}

	@Test
	public void promoteWhite(){
		startGame(",,,/,w,/,,,/,,/w,,,/,,/,B,,", true);
		assertMove("d6-c7",true,true);
		assertGameState(",W,,/,,/,,,/,,/w,,,/,,/,B,,", false, false, false);
	}

	@Test
	public void promoteBlack(){
		startGame(",,,/,w,/,,,/,,/w,,,/b,,/,,,", false);
		assertMove("b2-c1",false,true);
		assertGameState(",,,/,w,/,,,/,,/w,,,/,,/,B,,", true, false, false); 
	}

	@Test
	public void whiteAttackNotContinued(){
		startGame(",,,/,b,/,,,/b,,/w,w,w,w/w,w,w/w,w,w,w", false);
		assertMove("a3-c5",true,true);
		assertMove("g3-f4", true, false);
		assertMove("c5-e7",true,true);
	}

	@Test
	public void edgeMoveTestBlack(){
		startGame("B,,,B/,b,/,,,/,w,/,,,/,b,/B,,,B", false);
		assertMove("d2-c1", false, true);
		assertGameState("B,,,B/,b,/,,,/,w,/,,,/,,/B,B,,B", true, false, false); 
	}

	@Test
	public void edgeMoveTestWhite(){
		startGame("W,,,W/,W,/,,,/,b,/,,,/,w,/W,,,W", true);
		assertMove("d6-c7", true, true);
		assertGameState("W,W,,W/,,/,,,/,b,/,,,/,w,/W,,,W", false, false, false); 
	}


	@Test
	public void offizierAttackCases1(){
		startGame(",,,/,,b/,w,b,/,W,/,w,w,/,,/,,,", true); 
		//Attack own backwards right, next free
		assertMove("d4-f2", true, false);
		//Attack own backwards left, next free
		assertMove("d4-b2", true, false);
		assertGameState(",,,/,,b/,w,b,/,W,/,w,w,/,,/,,,", true, false, false);
	}

	@Test
	public void offizierAttackCases2(){
		startGame(",,,/,,b/,w,b,/,W,/,w,w,/w,,w/,,,", true); 
		//Attack own backwards right, next occupied
		assertMove("d4-f2", true, false);
		//Attack own backwards left, next occupied
		assertMove("d4-b2", true, false);
		assertGameState(",,,/,,b/,w,b,/,W,/,w,w,/w,,w/,,,", true, false, false);
	}

	@Test
	public void offizierAttackCases3(){
		startGame(",,,/,,b/,w,b,/,W,/,b,b,/w,,w/,,,", true); 
		//Attack enemy backwards right, next occupied
		assertMove("d4-f2", true, false);
		//Attack enemy backwards left, next occupied
		assertMove("d4-b2", true, false);
		assertGameState(",,,/,,b/,w,b,/,W,/,b,b,/w,,w/,,,", true, false, false);
	}

	@Test
	public void offizierAttackCases4(){
		startGame(",,,/,,b/,w,b,/,W,/,b,b,/,,/,,,", true); 
		//Attack enemy backwards right
		assertMove("d4-f2", true, true);
		assertGameState(",,,/,,b/,w,b,/,,/,b,,/,,Wb/,,,", false, false, false);
	}

	@Test
	public void offizierAttackCases5(){
		startGame(",,,/,,b/,w,b,/,W,/,b,bb,/,,/,,,", true); 
		//Attack enemy backwards right, check if attack same field twice is prohibited
		assertMove("d4-f2", true, true);
		assertGameState(",,,/,,b/,w,b,/,,/,b,b,/,,Wb/,,,", false, false, false);
	}

	@Test
	public void offizierAttackCases6(){
		startGame("b,,,/,,/,,,/,b,/,,,/bb,,/W,,,", true); 
		//Attack enemy, check if attack same field twice is prohibited
		assertMove("a1-c3", true, true);
		assertGameState("b,,,/,,/,,,/,b,/,Wb,,/b,,/,,,", true, false, false);
		assertMove("c3-a1", true, false);
		assertGameState("b,,,/,,/,,,/,b,/,Wb,,/b,,/,,,", true, false, false);
		assertMove("c3-b4", true, false);
		assertGameState("b,,,/,,/,,,/,b,/,Wb,,/b,,/,,,", true, false, false);
		assertMove("c3-e5", true, true);
		assertGameState("b,,,/,,/,,Wbb,/,,/,,,/b,,/,,,", false, false, false);
	}


	@Test
	public void promoteZugBeendet(){
		startGame(",,,/,b,b/,,,w/,,/,,,/bb,,/,,,", true); 
		assertMove("g5-e7", true, true);
		assertGameState(",,Wb,/,b,/,,,/,,/,,,/bb,,/,,,", false, false, false);
	}


	
	
	//++++ GameRules Test ++++
	@Test
	public void simpleForwardMovesTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/,,/w,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("a3-b4", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	@Test
	public void sameMoveAgainTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true);
		// nochmal denselben zug darf nicht funktionieren, da der stein dort nicht mehr liegt
		assertMove("a3-b4", true, false);
		// spiel muss demnach unveraendert sein!
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}

	@Test
	public void simpleBackwardMovesTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("b4-a3", true, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}

	@Test
	public void notPossibleForwardStepsTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false);

		// schwarz kann aber nicht auf das besetzte feld wo nun schon der stein von weiß auf b4 steht
		assertMove("a5-b4", false, false);
		// spiel muss bei nichterfolg unveraendert bleiben!
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false);

		// schwarz von a5 kann auch nicht auf das dahinter besetzte feld springen.
		assertMove("a5-c3", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	@Test
	public void simpleOfficerBackwardMoveTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/W,,/,w,w,w/w,w,w/w,w,w,w", true);
		assertMove("b4-a3", true, true);
		assertGameState("b,b,b,b/b,b,b/b,b,b,b/,,/W,w,w,w/w,w,w/w,w,w,w", false, false, false);
	}

	@Test
	public void simpleBeatMoveTest(){
		startGame("b,b,b,b/b,b,b/b,b,b,b/w,,/,w,w,w/w,w,w/w,w,w,w", false);

		// schwarz von c5 kann aber springen
		assertMove("c5-a3", false, true);
		assertGameState("b,b,b,b/b,b,b/b,,b,b/,,/bw,w,w,w/w,w,w/w,w,w,w", true, false, false);
	}

	@Test
	public void promotionTest(){
		startGame("b,b,b,b/b,b,b/b,b,,/w,,b/w,bw,w,w/w,w,w/w,w,,w", false);

		assertMove("c3-e1", false, true);
		assertGameState("b,b,b,b/b,b,b/b,b,,/w,,b/w,,w,w/w,,w/w,w,Bww,w", true, false, false);
	}

	@Test
	public void mustBeat1Test(){
		startGame(",,,/,,/,b,b,/w,,b/,w,w,w/w,w,w/,,,", true);

		// stein von b4 MUSS schlagen
		assertMove("b4-a5", true, false);
		assertGameState(",,,/,,/,b,b,/w,,b/,w,w,w/w,w,w/,,,", true, false, false);

		// stein von b4 wird schlagen
		assertMove("b4-d6", true, true);
		assertGameState(",,,/,wb,/,,b,/,,b/,w,w,w/w,w,w/,,,", false, false, false);
	}

	@Test
	public void mustBeat2Test(){
		startGame(",,,/,,/,b,b,/w,,b/b,w,w,w/w,w,w/,,,", false);

		// stein von f4 kann nicht schlagen
		assertMove("f4-d2", false, false);
		assertGameState(",,,/,,/,b,b,/w,,b/b,w,w,w/w,w,w/,,,", false, false, false);

		// stein von a3 MUSS schlagen
		assertMove("a3-c1", false, true);
		assertGameState(",,,/,,/,b,b,/w,,b/,w,w,w/,w,w/,Bw,,", true, false, false);
	}

	@Test
	public void comboKill1Test(){
		String comboKillBoard = "b,b,b,b/b,b,b/b,b,b,/w,w,b/w,,w,w/w,w,w/w,w,,w";
		startGame(comboKillBoard, false);

		// stein von e5 kann nur vorwaerts schlagen
		assertMove("e5-c3", false, true);
		assertGameState("b,b,b,b/b,b,b/b,b,,/w,,b/w,bw,w,w/w,w,w/w,w,,w", false, false, false);
		assertMove("c3-e1", false, true);
		assertGameState("b,b,b,b/b,b,b/b,b,,/w,,b/w,,w,w/w,,w/w,w,Bww,w", true, false, false);
	}

	@Test
	public void killBackwardTest(){
		String startBoard = "b,b,b,b/b,b,b/b,b,,/w,w,b/w,b,w,w/w,w,w/w,w,,w";
		startGame(startBoard, false);

		// stein von e5 kann nur vorwaerts schlagen
		assertMove("c3-e5", false, false);
		assertGameState("b,b,b,b/b,b,b/b,b,,/w,w,b/w,b,w,w/w,w,w/w,w,,w", false, false, false);
	}

	@Test
	public void win1Test(){
		String startBoard = ",,,/,Wb,/,Bww,,/,,/,,,bwbb/,bwbbwb,/Bwbb,Bw,,Bw";
		startGame(startBoard, false);

		assertMove("c5-e7", false, true);
		// wenn schwarz den letzten zug macht und gewinnt, warum ist weiss der naechste spieler?
		assertGameState(",,BwwW,/,b,/,,,/,,/,,,bwbb/,bwbbwb,/Bwbb,Bw,,Bw", true, true, false);
	}


	@Test
	public void win2Test(){
		String startBoard = ",,,/,,/,,,/,Bww,/,,Bwbbwb,bwbb/,,/Bwbb,,Bw,WbBw";
		startGame(startBoard, false);

		assertMove("g3-f2", false, true);
		assertGameState(",,,/,,/,,,/,Bww,/,,Bwbbwb,/,,bwbb/Bwbb,,Bw,WbBw", true, true, false);
	}
}
