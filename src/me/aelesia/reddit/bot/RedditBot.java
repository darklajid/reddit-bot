package me.aelesia.reddit.bot;

import java.util.List;

import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.utils.ThreadUtils;
import me.aelesia.reddit.api.O2AClient;
import me.aelesia.reddit.api.RedditAPI;
import me.aelesia.reddit.api.objects.RedditPost;

public abstract class RedditBot implements Runnable {
	
	protected O2AClient o2aClient;
	protected RedditAPI redditAPI;
	protected boolean pause = false;
	protected int sleepSeconds = 1;
	
	/**
	 * Generic interface for performing all initializing actions
	 */
	public abstract void init();
	
	/**
	 * Generic interface for retriving posts to be processed
	 */
	protected abstract List<RedditPost> retrieve();
	
	/**
	 * Generic interface for filtering of posts
	 */
	protected abstract List<RedditPost> filter(List<RedditPost> postList);
	
	/**
	 * Generic interface for scanning of posts and performing necessary actions
	 */
	protected abstract void scan(RedditPost post);
	
	/**
	 * Implementation of how the bot runs
	 * Retrieves all posts and stores it into postList
	 * Filters all posts and stores it into postList
	 * Loops through the posts individually and scans them
	 * Goes to sleep after that
	 * Loops indefinitely
	 */
	public void run() {
		while (true) {
			List<RedditPost> postList;
			postList = retrieve();
			postList = filter(postList);
			for (RedditPost post : postList) {
				scan(post);
			}
			Logger.debug("Sleeping for "+ sleepSeconds + " seconds");
			ThreadUtils.sleep(sleepSeconds* 1000);
			while (pause) {
				ThreadUtils.sleep(1000);
			}
		}
	}
}
