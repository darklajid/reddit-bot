package me.aelesia.reddit.bot.amosbot.interfaces;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import me.aelesia.commons.logger.Logger;
import me.aelesia.reddit.api.objects.RedditPost;
import me.aelesia.reddit.bot.amosbot.AmosBot;
import me.aelesia.reddit.bot.amosbot.AmosBotUtils;

public class InterfaceConsole implements Runnable {
	
	Scanner sc = new Scanner(System.in);
	private AmosBot bot;
	
	public InterfaceConsole(AmosBot bot) {
		this.bot = bot;
	}
	
	private String poll() {
		return sc.nextLine();
	}
	
	private AmosBotAPI.e parseCommand(String input) {
		if (input.contains(AmosBotAPI.e.HISTORY.toString())) {
			return AmosBotAPI.e.HISTORY;
		} else if (input.contains(AmosBotAPI.e.STATUS.toString())) {
			return AmosBotAPI.e.STATUS;
		} else if (input.contains(AmosBotAPI.e.TIME.toString())) {
			return AmosBotAPI.e.TIME;
		} else if (input.contains(AmosBotAPI.e.PAUSE.toString())) {
			return AmosBotAPI.e.PAUSE;
		} else if (input.contains(AmosBotAPI.e.UPDATE.toString())) {
			return AmosBotAPI.e.UPDATE;
		} else if (input.contains(AmosBotAPI.e.ADD.toString())) {
			return AmosBotAPI.e.ADD;
		} else if (input.contains(AmosBotAPI.e.REMOVE.toString())) {
			return AmosBotAPI.e.REMOVE;
		}
		return null;
	}
	
	private void execute(String input, AmosBotAPI.e command) {
		String response = "";
		AmosBotAPI.obj obj;
		
		switch(command) {
		
		case HISTORY:
			obj = bot.history();
			for (int i=obj.history.size()-1; i>=0; i--) {
				RedditPost historicPost = obj.history.get(i);
				response+= "- ";
				if (i!=0) {
					response+= obj.history.get(i-1).createdOn.until(historicPost.createdOn, AmosBot.config.COOLDOWN_TYPE()) + " " + AmosBot.config.COOLDOWN_TYPE().name().toLowerCase() + " | "; }
				response+= DateTimeFormatter.ofPattern("dd MMM yyyy").format(historicPost.createdOn) + " | ";
				response+= historicPost.author + " | ";
				response+= AmosBotUtils.toShortUrl(historicPost) + "\n";
			}
			break;
			
		case STATUS:
			obj = bot.status();
			response += "Uptime: " + obj.uptime;
			response += "\nDeveloper: " + AmosBot.config.DEVELOPER();
			response += "\nVersion: " + AmosBot.config.VERSION();
			response += "\nSubreddits: " + AmosBot.config.SUBREDDIT_LIST();
			break;
			
		case TIME:
			obj = bot.time();
			response += "It has been " + obj.lastPostElapsed + " since Amos Yee was last brought up.\n\n";
			response += "Last brought up by " + obj.lastPostAuthor;
			response += " on " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(obj.lastPostCreOn) ;
			response += " - " + obj.lastPostUrl;
			break;
			
		case PAUSE:
			bot.pause();
			response += "Bot paused";
			break;
			
		case RESUME:
			bot.resume();
			response += "Bot resumed";
			break;
			
		case UPDATE:
			String[] pair = (input.split(" ")[1]).split(":");
			response += bot.update(pair[0], pair[1]);
			break;
			
		case ADD:
			pair = (input.split(" ")[1]).split(":");
			response += bot.add(pair[0], pair[1]);
			break;
			
		case REMOVE:
			pair = (input.split(" ")[1]).split(":");
			response += bot.remove(pair[0], pair[1]);
			break;
			
		default:
			break;
		}
		if (command != null && !response.equals("")) {
			System.out.println(response);
		}
	}

	@Override
	public void run() {
		while (true) {
			String input = poll();
			AmosBotAPI.e command = parseCommand(input);
			if (command != null) {
				try {
					execute(input, command);
				} catch (ArrayIndexOutOfBoundsException e) {
					Logger.error("Invalid parameters: " + input);
				} catch (Exception e) {
					Logger.error("General error: " + input);
					e.printStackTrace();
				}
			}
		}
	}
}
