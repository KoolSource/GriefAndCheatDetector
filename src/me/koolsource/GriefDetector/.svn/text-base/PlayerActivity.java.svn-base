package me.koolsource.GriefDetector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import me.koolsource.GriefDetector.limits.BreakLimit;
import me.koolsource.GriefDetector.limits.MineLimit;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


/** This class represents a player's activity that we're interested in
 * Will keep track of times player last did a certain thing, accumulating suspicious items, etc*/
public class PlayerActivity
{
	
	Logger log = Logger.getLogger("playerActivity");
	
	private Player player;
	private Hashtable<Integer, MineLimit> mineLimits;
	
	private long loggedout = 0;
	private boolean isOnline = true;

	private Hashtable<Integer, MineLimit> lightLimits;
	private Hashtable<Integer, BreakLimit> breakLimits = new Hashtable<Integer, BreakLimit> () ;
	
	private BlockFace lastInteracted;

	private Map config;
		
	public PlayerActivity(Player player, Map config) {
		this.player = player;
		this.config = config;
		
		// If you're wondering why this says 'Integer', apparently hash tables don't like primitives
		// So the int is 'wrapped' in its Integer object wrapper class :P
		mineLimits = new Hashtable<Integer, MineLimit>();
		lightLimits =  new Hashtable<Integer, MineLimit>();
		
		
		setupMineLimits();
		setupMineinDarkLimits();
		setupBreaklimits();
		
		
	}
	
	public BlockFace getLastInteracted()
	{
		return lastInteracted;
	}

	public void setLastInteracted(BlockFace lastInteracted)
	{
		this.lastInteracted = lastInteracted;
	}

	public long getLoggedout()
	{
		return loggedout;
	}

	public void setLoggedout(long loggedout)
	{
		this.loggedout = loggedout;
	}

	public synchronized boolean isOnline()
	{
		return isOnline;
	}

	public synchronized void setOnline(boolean isOnline)
	{
		this.isOnline = isOnline;
	}


	
	private void setupBreaklimits()
	{

		Hashtable<Integer, Map> temp = new Hashtable<Integer, Map>();;
		ArrayList<Map> breaklims = (ArrayList<Map>) config.get("PLACE-BREAK-RATIO-SETTINGS");
		
		if (breaklims != null) {
			Integer id = null;
			Integer threshold = null;
			Boolean overide = null;
			
			for (Map lim: breaklims) {
				id = (Integer)lim.get("item_id");
				threshold = (Integer)lim.get("threshhold");
				overide = (Boolean)lim.get("first-break-override");
				
				if (id == null || threshold == null || overide == null) {
					log.info("GCD: Missing parameter in PLACE-BREAK-RATIO-SETTINGS configuration block. Please format in the following style:" +
					"- {item_id :  20, threshhold: 1, first-break-override: true}");
				} else {
					temp.put(id, lim);
				}
			}
			
		}
		
		// Check if any database entries have to be updated with new break, place, threshold, etc values..
		// Also, load database rows into the breaklims hashtable.. 
		
		
		
		
	}

	private void setupMineinDarkLimits()
	{
		ArrayList<Map> lights = (ArrayList<Map>) config.get("MINE_IN_DARK_LIMITS");
		
		if (lights != null) {
			Integer id = null;
			Integer amount = null;
			Integer thelimit = null;
			for (Map limit: lights) {
				id = (Integer)limit.get("item_id");
				amount = (Integer)limit.get("amount");
				thelimit = (Integer)limit.get("time_frame");
				
				if (id == null || amount == null || thelimit == null) {
					log.info("GCD: Missing parameter in MINE_IN_DARK_LIMITS configuration block. Please format in the following style:" +
					"- {item_id : 56, amount: 12, time_frame: 30}");
				} else {
					thelimit *= 60000;
					lightLimits.put(id, new MineLimit(id, amount, thelimit));
				}
		
			}
		}
		
	}

	private void setupMineLimits()
	{
		ArrayList<Map> limits = (ArrayList<Map>) config.get("MINE_LIMITS");
		
		if (limits != null) {
			Integer id = null;
			Integer amount = null;
			Integer thelimit = null;
			for(Map limit: limits) {
				

				id = (Integer)limit.get("item_id");
				amount = (Integer)limit.get("amount");
				thelimit = (Integer)limit.get("time_frame");

				if (id == null || amount == null || thelimit == null) {
					log.info("GCD: Missing parameter in MINE_LIMITS configuration block. Please format in the follow style:" +
					"- {item_id : 56, amount: 12, time_frame: 30}");
				} else {
					thelimit *= 60000;
					mineLimits.put(id, new MineLimit(id, amount, thelimit));
				}
			}
			
			
		}
		
	}

	/** Run this method when somebody has just mined a block. If we're monitoring the block, does the following:
	 * 
	 * If the item was first mined > time minutes ago, resets counts.
	 * If counters are 0, initialises new count. Else, +1 to item count
	 * 
	 * 
	 * Returns True if they've reached the conditions where they've mined a suspicious amount in a small
	 * time frame, and False for normal behaviour
	 * @param id */

	public MineLimit mined(int id) {
		
		MineLimit limit = mineLimits.get(new Integer(id));
		
		// If this is null, the entry isn't in the hash table meaning we don't care how much of this item
		// they've mined
		if (limit == null) {
			return null;
		}
		else {
			// Return if what they just mined exceeded the limits specified
			if(limit.justMined()) {
				return limit;
			}
		}
		return null;
	}
	
	/** Call this method if somebody mines in the dark, to count the amount of ores of types we care about they mine. 
	 * If the amount exceeds the amount in a timeframe specified in the config, return the MineLimit object to generate
	 * a report from */
	public MineLimit minedInDark(int id) {
		MineLimit limit = lightLimits.get(new Integer(id));
		
		// If this is null, the entry isn't in the hash table meaning we don't care how much of this item
		// they've mined
		if (limit == null) {
			return null;
		}
		else {
			// Return if what they just mined exceeded the limits specified
			if(limit.justMined()) {
				return limit;
			}
		}
		return null;
		
	}
	
	public Boolean hasRegisteredMineLimit(Integer key) {
		return mineLimits.containsKey(key);
	}
	
	public Boolean hasRegisteredMineInDarkLimit(Integer key) {
		return lightLimits.containsKey(key);
	}

}
