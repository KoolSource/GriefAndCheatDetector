package me.koolsource.GriefDetector;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import me.koolsource.GriefDetector.db.Database;
import me.koolsource.GriefDetector.db.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import Util.Setup;
import Util.SyncMethods;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//test
/**
 * @author Happy0, Brad;
 * 
 *
 */
public class GriefDetector extends JavaPlugin {

	public double verNum = 0.2; // version number
	// Enumerations for the commands for use in the switch statement
	public enum Cmd {REPORT, SHOWGRIEFS, TPGRIEF, DELGRIEF, SHOWCHEATS, TPCHEAT, DELCHEAT}

	private GriefDetectorPlayerListener playerListener;
	private MineListener mineListener;
	private GriefListener griefListener;

	Database db;

	long lastReport;
	private int REPORT_LIMIT = -1;
	Logger log = Logger.getLogger("grief");
	ConcurrentHashMap<Player, PlayerActivity> players;

	private GriefDetector instance;
	Map config;

	public static PermissionHandler permissionHandler;

	//test
	@Override
	public void onEnable()
	{

		instance = this;
		log.info("Enabling Grief & Cheat Detector " + versionNum + "...");

		Setup setup = new Setup(this);
		setup.generateFiles();
		config = setup.setUpConfigs();
		players = setup.setupPlayerHashTable();
		db = setup.setupDB();

		// If any of the above failed, it should disable the plugin..
		if (isEnabled()) {
			registerEvents();
			setupPermissions();
			log.info("Grief & Cheat Detector Enabled");
		}


	}


  /*
   *  Listen for all of this stuff (players joining, blocks breaking, etc) 
   *  
  */
  private void registerEvents()
  {
	  PluginManager pm = getServer().getPluginManager();
	  playerListener = new GriefDetectorPlayerListener(this, db, players, config);
	  mineListener = new MineListener(this, db, players, config);
	  griefListener = new GriefListener(this, db, players, config);
	  
	  // if something here is being listened for, its because its being used, don't
	  // remove any entries, or add any unneeded ones
	  
	  // Maintain the player list hashtable
	  pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
	  pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
	  pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Monitor, this);
	  
	  // Monitoring chat for key phrases
	  pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
	 
