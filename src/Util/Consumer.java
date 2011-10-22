package Util;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import me.koolsource.GriefDetector.GriefDetector;
import me.koolsource.GriefDetector.PlayerActivity;
import me.koolsource.GriefDetector.db.Database;


public class Consumer extends TimerTask

{
	private final Queue<ItemRatioRow> queue = new LinkedList<ItemRatioRow>();
	
	ConcurrentHashMap<Player, PlayerActivity> players;
	Database db;
	
	public Consumer(GriefDetector instance) {
		db = instance.getDb();
		players = instance.getPlayerActivities();
	}

	@Override
	public void run()
	{
		Enumeration<Player> keys = players.keys();

		while(keys.hasMoreElements()) {
			PlayerActivity activity;
			Player key = (Player) keys.nextElement();
			activity = players.get(key);
			
			// Get the break / place ratio values..
		}
		
	}

}
