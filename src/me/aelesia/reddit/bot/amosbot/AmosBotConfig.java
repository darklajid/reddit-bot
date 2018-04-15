package me.aelesia.reddit.bot.amosbot;

import java.time.temporal.ChronoUnit;
import java.util.List;

import me.aelesia.commons.configuration.Config;

public class AmosBotConfig extends Config {

	public String VERSION() { return this.config.getString("version"); }
	public String DEVELOPER() { return this.config.getString("developer"); }

	public String APP_ID() { return this.config.getString("app_id"); }
	public String SECRET() { return this.config.getString("secret"); }
	public String USERNAME() { return this.config.getString("username"); }
	public String PASSWORD() { return this.config.getString("password"); }
	public String USER_AGENT() { return this.config.getString("user_agent"); }
	
	public int COMMENT_LIMIT() { return this.config.getInt("comment_limit"); }
	public int THREAD_LIMIT() { return this.config.getInt("thread_limit"); }
	public int SLEEP_TIME_SEC() { return this.config.getInt("sleep_time_sec"); }
	
	public String FILE_HISTORY() { return this.config.getString("file_history"); }
	public String TESTING_THREAD() { return this.config.getString("testing_thread"); }
	
	public List<String> SUBREDDIT_LIST() { return this.config.getList(String.class, "subreddit_list"); }
	public List<String> IGNORE_USER_LIST() { return this.config.getList(String.class, "ignore_user_list"); }
	public List<String> ADMIN_USER_LIST() { return this.config.getList(String.class, "admin_user_list"); }
	
	public long COOLDOWN_DURATION() { return this.config.getLong("cooldown_duration"); }
	public ChronoUnit COOLDOWN_TYPE() {
		if ("minutes".equalsIgnoreCase(config.getString("cooldown_type"))) {
			return ChronoUnit.MINUTES;
		} else if ("hours".equalsIgnoreCase(config.getString("cooldown_type"))) {
			return ChronoUnit.HOURS;
		} else if ("days".equalsIgnoreCase(config.getString("cooldown_type"))) {
			return ChronoUnit.DAYS;
		} else {
			throw new IllegalArgumentException("Unrecognized ChronoUnit: " + config.getString("cooldown_type"));
		}
	}
	
	public AmosBotConfig(String fileName) {
		super(fileName);
	}

	@Override
	protected void map() {
	}

	@Override
	public String toString() {
		return "[" +
				"VERSION: " + VERSION() +
				", DEVELOPER: " + DEVELOPER() +
				", APP_ID: " + APP_ID() +
				", SECRET: " + SECRET() +
				", USERNAME: " + USERNAME() +
				", PASSWORD: " + PASSWORD() + 
				", USER_AGENT: " + USER_AGENT() + 
				", COMMENT_LIMIT: " + COMMENT_LIMIT() + 
				", THREAD_LIMIT: " + THREAD_LIMIT() + 
				", SLEEP_TIME_SEC: " + SLEEP_TIME_SEC() + 
				", SUBREDDIT_LIST: " + SUBREDDIT_LIST() + 
				", IGNORE_USER_LIST: " + IGNORE_USER_LIST() + 
				", ADMIN_USER_LIST: " + ADMIN_USER_LIST() +
				", FILE_HISTORY: " + FILE_HISTORY() + 
				", COOLDOWN_DURATION: " + COOLDOWN_DURATION() + 
				", COOLDOWN_TYPE: " + COOLDOWN_TYPE().name().toLowerCase() + 
				"]";
	}
}
