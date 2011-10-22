package Util;

import me.koolsource.GriefDetector.GriefDetector;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SyncMethods
{
	public static void syncSendMessage(final Player player,
			final String message, GriefDetector instance)
	{
		instance.getServer().getScheduler().scheduleSyncDelayedTask(instance,
				new Runnable()
				{

					public void run()
					{
						player.sendMessage(message);
					}

				});

	}

	public static void syncTeleport(final Player player, final Location location, GriefDetector instance)
	{
		instance.getServer().getScheduler().scheduleSyncDelayedTask(instance,
				new Runnable()
				{

					public void run()
					{
						player.teleport(location);
					}
				

	});
		
	}
	
	

}