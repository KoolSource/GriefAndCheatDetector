package me.koolsource.GriefDetector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.koolsource.GriefDetector.db.Database;
import me.koolsource.GriefDetector.db.MySQLDatabase;
import me.koolsource.GriefDetector.limits.MineLimit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MineListener extends BlockListener {
	Logger log = Logger.getLogger("mine");
	GriefDetector plugin;
	private ConcurrentHashMap<Player, PlayerActivity> players;
	private Database db;
	private Map config;
	private ArrayList<String> worldsExemptFromMining;
	private String txtChestPlace;
	private boolean doChestReminder=true;
	@SuppressWarnings("unchecked")
	public MineListener(GriefDetector instance, Database db,
			ConcurrentHashMap<Player, PlayerActivity> players, Map config) {
		plugin = instance;
		this.players = players;
		this.db = db;
		this.config = config;
		this.worldsExemptFromMining = (ArrayList<String>) config.get("MINE_DETECTION_EXEMPT_WORLDS");
		this.txtChestPlace = config.get("CHEST_PLACED_MESSAGE");
		
		if (worldsExemptFromMining == null) {
			worldsExemptFromMining = new ArrayList<String>();
		}
		
		if (txtChestPlace == null) // if the config option isn't set or is null
		{
			doChestReminder = false;  // disable the notification message on placing chests
		}
	}

/*	@Override
	public void onBlockBurn(BlockBurnEvent event) {

	}*/

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {

		int blockID = event.getBlock().getTypeId();
		Player player = event.getPlayer();
		PlayerActivity activity = players.get(player);

		// If the hash table doesn't have an entry for this player (for some reason), add them.
		if (activity == null)
		{
			players.put(player, new PlayerActivity(player, config));
			activity = players.get(player);
		}
		
		String playername = player.getDisplayName();
		//Location playerLocation = event.getPlayer().getLocation();
		Location blockLocation = event.getBlock().getLocation(); 
		String itemName = event.getBlock().getType().name().toString();
		
		// TODO: Loop through each of the logged block types in the config
		//for (blockID:stuffandthings)
		//{
		//  TODO: Get current placed amount from db
		//	thisParticularBlocksCountUsingAboveVariableName++
		// //note: the comparison will be done on block break or some other interval, not on block place
		//	putMeBackInDataBasePl0x; //save the variable in the base of data - now!  Not later at some interval, now!
		//}
		
		
		//if(blockID == 20) //if its glass
		//{
			// TODO: add to specific players' counter of how much glass they've placed
			// then will compare against glass broken at certain intervals in order to issue a report if
			// they've broken more glass than they've placed
			
		//}		
		// If it's TNT...
		//if (blockID == 46)
		//{
			// Add to tnt count placed, count tnt in area
			// Client side crashes only, server side crashes extremely rare (ie using a mod to place it, but at such numbers, its likely the placement would crash it before the explosion got a chance to)
			// *removed brads inane banter from comments*, except: "this isn't http://stackoverflow.com/questions/184618/what-is-the-best-comment-in-source-code-you-have-ever-encountered/184854#184854 !"
		//}
		
		// if the player places a chest
		if (blockID == 54)
		{
			if (doChestReminder) //if the config option is set to remind people to lock their chest when they place a chest
			{
				player.sendMessage(txtChestPlace); //send them this message
				//player.sendMessage("GCD: " + txtChestPlace); //Fully customizable, as its probable newbies will get this message, don't prefix with GCD (its a to:madmin prefix anyway) (Yes this might be a little out of the scope of GCD but its in an effort to thwart stealing)
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		PlayerActivity activity = players.get(player);

		// If the hash table doesn't have an entry for this player (for some reason), add them.
		if (activity == null)
		{
			players.put(player, new PlayerActivity(player, config));
			activity = players.get(player);
		}
		
		if(!worldsExemptFromMining.contains(event.getPlayer().getWorld().getName())) {
			checkMinedinDark(activity, player, event);
			checkMine(activity, player, event);
			checkRatio(activity, player, event); //check placed vs broken
		}

	}

	private void checkMine(PlayerActivity activity, Player player, BlockBreakEvent event)
	{
		int blockID = event.getBlock().getTypeId();
		
		// If we have registered a block ID to listen for amount mined..
		if (activity.hasRegisteredMineLimit(blockID))
		{
			MineLimit limit = activity.mined(blockID);
			
			// If this returns null, it didn't exceed the limits
			if (limit == null) {
				return;
			}
			
			String playername = player.getName();
			String itemName = event.getBlock().getType().name().toString();	
			Location location = player.getLocation();
			String fastMineMessage = playername + " is suspected of cheating, they mined " + limit.getLIMIT() + " " + itemName + " in " + limit.getTimeElapsed()/60000 + " minutes.";
			db.addCheat(location.getWorld().getName() + "," + (int) location.getX() + "," + (int) location.getY() +"," + (int) location.getZ(), playername, fastMineMessage, System.currentTimeMillis());
			log.info("GCD: " + fastMineMessage);
			
			Alert fastMineAlert = new Alert(event.getPlayer().getServer().getOnlinePlayers());
			fastMineAlert.alertCheat(fastMineMessage, false);
		}
		
	}

	private void checkMinedinDark(PlayerActivity activity, Player player, BlockBreakEvent event )
	{
		int blockID = event.getBlock().getTypeId();
		if (activity.hasRegisteredMineInDarkLimit(blockID))
		{
			byte level = event.getBlock().getRelative(activity.getLastInteracted()).getLightLevel();
			if (level == 0)
			{
				MineLimit limit = activity.minedInDark(blockID);
				
				// If this returns null, it didn't exceed the limits
				if (limit == null) {
					return;
				}
				
				String itemName = event.getBlock().getType().name().toString();	
				Location location = player.getLocation();
				
				String darkMineMessage = player.getDisplayName() + " is mining in the dark, they mined " + limit.getLIMIT() + " " + itemName + " in the past " + limit.getTimeElapsed()/60000 + " minutes with no lighting.";
				db.addCheat(location.getWorld().getName() + "," + (int) location.getX() + "," + (int) location.getY() +"," + (int) location.getZ(), player.getName(), darkMineMessage, System.currentTimeMillis());
				log.info("GCD: " + darkMineMessage);
				
				Alert darkMineAlert = new Alert(event.getPlayer().getServer().getOnlinePlayers());
				darkMineAlert.alertCheat(darkMineMessage, false);
			}
		}

		
	}
	
	private void checkRatio(PlayerActivity activity, Player player, BlockBreakEvent event)
	{
		int blockID = event.getBlock().getTypeId();
		
	}
	
	
	// if (blockID == 20) //if its glass
	// {
		//TODO: work out if its theirs!?!? we get the coords of this broken block and go through the table comparing against the glass blocks THAT player placed
		// if not, then alert,
		// - original idea was to alert if glass mined > glass placed. - probably still do that TOO...
		// .... entire new feature, if someone breaks someting that isn't there's? but that could get out of hand definitely, with friends
		// working together, so sticking to glass and glass placed/destroyed on a personal basis seems .. well, better overall? they both have their pros and cons
		// false negatives are to be expected in this type of plugin so i think both should be implemented.
	// }

}
