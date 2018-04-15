package me.aelesia.reddit.bot.amosbot;

import java.time.format.DateTimeFormatter;

import me.aelesia.reddit.api.objects.RedditPost;

public class Response {
	
	/**
	 * Constructs a response when counter is reset
	 */
	public static String resetCounter(RedditPost post, RedditPost lastPost) {
		String message = "";
		message += "**RESET THE AMOS YEE COUNTER!**\n\n";
		message += "We made it for " + lastPost.createdOn.until(post.createdOn, AmosBot.config.COOLDOWN_TYPE()) + " " + AmosBot.config.COOLDOWN_TYPE().name().toLowerCase() + " this time!\n\n";
		message += "Last brought up by " + lastPost.author + " on " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(lastPost.createdOn) + ": " + lastPost.url;
		return message;
	}
}
