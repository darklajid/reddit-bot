package me.aelesia.reddit.bot.amosbot;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import me.aelesia.commons.logger.Logger;
import me.aelesia.reddit.api.objects.RedditPost;

public class PostScanner {
	
	/**
	 * Scans a post to see if the user was summoning the bot
	 */
	public static boolean isSummoningBot (RedditPost post) {
		if (post.text.contains("/u/" + AmosBot.config.USERNAME())) {
			Logger.info("Post #" + post.id() + " /u/" + post.author + " is summoning bot: " + post.text);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the caller is an admin
	 */
	public static boolean isAdminUser(RedditPost post) {
		if (AmosBot.config.ADMIN_USER_LIST().contains(post.author)) {
			Logger.info("/u/" + post.author + " is authorised");
			return true;
		}
		Logger.warn("/u/" + post.author + " is unauthorised");
		return false;
	}
	
	/**
	 * Scans a post to determine if this is an Amos Yee post
	 * - Post content must contain the words 'amos' and 'yee'
	 * - Thread titles must contain the words 'amos'
	 */
	public static boolean isAmosPost(RedditPost post) {
		if (post.text.matches("/u/[\\w\\d-_]*amos")) {
			Logger.info("Post #" + post.id() + " contains a username with 'amos' in it. Ignoring.");
			return false;
		}
		else if (StringUtils.containsIgnoreCase(post.text, "amos") && StringUtils.containsIgnoreCase(post.text, "yee")) {
			Logger.info("Post #" + post.id() + " contains keywords 'amos' & 'yee' in content");
			return true;
		}
		else if (post.isThread() && StringUtils.containsIgnoreCase(post.threadTitle, "amos")) {
			Logger.info("Thread #" + post.id() + " contains keyword 'amos' in title");
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the threadId is contained within historical records
	 * eg. if Amos Yee has been brought up in the same thread previously,
	 *     then the thread would have been marked as an 'Amos Yee' thread
	 *     and the function will return false so that it will not
	 *     re-trigger on the same thread.
	 */
	public static boolean isNewAmosThread(RedditPost post, List<RedditPost> history) {
		if (post.threadId.equals(AmosBot.config.TESTING_THREAD())) {
			Logger.info("Thread #"+post.threadId+" is a testing thread");
			return true;
		}
		for (int i=history.size()-1; i>=0; i--) {
			RedditPost historicPost = history.get(i);
			if (post.threadId.equals(historicPost.threadId)) {
				Logger.info("Thread #"+post.threadId+" already exists in historical records");
				return false;
			}
		}
		Logger.info("Thread #"+post.threadId+" not encountered before in historical records");
		return true;
	}
	
	/**
	 * Checks if the time difference between the last Amos post and this Amos post
	 * has exceeded the minimum cooldown period.
	 */
	public static boolean cooldown(RedditPost post, RedditPost lastPost) {
		if (lastPost.createdOn.until(post.createdOn, AmosBot.config.COOLDOWN_TYPE()) >= AmosBot.config.COOLDOWN_DURATION()) {
			Logger.info("Elapsed time between post #"+post.id() + " ["+post.createdOn+"] "
					+ "and last post #" + lastPost.id() + " ["+lastPost.createdOn+"] "
					+ "is above threshold");
			return true;
		}
		Logger.info("Elapsed time between post #"+post.id() + " ["+post.createdOn+"] "
				+ "and last post #" + lastPost.id() + " ["+lastPost.createdOn+"] "
				+ "is under minimum duration of " + AmosBot.config.COOLDOWN_DURATION() + " " + AmosBot.config.COOLDOWN_TYPE().name().toLowerCase());
		return false;
	}
}
