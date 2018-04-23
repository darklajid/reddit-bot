package me.aelesia.reddit.bot.amosbot;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
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
	 * Formats elapsed time to SECONDS / MINUTES / HOURS / DAYS
	 */
	public static String formatTimeUnit(LocalDateTime oldTime, LocalDateTime newTime) {

		long diffEpochTime = oldTime.until(newTime, ChronoUnit.SECONDS);
		ChronoUnit timeUnit;
		
		if (diffEpochTime < 120) {
			timeUnit = ChronoUnit.SECONDS;
		} else if (diffEpochTime < 7200) {
			timeUnit = ChronoUnit.MINUTES;
		} else if (diffEpochTime < 172800) {
			timeUnit = ChronoUnit.HOURS;
		} else {
			timeUnit = ChronoUnit.DAYS;
		}
		return oldTime.until(newTime, timeUnit) + " " + timeUnit.name().toLowerCase();
	}
	
	public static String generatePrefix() {

		int random = new Random().nextInt(16);
		switch(random) {
			case 0: return "an astonishing";
			case 1: return "a mind-boggling";
			case 2: return "a mind-blowing";
			case 3: return "an unbelievable";
			case 4: return "an inconceivable span of";
			case 5: return "a grand total of";
			case 6: return "a wondrous";
			case 7: return "a shocking span of";
			case 8: return "an exceptional span of";
			case 9: return "an impressive";
			case 10: return "a remarkable";
			case 11: return "a peaceful span of";
			case 12: return "a noteworthy";
			case 13: return "a spectacular";
			case 14: return "a momentous";
			case 15: return "an exceptional";
			default: return "";
		}
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
	
	public static String generateSignature() {
		String str = "";
		str += "v" + AmosBot.config.VERSION() + " | ";
		str += "[Github](https://github.com/aelesia/reddit-bot)" + " | ";
		str += "View History: ";
		str += "`/u/" + AmosBot.config.USERNAME() + " !history`";
		return str;
	}
}
