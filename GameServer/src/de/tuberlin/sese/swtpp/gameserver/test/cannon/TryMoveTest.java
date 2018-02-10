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

	//TODO: implement test cases of same kind as example here
	public void winWhiteTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7//b1b3b1b1/b1b1b1b1b1/b1b1b1wbb1/3B6",true);
		assertMove("i4-j4",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,true,true);
	}	
	public void moveOnOwnPieceTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/2w7/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("e3-f3",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/2w7/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	public void cannonShotBlockedTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/1ww7/1b8/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b2-f2",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/1ww7/1b8/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	public void cannonMoveTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b2-e2",true,true);
		assertGameState("5W4/111w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1w8//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	public void cannonShootTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///1bb1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("b2-g2",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///11b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
}
