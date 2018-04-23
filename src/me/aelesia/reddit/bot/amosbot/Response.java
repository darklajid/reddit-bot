package me.aelesia.reddit.bot.amosbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import me.aelesia.reddit.api.objects.RedditPost;

public class Response {
	
	/**
	 * Constructs a response when counter is reset
	 */
	public static String resetCounter(RedditPost post, RedditPost lastPost) {
		String message = "";
		message += "**RESET THE AMOS YEE COUNTER!**\n\n";
		message += "It has been " + AmosBotUtils.generatePrefix() + " *" + AmosBotUtils.formatTimeUnit(lastPost.createdOn, post.createdOn)  + "* since we last talked about Amos Yee!\n\n";
		message += "Last brought up by " + lastPost.author;
		message += " on " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(lastPost.createdOn) ;
		message += ": " + AmosBotUtils.toShortUrl(lastPost);
		return message;
	}
}
