package me.aelesia.reddit.bot.amosbot.interfaces;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
			for (int i=obj.history.size()-1; i>0; i--) {
				RedditPost post = obj.history.get(i);
				response+= "- ";
				if (i==obj.history.size()-1) {
					response+= AmosBotUtils.formatTimeUnit(post.createdOn, LocalDateTime.now(ZoneId.of("+8"))) +  " | ";
				} else {
					response+= AmosBotUtils.formatTimeUnit(post.createdOn, obj.history.get(i+1).createdOn) +  " | ";
				}
				response+= DateTimeFormatter.ofPattern("dd MMM yyyy").format(post.createdOn) + " | ";
				response+= post.author + " | ";
				response+= AmosBotUtils.toShortUrl(post) + "\n";
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
			response += "It has been " + AmosBotUtils.generatePrefix() + " " + AmosBotUtils.formatTimeUnit(obj.lastPost.createdOn, LocalDateTime.now(ZoneId.of("+8")))  + " since we last talked about Amos Yee!\n";
			response += "Last brought up by " +obj.lastPost.author;
			response += " on " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(obj.lastPost.createdOn) ;
			response += ": " + AmosBotUtils.toShortUrl(obj.lastPost);
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
					e.printStackTrace();
				} catch (Exception e) {
					Logger.error("General error: " + input);
					e.printStackTrace();
				}
			} else {
				System.out.println("Invalid command");
			}
		}
	}
}
