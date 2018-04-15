package me.aelesia.reddit.bot.amosbot.interfaces;

import java.time.format.DateTimeFormatter;

import me.aelesia.commons.logger.Logger;
import me.aelesia.reddit.api.RedditAPI;
import me.aelesia.reddit.api.objects.RedditPost;
import me.aelesia.reddit.bot.amosbot.AmosBot;
import me.aelesia.reddit.bot.amosbot.AmosBotUtils;
import me.aelesia.reddit.bot.amosbot.PostScanner;

public class InterfaceReddit {
	
	private AmosBot bot;
	private RedditAPI redditAPI;
	
	public InterfaceReddit(AmosBot bot, RedditAPI redditAPI) {
		this.bot = bot;
		this.redditAPI = redditAPI;
	}
	
	private AmosBotAPI.e parseCommand(RedditPost post) {
		if (post.text.contains(AmosBotAPI.e.HISTORY.toString())) {
			return AmosBotAPI.e.HISTORY;
		} else if (post.text.contains(AmosBotAPI.e.STATUS.toString())) {
			return AmosBotAPI.e.STATUS;
		} else if (post.text.contains(AmosBotAPI.e.TIME.toString())) {
			return AmosBotAPI.e.TIME;
		} else if (PostScanner.isAdminUser(post)) {
			if (post.text.contains(AmosBotAPI.e.PAUSE.toString())) { 
				return AmosBotAPI.e.PAUSE;
			} else if (post.text.contains(AmosBotAPI.e.ADD.toString())) { 
				return AmosBotAPI.e.ADD;
			} else if (post.text.contains(AmosBotAPI.e.REMOVE.toString())) { 
				return AmosBotAPI.e.REMOVE;
			} else if (post.text.contains(AmosBotAPI.e.UPDATE.toString())) { 
				return AmosBotAPI.e.UPDATE;
			}
		}
		return null;
	}
	
	private String generateResponse(String input, AmosBotAPI.e command) {
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
				response += "\n\nDeveloper: " + AmosBot.config.DEVELOPER();
				response += "\n\nVersion: " + AmosBot.config.VERSION();
				response += "\n\nSubreddits: " + AmosBot.config.SUBREDDIT_LIST();
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

			case UPDATE:
				try {
					String[] pair = (input.split(" ")[2]).split(":");
					response += bot.update(pair[0], pair[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					Logger.error("Invalid parameters: " + input);
				}
				break;
				
			case ADD:
				try {
					String[] pair = (input.split(" ")[2]).split(":");
					response += bot.add(pair[0], pair[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					Logger.error("Invalid parameters: " + input);
				}
				break;
				
			case REMOVE:
				try {
					String[] pair = (input.split(" ")[2]).split(":");
					response += bot.remove(pair[0], pair[1]);
					break;
				} catch (ArrayIndexOutOfBoundsException e) {
					Logger.error("Invalid parameters: " + input);
				}
				break;
				
			default:
				throw new IllegalStateException("Unhandled state");
		}
		return response;
	}

	public void run(RedditPost post) {		
		AmosBotAPI.e command = parseCommand(post);
		if (command != null) {
			redditAPI.reply(post.id(), generateResponse(post.text, command));
		}
	}

}
