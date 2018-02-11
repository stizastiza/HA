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


	//TODO: implement test cases of same kind as example here
	@Test
	public void winWhiteTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7//b1b3b1b1/b1b1b1b1b1/b1bwb1wbb1/3B6",true);
		assertMove("d1-d0",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7//b1b3b1b1/b1b1b1b1b1/b1b1b1wbb1/3w6",false,true,true);
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
		assertGameState("5W4/3w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1w8//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void cannonShootTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///1bb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b8-b3",true,true); //nullpointer exception
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test 
	public void retreatSoldier() {
		startGame("5W4///3w6/3b6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);

		assertMove("d6-d8",true,true); 

		assertGameState("5W4/3w6///3b6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test 
	public void retreatDiagonalSoldier() {
		startGame("5W4///3w6/3b6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("d6-b8",true,true); //retreat funktioniert nicht
		assertGameState("5W4/1w8///3b6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test 
	public void retreatNotSoldier() {
		startGame("5W4///3w6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("d6-d8",true,false); //retreat funktioniert nicht
		assertGameState("5W4///3w6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test 
	public void retreatCitySoldier() {
		startGame("5Wb3//////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("g9-g7",false,false); //retreat funktioniert nicht
		assertGameState("5Wb3//////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	
	@Test 
	public void captureActualSoldier() {
		startGame("5W4///3wb5///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("d6-e6",true,true); //capture überprüfen
		assertGameState("5W4///4w5///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test 
	public void captureNotSideSoldier() {
		startGame("5W4///3b6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("d6-e6",false,false); //capture überprüfen
		assertGameState("5W4///3b6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void tryCaptureSoldier() {
		startGame("5W4///3b6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("d6-e6",false,false);
		assertGameState("5W4///3b6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
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
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w//w9/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void placeCity() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true);
		assertMove("b9-b9",true,true); //city placen überprüfen
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}
	@Test
	public void CannonShootFalse() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true);
		assertMove("b8-b3",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true,false,false);
	}
	@Test
	public void CannonShootFrontForward() {
		startGame("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///1bb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",true);
		assertMove("b8-b3",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/4B5",false,false,false);
	}
	@Test
	public void DiagonalFrontLeftMoveCannon() {
		startGame("5W4/1w8/2w7/3w6///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-e5", true, true);
		assertGameState("5W4//2w7/3w6/4w5//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
	}
	@Test

	public void DiagonalFrontLeftMoveCannonBlocked() {
		startGame("5W4/1w8/2w7/3w6/4b5//2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-e5", true, false);
	}
	@Test
	public void DiagonalFrontLeftShootCannon() {
		startGame("5W4/1w8/2w7/3w6//5b4/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-f4", true, true);
		assertGameState("5W4/1w8/2w7/3w6///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false, false, false);
	}
	@Test
	public void DiagonalFrontLeftShootCannonBlocked() {
		startGame("5W4/1w8/2w7/3w6/4b5/5b4/31b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-f4", true, false);
	}
	

	@Test
	public void DiagonalFrontRightMoveCannon() {
		startGame("5W4/8w1/7w2/6w3///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("i8-f5", true, true);
		assertGameState("5W4//7w2/6w3/5w4//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
	}
	
	@Test
	public void DiagonalFrontRightMoveCannonBlocked() {
		startGame("5W4/8w1/7w2/6w3/5b4//2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("i8-f5", true, false);
	}
	@Test
	public void DiagonalFrontRightShootCannon() {
		startGame("5W4/8w1/7w2/6w3//4b5/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("i8-e4", true, true);
		assertGameState("5W4/8w1/7w2/6w3///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false, false, false);
	}
	@Test
	public void DiagonalFrontRightShootCannonBlocked() {
		startGame("5W4/8w1/7w2/6w3/5w4/4b5/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("i8-e4", true, false);
	}
	
	// Diagonal left back shoot and diagonal right back shoot

	public void sideMoveCannonRight() {
		startGame("5W4/3www4/////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("d8-g8", true, true);
		assertGameState("5W4/4www3/////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
	}
	@Test
	public void sideMoveCannonLeft() {
		startGame("5W4/3www4/////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("f8-c8", true, true);
		assertGameState("5W4/2www5/////b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
	}
	@Test
	public void sideShootCannonRight() {
		startGame("5W4////////2bbb1w3/3B6", false);
		assertMove("c2-g2", false, true);
		assertGameState("5W4////////2bbb5/3B6", true,false,false);
	}
	@Test
	public void blackMovesWhite() {
		startGame("5W4/1w8/1w8/1w8/////b1b1b1b1b1/3B6", false);
		assertMove("b8-b3", false, false);
		assertGameState("5W4/1w8/1w8/1w8/////b1b1b1b1b1/3B6", false,false,false);
	}
	@Test
	public void blackMovesNothing() {
		startGame("5W4/1w8/1w8/1w8//////3B6", false);
		assertMove("b8-b3", false, false);
		assertGameState("5W4/1w8/1w8/1w8//////3B6", false,false,false);
	}

	@Test
	public void cannonBack() {
		startGame("5W4/2b7//2w7/2w7/2w7///1b8/3B6", true);
		assertMove("c4-c8", true, true);
		assertGameState("5W4///2w7/2w7/2w7///1b8/3B6", false,false,false);
	}
	@Test
	public void cannonBack2() {
		startGame("2b2W4///2w7/2w7/2w7///1b8/3B6", true);
		assertMove("c4-c9", true, true);
		assertGameState("5W4///2w7/2w7/2w7///1b8/3B6", false,false,false);
	}
	@Test
	public void sideShootCannon2() {
		startGame("5W4/1w8/1w8/1w8//1b8/bbb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", true);
		assertMove("b8-b4", true, true);
		assertGameState("5W4/1w8/1w8/1w8///bbb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6", false,false,false);
	}

}
