package me.koolsource.GriefDetector.db;

import java.util.Hashtable;

import me.koolsource.GriefDetector.limits.BreakLimit;

import org.bukkit.entity.Player;

public abstract class Database
{
	/** Setup the database */
	public abstract void createTables();
	
	/** Add a report the user has issued to the database */
	public abstract void addReport(final String locationAsString, String playerName,
			final String message, final long timeAsLong, final Player player);
	
	/**
	 * Returns an array of hash tables of all the griefs
	 * 
	 * (with field -> Value mappings i.e. Message -> "dsdfsdf"
	 * 
	 * Limit -> the number of results to retrieve
	 * 
	 * returns null if there are no griefs in the DB
	 */
	public abstract Hashtable<String, String>[] getGriefs(int lowerlimit, int upperlimit);
	
	/**
	 * Adds a detected grief to the database
	 * 
	 * 'When' is the date/time formatted in System.currentTimeMillis which can
	 * be converted to a date later
	 */
	public abstract void addGrief(String location, String player, String message,
			long when);
	
	/**
	 * Delete the grief with the given grief id Returns 'true' if successful.
	 * False when an invalid id was given
	 */
	public abstract Boolean delGrief(int griefid);
	
	/**
	 * Retrieve the grief with ID griefid
	 * 
	 * Returns null if grief ID is invalid (row doesn't exist)
	 */
	public abstract Hashtable<String, String> getGrief(int griefid);
	
	
	/**
	 * Retrieve the cheat with ID griefid
	 * 
	 * Returns null if grief ID is invalid (row doesn't exist)
	 */
	public abstract Hashtable<String, String> getCheat(int cheatId);
	
	/**
	 * Returns an array of hash tables of all the cheats
	 * 
	 * (with field -> Value mappings i.e. Message -> "dsdfsdf"
	 * 
	 * Limit -> the number of results to retrieve
	 * 
	 * returns null if there are no griefs in the DB
	 */
	public abstract Hashtable<String, String>[] getCheats(int lowerlimit, int upperlimit);
	
	/**
	 * Adds a detected cheat to the database
	 * 
	 * 'When' is the date/time formatted in System.currentTimeMillis which can
	 * be converted to a date later
	 */
	public abstract void addCheat(String location, String player, String message, long when);
	
	/**
	 * Delete the grief with the given grief id Returns 'true' if successful.
	 * False when an invalid id was given
	 */
	public abstract Boolean delCheat(int griefid);
	
	/** Returns a count of how many cheats are in the database */
	public abstract int countCheats();
	
	/** Returns a count of how many griefs are in the database */
	public abstract int countGriefs();
	
	public abstract void updateBreakEntry(String playername, BreakLimit limit);
	
	public abstract Hashtable<Integer, BreakLimit> getBreakEntry(int itemid, String playername);
	
	public abstract void addBreakEntry(String playername, BreakLimit limit);

}
