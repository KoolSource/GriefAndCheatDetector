package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.Yaml;

import me.koolsource.GriefDetector.GriefDetector;
import me.koolsource.GriefDetector.PlayerActivity;
import me.koolsource.GriefDetector.db.Database;
import me.koolsource.GriefDetector.db.MySQLDatabase;
import me.koolsource.GriefDetector.db.SQLiteDatabase;

public class Setup
{
	GriefDetector instance;
	Logger log = Logger.getLogger("GCD setup");
	Map config;
	private String directory;
	
	public Setup(GriefDetector instance) {
		this.instance = instance;
	}
	
	 /*
	   * Create the plugins/griefDetector dir, and put the default files in it if they don't already exist
	   */
	  public void generateFiles()
	  {
	      this.directory = "plugins" + File.separator + instance.getDescription().getName();
	      File configfile = new File(directory + File.separator + "config.yml");
	      File dir =  new File(directory);
	      
	      if(!dir.exists()) {
	    	  dir.mkdir();
	      }
	      
	      if(!configfile.exists()) {
	    	  
	    	try
			{
	    	    configfile.createNewFile();
				
				Configuration config = new Configuration(configfile);
				
				config.setHeader("# KoolSource Grief and Hack detector \n"
				+ "# Please visit http://www.koolsource.com for more plugin releases, and gaming and web design related services.\n"
				+ "# NOTE: CONFIG MUST USE 4 SPACES FOR INDENTATION, NOT TABS. CONFIG IS IN YML FORMAT [http://en.wikipedia.org/wiki/YAML] \n");
				
				// SQL stuff
				Hashtable<String, String> sql = new Hashtable<String, String>();
				sql.put("Use-SQLite", "yes");
				sql.put("sql-hostname", "your sql host name (only if Use-SQLite: 'no')");
				sql.put("sql-username", "your sql username (only if Use-SQLite: 'no')");
				sql.put("sql-password", "your sql password (only if Use-SQLite: 'no')");
				sql.put("sql-db-name", "your sql database (only if Use-SQLite: 'no')");
				
				config.setProperty("SQL_SETTINGS", sql);

				// Report throttling
				config.setProperty("report-time-limit", 30);
				
				// Set up the worlds that are exempt from cheat detection
				
				ArrayList<String> exempt = new ArrayList<String>();
				exempt.add("worldGoesHere");
				exempt.add("otherWorldGoesHere");
				
				config.setProperty("MINE_DETECTION_EXEMPT_WORLDS", exempt);					
				
				// Set up the mines limits
					
				ArrayList<Map> list = new ArrayList<Map>();
				
				Map current = new Hashtable();
				current.put("item_id", 56);
				current.put("amount", 12);
				current.put("time_frame", 30);
				
				list.add(current);
				
				current = new Hashtable();
				current.put("item_id", 14);
				current.put("amount", 12);
				current.put("time_frame", 30);
				
				list.add(current);
							
				config.setProperty("MINE_LIMITS", list);
				
				//MINE IN THE DARK VALUES
				
				list = new ArrayList<Map>();
				current = new Hashtable();
				current.put("item_id", 56);
				current.put("amount", 4);
				current.put("time_frame", 1);
				
				list.add(current);
				
				current = new Hashtable();
				current.put("item_id", 14);
				current.put("amount", 10);
				current.put("time_frame", 5);
				
				list.add(current);
				
				current = new Hashtable();
				current.put("item_id", 15);
				current.put("amount", 6);
				current.put("time_frame", 1);
				
				list.add(current);
				
				
				config.setProperty("MINE_IN_DARK_LIMITS", list);
				
				// Set up the default materials to care about griefing on..
				list = new ArrayList<Map>();
				current = new Hashtable();
				
				current.put("item_id", 20);
				current.put("threshhold", 1);
				current.put("first-break-override", true);
				
				list.add(current);
				
				current = new Hashtable();
				current.put("item_id", 5);
				current.put("threshold", 3);
				current.put("first-break-override", true);
				
				list.add(current);
				config.setProperty("PLACE-BREAK-RATIO-SETTINGS", list);
				config.save();
				

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				log.info("GCD: ERROR setting up config file");
				e.printStackTrace();
			} 
	    	  
	      }

		  generateChatFiles();
			  
	  }
	  
