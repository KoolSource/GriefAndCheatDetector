package me.koolsource.GriefDetector;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** This class represents a grief report */

// Will be used to construct a class out of the fields that are sent to the
// grief report database
// This will be useful to do things such as format the location in a way that
// can be used for the teleport command

public class GriefReport
{
	Logger log = Logger.getLogger("report");
	private Location location;
	private long time;
	GriefDetector plugin;
	private String playerName;
	private String message;
	private String id;

	// Constructor for constructing a report
	public GriefReport(String id, Player player, long time, String message,
			GriefDetector instance)
	{
		this.id = id;
		this.location = player.getLocation();
		this.time = time;
		this.plugin = instance;
		this.playerName = player.getName();
		this.message = message;
	}

	// Constructor for 'de-constructing' a report
	public GriefReport(String id, String location, long time, String name,
			String message, GriefDetector instance)
	{
		this.id = id;
		this.plugin = instance;
		this.location = getLocationFromString(location);
		this.time = time;
		this.playerName = name;
		this.message = message;
	}

	// Constructor for 'de-constructing' a report (from a hash table)
	public GriefReport(Hashtable<String, String> report, GriefDetector instance)
	{
		// Use the above constructor
		this(report.get("id"), report.get("location"), Long.parseLong(report
				.get("time")), report.get("player"), report.get("message"),
				instance);
	}

	public Location getLocation()
	{
		return location;
	}

	// For storing in database
	public String getLocationAsString()
	{
		return location.getWorld().getName() + "," + location.getX() + ","
				+ location.getY() + "," + location.getZ();
	}

	// For converting back into a location from the database
	private Location getLocationFromString(String location)
	{
		//log.info("location is: " + location);
		
		String[] split = location.split(",");
		
		Location loc = new Location(plugin.getServer().getWorld(split[0]),
				Double.parseDouble(split[1]), Double.parseDouble(split[2]),
				Double.parseDouble(split[3]));

		return loc;
	}

	public String getTimeAsString()
	{
		// Construct date from milliseconds since 1970
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat();
		// Presents a readable date and time
		String timeAsString = format.format(date);
		return timeAsString;
	}

	public long getTimeAsLong()
	{
		return time;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public String getMessage()
	{
		return message;
	}

	public String getId()
	{
		// TODO Auto-generated method stub
		return id;
	}

}
