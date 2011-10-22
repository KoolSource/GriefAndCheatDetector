package me.koolsource.GriefDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.koolsource.GriefDetector.db.Database;
import me.koolsource.GriefDetector.db.MySQLDatabase;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import Util.SyncMethods;

public class GriefDetectorPlayerListener extends PlayerListener {
	Logger log = Logger.getLogger("chat");
	GriefDetector plugin;
	Scanner cluescanner;
	ArrayList<String> griefs;
	ArrayList<String> cheats;
	ConcurrentHashMap<Player, PlayerActivity> players;
	private Database db;

	long expiration;
	int cleanUpTimeEvery = 36000000;
	private Map config;
	
	public GriefDetectorPlayerListener(GriefDetector instance, Database db, ConcurrentHashMap<Player, PlayerActivity> players, Map config) {
		
		// How long to wait till someone leave the server to remove the hash table entry
		expiration = 60000;
		
		this.players = players;
		plugin = instance;
		griefs = new ArrayList<String>();
		cheats = new ArrayList<String>();
		this.db = db;
		this.config = config;

		// Load some sort of keyword list (for parsing of chat for clues)
		File file = new File("plugins" + File.separator + instance.getDescription().getName() + File.separator + "griefs.txt");
		
		loadFile(file, griefs);
		
		file = new File("plugins" + File.separator + instance.getDescription().getName() + File.separator + "cheats.txt");
		loadFile(file, cheats);
		
		//startRecurringCleanupTask();
	}

	private void startRecurringCleanupTask()
	{
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			public void run() {
				Enumeration<Player> keys = players.keys();

				while(keys.hasMoreElements()) {
					PlayerActivity activity;
					Player key = (Player) keys.nextElement();
					activity = players.get(key);

					// If they're not online, and they logged out (expiration) time ago, delete them from the hash table
					if (!activity.isOnline()) {
						if (activity.getLoggedout() > expiration) {
							players.remove(key);
						}
					}
				}
			}
		}, cleanUpTimeEvery, cleanUpTimeEvery);
	}

	/*
	 * Parses messages the players send for hints of planned griefing or
	 * reactions to griefing
	 */
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		
		String message = event.getMessage();
		String messageLower = message.toLowerCase();
		
		//Check if the message matches any phrases
		for (String grief : griefs) {
			if (messageLower.contains(grief)) {
				sendChatAlert("GRIEF", message, event);
				break;
			}
		}
		for (String cheat : cheats) {
			if (messageLower.contains(cheat)) {
				sendChatAlert("CHEAT", message, event);
				break;
			}
		}
	}
	
	/* Add and remove players from the hash table as they join and connect. 
	 * Note: still to decide if we want to remove them from the table. Will effectively reset timers
	 * But: have to assume plugin will run 24/7 so have to remove old playera at some point... 
	 * Could have a thread that clears it up every so often based on 'last seen' time */
	
	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		
		// If there is no hash table entry for this player, make one
		if (players.get(event.getPlayer()) == null)
		{
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
					new Runnable() {
				public void run() {
					players.put(event.getPlayer(), new PlayerActivity(
							event.getPlayer(), config));
					PlayerActivity activity = players.get(event.getPlayer());
					activity.setOnline(true);
				}
			}
			);
			
			
		
		}
	
		
		// If the player has the permission to view grief and/or cheat reports
		// and if there are grief and/or cheat reports, then alert them to the fact that there are
		// (these should be separate as they are different perms)
		if(GriefDetector.hasPermission(player, "griefdetector.viewgriefs")) {
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
					new Runnable() {
				public void run() {
					int griefCount = db.countGriefs();
					SyncMethods.syncSendMessage(player, ChatColor.YELLOW + "There are currently " + ChatColor.AQUA + griefCount + ChatColor.YELLOW + " unresolved grief reports (/sg)", plugin);
				}
			}
			);
		}
	
		if(GriefDetector.hasPermission(player, "griefdetector.viewcheats")) {
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
					new Runnable() {
				public void run() {
					int cheatCount = db.countCheats();
					SyncMethods.syncSendMessage(player, ChatColor.YELLOW + "There are currently " + ChatColor.AQUA + cheatCount +  ChatColor.YELLOW + " unresolved cheat reports (/sc)", plugin);
				}
			}
			);
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Set the time they logged out at, for use in deleting 'expired' users
		players.get(event.getPlayer()).setLoggedout(System.currentTimeMillis());
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		// Set the time they aheh... 'logged out' at, for use in deleting 'expired' users
		players.get(event.getPlayer()).setLoggedout(System.currentTimeMillis());
	}
	
	
	/* Check for players: 
	 * Picking up a suspicious amount of a valuable item in a short time period
	 * Picking up a suspicious amount oa trolly item in a short time period :P */ //oa trolly? wat?
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		long time = System.currentTimeMillis();
		String username = event.getPlayer().getDisplayName();
		int itemid = event.getItem().getEntityId();	
	}
	
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		BlockFace face = event.getBlockFace();
		
		players.get(event.getPlayer()).setLastInteracted(face);
	}
	
	public void loadFile(File file, ArrayList<String> array) {
		try {
			cluescanner = new Scanner(file);

			// Load chat clues into a arraylist data structure

			while (cluescanner.hasNext()) {
				String current = cluescanner.nextLine().toLowerCase();
				array.add(current);
				//log.info("added " + current + " to array");
			}

		} catch (FileNotFoundException e) {
			log.info("GCD: " + file + " not found");
		}
	}
	
	private void sendChatAlert(String type, String message, PlayerChatEvent event) {
		String name = event.getPlayer().getDisplayName();
		Location location = event.getPlayer().getLocation();
		
		String world = location.getWorld().getName();
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		
		log.info("GCD: " + "Offending message: " + message);
		//plugin.getServer().broadcastMessage("GD caught grief chat message: " + name + ": " + message);
		
		
		
		Alert chatAlert = new Alert(event.getPlayer().getServer().getOnlinePlayers());
		if(type.equals("CHEAT"))
		{
			db.addCheat(world + "," + x + "," + y + "," + z, name, "Caught text from chat: " + message, System.currentTimeMillis());
			chatAlert.alertCheat("X", true);
		}
			
		else if(type.equals("GRIEF"))
		{
			db.addGrief(world + "," + x + "," + y + "," + z, name, "Caught text from chat: " + message, System.currentTimeMillis());
			chatAlert.alertGrief("X", true);
		}
			
	}
}
