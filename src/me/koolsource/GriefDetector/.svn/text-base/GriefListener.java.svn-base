package me.koolsource.GriefDetector;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.koolsource.GriefDetector.db.Database;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class GriefListener extends BlockListener
{
	
	private GriefDetector instance;
	private Database db;
	private ConcurrentHashMap<Player, PlayerActivity> players;
	private Map config;

	public GriefListener(GriefDetector instance, Database db,
			ConcurrentHashMap<Player, PlayerActivity> players, Map config) {
		
		this.instance = instance;
		this.db = db;
		this.players = players;
		this.config = config;
		
		
		
		
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		
	}

}
