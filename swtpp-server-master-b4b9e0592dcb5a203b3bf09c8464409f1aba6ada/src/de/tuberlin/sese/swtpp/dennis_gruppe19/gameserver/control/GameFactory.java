package de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.control;

import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.Game;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.User;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.lasca.HaskellBot;
import de.tuberlin.sese.swtpp.dennis_gruppe19.gameserver.model.lasca.LascaGame;

public class GameFactory {
	
	//TODO: change path to bot executable if desired
	public static final String BOT_PATH = "C:\\test\\Main";
	
	public static Game createGame() {
		return new LascaGame();
	}

	public static User createBot(String type, Game game) {
		switch(type) {
			case "haskell": return new HaskellBot((LascaGame)game, BOT_PATH);
			default: return null;
		}
	}
	
}
