package main;

import me.aelesia.reddit.bot.amosbot.AmosBot;
import me.aelesia.reddit.bot.amosbot.interfaces.InterfaceConsole;

public class Main {

	public static void main(String[] args) {
		AmosBot bot = new AmosBot();
		InterfaceConsole console = new InterfaceConsole(bot);
		
		bot.init();
		Thread consoleThread = new Thread(console);
		consoleThread.start();
		
		while(true) {
			bot.run();
		}
	}

}
