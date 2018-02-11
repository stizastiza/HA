package de.tuberlin.sese.swtpp.gameserver.test.cannon;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	CannonGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "");
		
		game = (CannonGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(expectedBoard,game.getBoard().replaceAll("e", ""));
		assertEquals(whiteNext, game.isWhiteNext());

		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(whiteNext, game.isWhiteNext());
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h6-h5",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void firstTest() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("a9-a9",true,false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", true, false, false);
	}
	@Test
	public void secondTest() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/8B1",true);
		assertMove("f6-f5",true,true);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/5w4//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/8B1",false,false,false);
	}
	@Test
	public void thirdTest() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("b9-b9",true,true);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", false,false,false);
	}
	@Test
	public void fourthTest() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("a9-a9",true,false);
		assertMove("e9-e9",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", false,false,false);
		assertMove("e0-e0",false,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5", true,false,false);
	}
	@Test
	public void fifthTest() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true);
		assertMove("b8-b5",true,true);
		assertGameState("4W5/3w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1w8//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",false,false,false);
	}
<<<<<<< HEAD

	//TODO: implement test cases of same kind as example here
	@Test
	public void winWhiteTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7//b1b3b1b1/b1b1b1b1b1/b1bwb1wbb1/3B6",true);
		assertMove("d1-d0",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/10/b1b3b1b1/b1b1b1b1b1/b1b1b1wbb1/3w6",false,true,true);
	}	
	@Test
	public void moveOnOwnPieceTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/2w7/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("e3-f3",false,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/2w7/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void cannonShotBlockedTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/1ww7/1b8/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b2-f2",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/1ww7/1b8/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void cannonMoveTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b8-b5",true,true); // cannon scheint nicht richtig zu moven
		assertGameState("5W4/3w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1w8/10/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void cannonShootTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///1bb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b8-b3",true,false); //nullpointer exception
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/10/10/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test 
	public void retreatSoldier() {
		startGame("5W4///3b6/3w6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("d4-b4",false,true); //retreat funktioniert nicht
		assertGameState("5W4/3b6/10/10/3w6/10/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test 
	public void captureActualSoldier() {
		startGame("5W4///3bw5///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("d6-e6",false,true); //capture überprüfen
		assertGameState("5W4/10/10/4b5/10/10/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void tryCaptureSoldier() {
		startGame("5W4///3b6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("d6-e6",false,false);
		assertGameState("5W4/10/10/3b6/10/10/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void moveOutOfBounce1() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("x6-h5",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void moveOutOfBounce2() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h2-h12",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void moveTooLongWhite() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h5--h5",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void moveTooLongBlack() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("h5--h5",false,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void capture() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/w9/b9/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("a5-a4",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/10/w9/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void placeCity() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("b0-b0",true,true); //city placen überprüfen
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/10/10/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/10",false,false,false);
=======
	@Test
	public void CannonShootFalse() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true);
		assertMove("b8-b3",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true,false,false);
	}
	@Test
	public void CannonShoot() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///1bb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true);
		assertMove("b8-b3",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",false,false,false);
	}
	@Test
	public void DiagonalMoveCannon() {
		startGame("5W4/1w8/2w7/3w6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-e5", true, true);
		assertGameState("5W4//2w7/3w6/4w5//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
>>>>>>> bfc1ab341342b15f57dae347bdadc6f7321122aa
	}
}
