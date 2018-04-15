package me.aelesia.reddit.bot.amosbot;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.aelesia.commons.logger.Logger;
import me.aelesia.commons.storage.JsonTextStorage;
import me.aelesia.commons.storage.Storage;
import me.aelesia.commons.utils.ThreadUtils;
import me.aelesia.reddit.api.O2AClient;
import me.aelesia.reddit.api.RedditAPI;
import me.aelesia.reddit.api.objects.RedditPost;
import me.aelesia.reddit.bot.RedditBot;
import me.aelesia.reddit.bot.amosbot.interfaces.AmosBotAPI;
import me.aelesia.reddit.bot.amosbot.interfaces.InterfaceReddit;

public class AmosBot extends RedditBot implements AmosBotAPI {

	public static final AmosBotConfig config = new AmosBotConfig("config.properties");
	List<RedditPost> history;
	LocalDateTime bootTime = LocalDateTime.now().minusDays(1).minusHours(1).minusMinutes(59);
	
	Storage jsonStorage =  new JsonTextStorage();
	PostFilter filter = new PostFilter();
	
	InterfaceReddit interfaceReddit;
	
	/**
	 * Initializes all required resources by the bot
	 * Loads configuration file
	 * Creates O2AClient based on configuration file
	 * Sets API parameters
	 * Loads historic Amos Yee posts from .txt file
	 */
	@Override
	public void init() {
		
		this.sleepSeconds = AmosBot.config.SLEEP_TIME_SEC();
		
		this.o2aClient = new O2AClient(
			AmosBot.config.USERNAME(),
			AmosBot.config.PASSWORD(),
			AmosBot.config.APP_ID(),
			AmosBot.config.SECRET(),
			AmosBot.config.USER_AGENT()
		);
		
		this.redditAPI = new RedditAPI(o2aClient);
		this.redditAPI.setCommentLimit(AmosBot.config.COMMENT_LIMIT());
		this.redditAPI.setThreadLimit(AmosBot.config.THREAD_LIMIT());
		
		this.interfaceReddit = new InterfaceReddit(this, redditAPI);
		
		try {
			history = (List<RedditPost>)(Object)jsonStorage.loadList(AmosBot.config.FILE_HISTORY(), RedditPost.class);
		} catch (IOException e) {
			Logger.warn("Unable to load file history. Generating new file.");
			
			history = new ArrayList<RedditPost>();
			RedditPost post = new RedditPost();
			post.author = AmosBot.config.DEVELOPER();
			post.threadId = "";
			post.threadTitle = AmosBot.config.DEVELOPER();
			post.url = "https://www.reddit.com/user/"+AmosBot.config.DEVELOPER()+"/";
			post.setCreatedOn(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
			history.add(post);
			jsonStorage.save(history, AmosBot.config.FILE_HISTORY());
		}
	}

	/** 
	 * Retrieves all posts contained in SUBREDDIT_LIST
	 * Creates a new thread for each subreddit so that it will be processed in parallel
	 */
	@Override
	protected List<RedditPost> retrieve() {
		List<RedditPost> postList = Collections.synchronizedList(new ArrayList<RedditPost>());
		List<Thread> multithreadList = new ArrayList<Thread>();
		
		for (String subreddit : AmosBot.config.SUBREDDIT_LIST()) {
			multithreadList.add(new Thread(){ public void run() {
				postList.addAll(redditAPI.retrieveNewComments(subreddit));
			}});
			
			multithreadList.add(new Thread(){ public void run() {
				postList.addAll(redditAPI.retrieveNewThreads(subreddit));
			}});
		}

		ThreadUtils.startAllThreads(multithreadList);
		ThreadUtils.joinAllThreads(multithreadList);
		return postList;
	}
	
	/** 
	 * Performs filtering of posts
	 */
	@Override
	protected List<RedditPost> filter(List<RedditPost> postList) {
		postList = filter.filterOldPosts(postList);
		postList = filter.filterResponded(postList);
		postList = filter.filterSelf(postList);
		postList = filter.filterIgnoreUsers(postList);
		
		if (postList.size() != 0) {
			Logger.info("Retrieved " + postList.size() + " new post(s): " + AmosBotUtils.formatPostIDList(postList));
		}
		return postList;
	}

	/** 
	 * Performs scanning of posts
	 */
	@Override
	protected void scan(RedditPost post) {
		if (PostScanner.isAmosPost(post)
				&& PostScanner.isNewAmosThread(post, history)
				&& PostScanner.cooldown(post, lastPost())) {
			Logger.info("Counter has been reset! Post #" + post.id() + " , user: " + post.author);
			redditAPI.reply(post.id(), Response.resetCounter(post, history.get(history.size()-1)));
			history.add(post);
			jsonStorage.save(history, AmosBot.config.FILE_HISTORY());
		} 
		else if (PostScanner.isSummoningBot(post)) {
			this.interfaceReddit.run(post);
		};
	}
	
	private RedditPost lastPost() {
		return history.get(history.size()-1);
	}

	
	@Override
	public AmosBotAPI.obj history() {
		return AmosBotAPI.obj.create().history(history);
	}

	@Override
	public AmosBotAPI.obj time() {
		AmosBotAPI.obj obj = new AmosBotAPI.obj();
		obj.lastPostAuthor = lastPost().author;
		obj.lastPostElapsed = lastPost().createdOn.until(LocalDateTime.now(), AmosBot.config.COOLDOWN_TYPE()) + " " + AmosBot.config.COOLDOWN_TYPE().name().toLowerCase();
		obj.lastPostUrl = lastPost().url;
		obj.lastPostCreOn = lastPost().createdOn;
		return obj;
	}

	@Override
	public AmosBotAPI.obj status() {
		AmosBotAPI.obj obj = new AmosBotAPI.obj();
		obj.uptime = AmosBotUtils.formatElapsedTime(ChronoUnit.SECONDS.between(bootTime, LocalDateTime.now()));
		return obj;
	}

	@Override
	public void resume() {
		pause = false;
	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public String add(String key, String value) {
		AmosBot.config.addToList(key, value);
		return (key + ": " + AmosBot.config.getList(key));
	}

	@Override
	public String remove(String key, String value) {
		AmosBot.config.removeFromList(key, value);
		return (key + ": " + AmosBot.config.getList(key));
	}

	@Override
	public String update(String key, String value) {
		AmosBot.config.update(key, value);
		return (key + ": " + AmosBot.config.get(key));
	}
}
