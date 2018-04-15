package me.aelesia.reddit.bot.amosbot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.aelesia.commons.logger.Logger;
import me.aelesia.reddit.api.objects.RedditPost;

public class PostFilter{
	
	private LocalDateTime lastPostTime = LocalDateTime.MIN;
	
	
	/**
	 * Filter to remove posts that have already been read
	 */
	public List<RedditPost> filterOldPosts(List<RedditPost> postList) {

		List<RedditPost> filteredList = null;
		
		// Removes all posts that occured after 'lastPostTime'
		filteredList = postList.stream()
			.filter(post -> post.createdOn.isAfter(lastPostTime))
			.collect(Collectors.toList());
		
		// Iterates through the filtered post list to get timing of the newest post
		if (!filteredList.isEmpty()) {
			Optional<LocalDateTime> newestPostTime = postList.stream()
				.map(RedditPost::createdOn)
				.max(LocalDateTime::compareTo);
			if (newestPostTime.isPresent()) {
				lastPostTime = newestPostTime.get();
			}
		}
		
		if (postList.size() != filteredList.size()) {
			Logger.debug("Filter old posts: " + (postList.size() - filteredList.size()));
		}
		return filteredList;
	}
	
	/**
	 * Filter to remove posts that have already been responded to
	 */
	public List<RedditPost> filterResponded(List<RedditPost> postList) {

		// Gets all posts that were posted by self
		// Stores the parentId of all those posts
		List<String> respondedPosts = postList.stream()
			.filter(post -> AmosBot.config.USERNAME().equals(post.author))
			.map(post -> post.parentId)
        		.collect(Collectors.toList());
		
		// Removes posts that contain that parentId
		List<RedditPost> filteredList = postList.stream()
        		.filter(post -> { if (!respondedPosts.contains(post.id())) {
        				return true;
        			}
				Logger.debug("Removing post#"+post.id()+" as it has been responded to");
        			return false; }) 
        		.collect(Collectors.toList());   
        
		if (postList.size() != filteredList.size()) {
			Logger.info("Filter responded posts: " + this.removedPosts(postList, filteredList));
		}
		return filteredList;
	}
	
	/**
	 * Filter to remove all posts that were posted by self
	 */
	public List<RedditPost> filterSelf(List<RedditPost> postList) {

        List<RedditPost> filteredList = postList.stream()
        		.filter(post -> { if (!AmosBot.config.USERNAME().equals(post.author)) {
    				return true;
    			}
			Logger.debug("Removing post#" + post.id() + " as it was posted by self");
    			return false; }) 
        		.collect(Collectors.toList());   
        
		if (postList.size() != filteredList.size()) {
			Logger.info("Filter self posts: " + this.removedPosts(postList, filteredList));
		}
		return filteredList;
	}
	
	/**
	 * Filter to remove all posts that were posted by users on the IGNORE_USER_LIST
	 */
	public List<RedditPost> filterIgnoreUsers(List<RedditPost> postList) {

        List<RedditPost> filteredList = postList.stream()
        		.filter(post -> { if (!AmosBot.config.IGNORE_USER_LIST().contains(post.author)) {
    				return true;
    			}
			Logger.debug("Removing post#"+post.id()+" as /u/"+post.author+" is on ignore list");
    			return false; }) 
        		.collect(Collectors.toList());   
        
		if (postList.size() != filteredList.size()) {
			Logger.info("Filter ignored posts: " + this.removedPosts(postList, filteredList));
		}
		return filteredList;
	}
	
	private String removedPosts(List<RedditPost> beforeList, List<RedditPost> afterList) {
		List<RedditPost> removedPosts = new ArrayList<RedditPost>(beforeList);
		removedPosts.removeAll(afterList);
		return AmosBotUtils.formatPostIDList(removedPosts);
	}
}