	  public void generateChatFiles()
	  {
	  	  String cheatString = "i have a mod, i've got a mod, ive got a mod, x-ray, xray, x ray, see through walls, see where diamonds, i'm hacking, im hacking, i've got a hack, ive got a hack, i can see where diamond, i can see were diamond, i can fly, i will fly, i'll fly, my health regenerates, i have 10 hearts, i can't die, don't take fall damage, fly mod, flight mod";
	  	  String griefString = "report you, stop griefing, is griefing, stealing my, breaking my, griefing my, destroying my, stole my, broke my, griefed my, destroyed my, stole from my, got griefed, i've been griefed, ive been griefed, we've been griefed, weve been griefed, set on fire, griefer!, been griefed, someone blew up, robbed my, been robbed";
	  		
	        String directory = "plugins" + File.separator + instance.getDescription().getName();
	        File cheats = new File(directory + File.separator + "cheats.txt");
	        
	        if(!cheats.exists()) {
	      	  try
	  		{
	  			cheats.createNewFile();
	  			cheatString = cheatString.replace(", ", "\n");
	  			PrintWriter pw = new PrintWriter(new FileWriter(cheats), true);
	  			
	  			Scanner lineScanner = new Scanner(cheatString);
	  			while (lineScanner.hasNext()) {
	  				pw.write(lineScanner.nextLine() + "\n");
	  			}
	  			pw.flush();
	  			
	  			
	  		} catch (IOException e)
	  		{
	  			log.info("GCD: Error creating cheats.txt");
	  			e.printStackTrace();
	  		}
	      	  
	      	  
	        }
	        
	        File griefs = new File(directory + File.separator + "griefs.txt");
	        
	        
	        if(!griefs.exists())
	        {
	      	try {
	  			griefs.createNewFile();
	  			griefString = griefString.replace(", ", "\n");
	  			
	  			
	  			PrintWriter pw = new PrintWriter(new FileWriter(griefs), true);
	  			
	  			Scanner lineScanner = new Scanner(griefString);
	  			while (lineScanner.hasNext()) {
	  				pw.write(lineScanner.nextLine() + "\n");
	  			}
	  			pw.flush();
	  			
	  			
	  		} catch (IOException e)
	  		{
	  			log.info("GCD: Error creating griefs.txt");
	  			e.printStackTrace();
	  		}
	      	  
	      	  
	        }
	        
	  		
	  }
	  public Map setUpConfigs()
	  {
		  InputStream input;
		  try {
			  input = new FileInputStream(new File("plugins/GriefDetector/config.yml"));
			  Yaml yaml = new Yaml();
			  config = (Map) yaml.load(input);

			  instance.setREPORT_LIMIT((Integer) config.get("report-time-limit") );

		  } catch (FileNotFoundException e)
		  {
			  // config file not found - disable the plugin
			  log.info("GCD config file missing, disabling plugin. Please create a config.yml file in /plugins/GriefDetector");
			  instance.disable();
			  return null;

		  } 

		  if (instance.getREPORT_LIMIT() < 0) //changed to < 0 from 0 (initialised as -1 also) in case in the config implicitly states 0 as the limit
		  {
			  log.info("No report-time-limit set in config, defaulting to 30 seconds");
			  instance.setREPORT_LIMIT(30);
		  }
		  return config;
	  }


	  public Database setupDB()
	  {
		    /*
		     * Set up the database connection
		     */
		    
		    try {
		    	Map db = (Map) config.get("SQL_SETTINGS");
		    	
		    	if (db.get("Use-SQLite") == null) {
		    		log.info("Grief & Cheat Detector: config file is missing the 'Use-SQLite' option. Check the forum " +
		    				"thread for the latest default config, as reference. Defaulting to use MySQL settings. ");
		    		return new MySQLDatabase((String) db.get("sql-hostname"), (String) db.get("sql-username"), (String) db.get("sql-password"),  (String) db.get("sql-db-name"), instance);

		    	}
		    	
		    	if (((String) db.get("Use-SQLite")).equalsIgnoreCase("yes")) {
		    		return new SQLiteDatabase(directory, instance);
		    	} else if (((String) db.get("Use-SQLite")).equalsIgnoreCase("no")){
		    		return new MySQLDatabase((String) db.get("sql-hostname"), (String) db.get("sql-username"), (String) db.get("sql-password"),  (String) db.get("sql-db-name"), instance);
		    	}
		    	else {
		    		log.info("GCD: There is an issue with the config's SQL settings. Please ask for support in the forum thread. Disabling plugin.");
		    		instance.disable();
		    		return null;
		    	}
		    	
		    	
		    	
		    } catch (NullPointerException e)
		    {
		    	// config is missing paramters
		    	e.printStackTrace();
		    	log.info("GCD: Config file is missing SQL credentials. Disabling plugin.");
		    	instance.disable();
		    	return null;
		    }

	  }
	  
	  public ConcurrentHashMap<Player, PlayerActivity> setupPlayerHashTable()
	  {
		  ConcurrentHashMap<Player, PlayerActivity> players = new ConcurrentHashMap<Player, PlayerActivity>();
		  
		  /* Create a hash table of players to record the activities of */
		  Player[] onlineplayers = instance.getServer().getOnlinePlayers();
		  for (int i = 0; i < onlineplayers.length; i++)
		  {
			  players.put(onlineplayers[i], new PlayerActivity(onlineplayers[i], config));
		  }
		  
		  return players;
	  }
	

}
