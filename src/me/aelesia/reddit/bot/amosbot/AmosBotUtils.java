package me.aelesia.reddit.bot.amosbot;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.aelesia.reddit.api.objects.RedditPost;

public class AmosBotUtils {
	
	/**
	 * Formats elapsed time to d : hr : m : s format
	 */
	public static String formatElapsedTime(long seconds) {

	    long days = TimeUnit.SECONDS.toDays(seconds);
	    seconds -= TimeUnit.DAYS.toSeconds(days);
		
	    long hours = TimeUnit.SECONDS.toHours(seconds);
	    seconds -= TimeUnit.HOURS.toSeconds(hours);

	    long minutes = TimeUnit.SECONDS.toMinutes(seconds);
	    seconds -= TimeUnit.MINUTES.toSeconds(minutes);
	    
	    return String.format("%d d : %d h : %d m : %d s", days, hours, minutes, seconds);
	}
	
	/**
	 * Formats a reddit post link url to show the title and hide the url
	 */
	public static String toShortUrl(RedditPost post) {
		String shortUrl = "";
		if (post.threadTitle.length()>50) {
			shortUrl += "[" + post.threadTitle.substring(0,50) + "...]";
		} else {
			shortUrl += "[" + post.threadTitle + "]";
		}
		shortUrl += "(" + post.url +  ")";
		return shortUrl;
	}
	
	public static String formatPostIDList(List<RedditPost> postList) {
		String str = "[";
		for (RedditPost post : postList) {
			str+= post.subreddit + "." + post.id();
			str+= ", ";
		}
		return (str.substring(0, str.length()-2) + "]");
	}
}
