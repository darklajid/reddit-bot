package me.aelesia.reddit.bot.amosbot.interfaces;

import java.time.LocalDateTime;
import java.util.List;

import me.aelesia.reddit.api.objects.RedditPost;

public interface AmosBotAPI {
	
    public enum e {
        HISTORY("!history"),
        TIME("!time"),
        STATUS("!status"),
        RESUME("!resume"),
        PAUSE("!pause"),
        ADD("!add"),
        REMOVE("!remove"),
        UPDATE("!update");
        
        private String str;
      
        e(String str) {
        		this.str = str;
        }
        
        public String toString() {
        		return this.str;
        }
        
        public static e fromString(String str) {
        		for (e value : e.values()) {
        			if (str.equalsIgnoreCase(value.toString())) {
        				return value;
        			}
        		}
        		return null;
        }
    }
    
    public class obj {
    		public String response;
    		
    		public String uptime;
    		public List<RedditPost> history;
    		
    		public RedditPost lastPost;
    		
    		public static obj create() {
    			return new obj();
    		}
    		public obj history(List<RedditPost> history) {
    			this.history = history;
    			return this; 
    		}
    }
	
	public AmosBotAPI.obj history();
	
	public AmosBotAPI.obj time();
	
	public AmosBotAPI.obj status();

	public void resume();
	
	public void pause();

	public String add(String config, String param);
	
	public String remove(String config, String param);

	public String update(String config, String param);
}