	  // Important for mine in the dark detection
	  pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);

	  // Mine Cheat Listener..
	  pm.registerEvent(Event.Type.BLOCK_BREAK, mineListener, Priority.Monitor, this);
	  
	  // Grief Listener: Monitors the ratio of block breaks, to block places
	  pm.registerEvent(Event.Type.BLOCK_PLACE, griefListener, Priority.Monitor, this);
	
  }
  private void setupPermissions()
  {
	    if (permissionHandler != null) {
	        return;
	    }

	  Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	  
	    if (permissionsPlugin == null) {
	        log.info("Permission system not detected, defaulting to OP");
	        return;
	    }
	    

	    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	    log.info("Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());

  }

@Override
  public void onDisable() {
    log.info("Disabling Grief & Cheat Detector...");
    // Don't know if this line is needed, or bukkit takes care of it itself, but best to be safe :|
    this.getServer().getScheduler().cancelAllTasks();
    
    log.info("Grief & Cheat Detector Disabled");
  }
  
  public boolean onCommand(CommandSender sender, Command command,
                           String label, String[] args) {
    
    // if the player is the sender
    if (sender instanceof Player) {
      
      Player player = (Player) sender;
      
      /*
       * Get the command the user has sent run relevant method Else do
       * magic stuff and things and beans.
       */
      
      String usedCommand = command.getName().toUpperCase();
      Cmd cmd = Cmd.valueOf(usedCommand);
      
      // Added returns (these methods will return true if command is in
      // the right format. False otherwise, to show the 'usage'
      // Added 'args' 'cause these methods will need to use the arguments
      // :P
      switch (cmd) {
      case REPORT:
    	  if (args.length > 0) {
    		  cmdReport(args,player);
    		  return true;
    	  }
    	  else {
    		  player.sendMessage("You must include a message in your report! Report NOT sent.");
    		  return false;
    	  }
      case SHOWGRIEFS:
    	  cmdShowGriefs(args, player);
    	  return true;
      case TPGRIEF:
    	  if (args.length == 0)
    	  {
    		  // No grief ID given
    		  player.sendMessage("No Grief ID provided");
    		  return false;
    	  } else if (args.length > 1)
    	  {
    		  // Too many arguments
    		  player.sendMessage("Too many arguments");
    		  return false;
    	  }

    	  cmdTpGrief(args, player);
    	  return true;
      case DELGRIEF:
    	  if (args.length == 0)
    	  {
    		  // No grief ID given  
    		  player.sendMessage("No Grief ID provided");
    		  return false;
    	  }
    	  else if (args.length > 1)
    	  {
    		  //Too many arguments
    		  player.sendMessage("Too many arguments");
    		  return false;
    	  }

    	  cmdDelGrief(args, player);
    	  return true;
      case SHOWCHEATS:    	
    	  cmdShowCheats(args, player);
    	  return true;
      case TPCHEAT:
    	  if (args.length == 0)
    	  {
    		  // No cheat ID given
    		  player.sendMessage("No Cheat ID provided");
    		  return false;
    	  } else if (args.length > 1)
    	  {
    		  // Too many arguments
    		  player.sendMessage("Too many arguments");
    		  return false;
    	  }

    	  cmdTpCheat(args, player);
    	  return true;
      case DELCHEAT:
    	  if (args.length == 0)
    	  {
    		  // No grief ID given  
    		  player.sendMessage("No cheat ID provided");
    		  return false;
    	  }
    	  else if (args.length > 1)
    	  {
    		  //Too many arguments
    		  player.sendMessage("Too many arguments");
    		  return false;
    	  }


    	  cmdDelCheat(args, player);
    	  return true;
      default:
    	  return false;
      } // close switch statement
    } // end if the player is the sender
    return false;
  } // end onCommand
  

  private void cmdReport(String[] args, Player player)
  {
	  //Get the current time since 1970 seconds  
	  long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

	  // Players can report griefers
	  // Throttle number of times someone may do this so e-mail boxes don't get flooded
	  if (currentTime - lastReport < REPORT_LIMIT)
	  {
		  player.sendMessage("Too many reports have been issued in too short a time frame. Please wait and try again.");
		  String myMessage = "Report spam detected from " + player.getDisplayName(); 
		  log.info("GCD: " + myMessage);
		  return;
	  }

	  // Reconstruct the message from the arguments
	  String reportMsg = "";
	  for (int i = 0; i < args.length; i++)
	  {
		  if (i == 0){reportMsg += args[i];} // If the first word, don't have a space before it.
		  else {reportMsg += " " + args[i];} // Add spaces between words.
	  }

	  GriefReport report = new GriefReport("0", player, System.currentTimeMillis(), reportMsg, instance);

	  log.info("Report from " + player.getName() + ": " + reportMsg);

	  Alert griefAlert = new Alert(this.getServer().getOnlinePlayers());
	  griefAlert.alertGrief("Report from " + player.getName() + ": " + reportMsg, false);

	  db.addReport(report.getLocationAsString(), report.getPlayerName(), report.getMessage(), report.getTimeAsLong(), player);
	  player.sendMessage("Report sent. An admin has been alerted.");

	  // Get current time for "lastReport" in seconds
	  lastReport = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	  return;
  }
  // end cmdReport
  
  private void cmdShowEntries(final String[] args, final Player player, final Boolean grief) {
	  this.getServer().getScheduler().scheduleAsyncDelayedTask(this,
				new Runnable() {
		  
		  final GriefDetector test = instance;

			public void run() {

				Hashtable<String, String>[] result;

				int rows = grief ? db.countGriefs() : db.countCheats();
				int pages = rows / 4;
				if (rows % 4 > 0)
					pages++; // there's a remainder so there's another page (add one to pages variable)
				int pageno = 0;

				if (args.length == 0)
				{
					// Return the first page, no arguments given TODO: make this say 'do
					// /sg <page number> to see next page'
					result = grief ? db.getGriefs(0, 4) : db.getCheats(0, 4);
				}
				else
					if (args.length == 1)
					{
						pageno = Integer.parseInt(args[0]) - 1;

						if (pageno + 1 > pages)
						{
							SyncMethods.syncSendMessage(player, "There are only " + pages  + " pages", instance);
							return;
						}
						else
						{
							result = grief ? db.getGriefs(4 * pageno, 4) : db.getCheats(4 * pageno, 4);
						}

					}
					else
					{
						// This is checked here, rather than in onCommand incase we ever want to do /showgriefs 1 2 [pages 1-2]
						
						SyncMethods.syncSendMessage(player, "This command takes 0 or 1 parameters only.", instance);
						return;
					}

				if (result == null)
				{
					SyncMethods.syncSendMessage(player, "No new events.", instance);
					return;
				}
				else
				{
			SyncMethods.syncSendMessage(player, (ChatColor.DARK_PURPLE + (grief ? "Grief " : "Cheat ") + "Reports: " + ChatColor.YELLOW + " Commands:"+ ChatColor.AQUA + " /tp " + (grief? "grief [ID] " : "cheat [ID] ") + ChatColor.YELLOW + "& " + ChatColor.AQUA + "/del " + (grief? "grief [ID] " : "cheat [ID] ")), instance);
			SyncMethods.syncSendMessage(player, ("Displaying page " + (pageno + 1) + " of " + (pages) + ". Use " + (grief ? "/showgriefs [page] " : "/showcheats [page] ") + "to view other pages. "), instance);
			for (int i = 0; i < result.length; i++)
			{
				Hashtable<String, String> toCheck = result[i];
				String check = toCheck.get("player");
				if (check == null)
				{
					// The other hash tables are empty
					break;
				}
				GriefReport report = new GriefReport(result[i], instance);

				//print the list item to the player
				SyncMethods.syncSendMessage(player,(ChatColor.RED + "ID: "   	+ ChatColor.GREEN + "[" + ChatColor.RED + report.getId() + ChatColor.GREEN + "] "
					             + ChatColor.RED + "Concerns: " + ChatColor.GREEN + "[" + report.getPlayerName() + ChatColor.GREEN + "] "
					           //+ ChatColor.RED + "At:"   		+ ChatColor.GREEN + "[" + location + "] "
					             + ChatColor.RED + "Message: "  + ChatColor.GREEN + "[" + report.getMessage() + "] "
					             + ChatColor.RED + "Time: " 	+ ChatColor.GREEN + "[" + report.getTimeAsString() + "]"), instance);
 
      
      
		        /*
		        player.sendMessage(ChatColor.RED + "ID: "   	+ ChatColor.GREEN + "[" + ChatColor.YELLOW + id + ChatColor.GREEN + "] "
					             + ChatColor.RED + "Concerns: " + ChatColor.GREEN + concerns + " "
					             + ChatColor.RED + "Message: "  + ChatColor.GREEN + "\"" + message + "\" "
					             + ChatColor.RED + "Time: " 	+ ChatColor.GREEN + time);
		         */
			}
			return;
		}
			}

		});
	  
  }
  
  private void cmdTpEntry(final String[] args, final Player player, final Boolean grief) {
		this.getServer().getScheduler().scheduleAsyncDelayedTask(this,
				new Runnable()
				{
					public void run()
					{
						try
						{

							Hashtable<String, String> result = grief ? db
									.getGrief(Integer.parseInt(args[0])) : db.getCheat(Integer.parseInt(args[0]));

							if (result != null)
							{
								GriefReport report = new GriefReport(
										result, instance);
								Location location = report.getLocation();
								SyncMethods.syncTeleport(player, location,
										instance);
								return;
							} else
							{
								player
										.sendMessage("There is no entry with " + (grief? "Grief" : "Cheat") + " ID '"
												+ args[0]
												+ "'. Did you mean /tp" + (grief ? "cheat?" : "grief?"));
								return;
							}
						} catch (NumberFormatException e)
						{
							// e.printStackTrace();
							SyncMethods
									.syncSendMessage(
											player,
											("There is no entry with " + (grief? "Grief" : "Cheat") + " ID '"
													+ args[0] + "' (IDs are a number)"),
											instance);
							return;
						}

					}
				});
	}
  
  	private void cmdDelEntry(final String[] args, final Player player, final Boolean grief) {
  		this.getServer().getScheduler().scheduleAsyncDelayedTask(this,
				new Runnable()
		{
			public void run()
			{

				try
				{
					// Attempt to delete grief record from the database
					Boolean result = grief ? db.delGrief(Integer.parseInt(args[0])) : db.delCheat(Integer.parseInt(args[0]));
					if (result == false)
					{
						SyncMethods.syncSendMessage(player, "There is no entry with "+ (grief? "Grief " : "Cheat ") + "ID " +"'"
								+ args[0] + "'.", instance);
					} else
					{
						SyncMethods.syncSendMessage(player,("Entry '" + args[0]
						                                    + "' successfully deleted"), instance);
					}
					return;
				} catch (NumberFormatException e)
				{
					// e.printStackTrace();
					SyncMethods.syncSendMessage(player,("There is no entry with " + (grief? "Grief " : "Cheat ") +  "ID " + " '" + args[0]
					                                                              + "' (IDs are a number)"), instance);
					return;
				}
			}
		});
  	}

  
	private void cmdShowGriefs(final String[] args, final Player player)
	{

		if (!GriefDetector.hasPermission(player, "griefdetector.viewgriefs"))
		{
			log.info("GCD: " + player.getName() + "attempted to '/sg' without permission");

			player.sendMessage("You don't have permission to use this command");
			return;
		}
		else {
			cmdShowEntries(args, player, true);
			return;
		}

	} //end cmdShowGriefs
	
	private void cmdTpGrief(final String[] args, final Player player)
	{
		if (!GriefDetector.hasPermission(player, "griefdetector.tpgrief"))
		{
			log.info("GCD: " + player + "attempted to '/tpGrief " + args
					+ "' without permission");

			player
					.sendMessage("You don't have permission to use this command.");
			return;
		}

		else
		{

			cmdTpEntry(args, player, true);
			return;

		}
	} // end cmdTpGrief
	
	
  
	private void cmdDelGrief(final String[] args, final Player player)
	{
		if (!GriefDetector.hasPermission(player, ("griefdetector.delgrief")))
		{
			log.info("GCD: " + player + "attempted to '/delGrief " + args
					+ "' without permission");

			player.sendMessage("You don't have permission to use this command");
			return;
		}
		else {
			cmdDelEntry(args, player, true);
			return;
		}

	} // end cmdDelGrief

  private void cmdShowCheats(String[] args, Player player)
  {
		if(!GriefDetector.hasPermission(player, "griefdetector.viewcheats")) {
			log.info("GCD: " + player + "attempted to '/sc' without permission");
			
			player.sendMessage("You don't have permission to use this command");
			return;
		}
		else {
			cmdShowEntries(args, player, false);
			return;
		}
  }

  private void cmdTpCheat(String[] args, Player player)
  {
		if(!GriefDetector.hasPermission(player, ("griefdetector.tpcheat"))) {
			log.info("GCD: " + player + "attempted to '/tpCheat " + args + "' without permission");
			
			player.sendMessage("you don't have permission to use this command");
			return;
		}
		else {
			cmdTpEntry(args, player, false);
			return;
		}
	  
      
  } //end cmdTpCheat
  
  private void cmdDelCheat(String[] args, Player player)
  {
		if(!GriefDetector.hasPermission(player, ("griefdetector.delcheat"))) {
			log.info("GCD: " + player + "attempted to '/delCheat " + args + "' without permission");
			
			player.sendMessage("You don't have permission to use this command");
			return;
		}
		else {
			cmdDelEntry(args, player, false);
			return;
		}
      
  } //end cmdDelCheat

public void disable()
{
	this.setEnabled(false);
	
}

/**
 * Checks if a player has the specified permission node.
 * 
 * @param permission
 *            Permission node to check
 * @return true if the player has permission
 */
public static boolean hasPermission(Player player, String permission) {
	boolean permissionPlugin = Bukkit.getServer().getPluginManager()
			.isPluginEnabled("Permissions");
	if (permissionPlugin) {
		if (GriefDetector.permissionHandler.has(player, permission)) {
			return true;
		}
	} else if (player.isOp() || player.hasPermission(permission)) {
		return true;
	}
	return false;
}

public Database getDb()
{
	  return db;
}


public void setDb(MySQLDatabase db)
{
	  this.db = db;
}


public int getREPORT_LIMIT()
{
	return REPORT_LIMIT;
}


public void setREPORT_LIMIT(int rEPORTLIMIT)
{
	REPORT_LIMIT = rEPORTLIMIT;
}


public ConcurrentHashMap<Player, PlayerActivity> getPlayerActivities()
{
	return players;
}



  
} // end class

// notes
/*
 * Admin login (for sake of ease, i'm calling users who receive the grief alerts	/
 * admins) receives message such as "There are X unresolved griefs" type			/
 * /showgriefs or /sg to view them which will then show a list from a txt (or		/
 * something) file, containing report messages (which includes the coords) -		/
 * coords could be saved separately i guess or even parsed from that file quite		/
 * easily i suppose... allow teleportation to grief sites in particular without		/
 * having to utilise "/tppos x y z" just "tpgrief griefID) allow admins delete		/
 * griefs/mark as resolved once they've "sorted out the grief" (eg checked and		/
 * perhaps rolled back etc) FOR USABILITY: Mark a grief as "Viewed"/"Pending",		
 * if one has been teleported to (some admins may "forget" to delete a current		
 * grief from the list even after they have actually resolved the grief so we		
 * don't want to spam admins logging in with griefs that have already been		
 * resolved, they will STILL BE THERE on the list when manually checked for, but	
 * it won't show up as a "new grief" when the admin logs in (simple int flag		
 * value 0 1 or 2 depending on its state, new, pending, resolved/deleted			
 * 
 * And such... :P and such and such
 */
