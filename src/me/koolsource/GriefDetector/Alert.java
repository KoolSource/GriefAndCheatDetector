/*
 * Sends alerts as in-game messages for detected cheats and griefs.
 */

package me.koolsource.GriefDetector;

import org.bukkit.entity.Player;

public class Alert
{
	private Player online[]; // Array containing online players

	public Alert(Player playersOnline[])
	{
		online = playersOnline;
	}

	public void alertCheat(String message, boolean fromChat)
	{
		for (Player player:online) // cycle through online players
		{
			if (GriefDetector.hasPermission(player, ("griefdetector.viewcheats"))) //if they can view cheat reports
			{
				if(fromChat) // if cheat text was caught from the chat, don't REPEAT it to the madmin
				{
					player.sendMessage("GCD detected a suspicious chat message - type /sc to view current cheat reports");
				}
				else // we don't mind putting the message in chat because it hasn't been shown in chat
				{
					player.sendMessage("GCD New Cheat: " + message + " Type /sc to see the list of unresolved cheat reports.");
				}
			}
		}
	}

	public void alertGrief(String message, boolean fromChat)
	{
		for (Player player:online) //cycle through online players
		{
			if (GriefDetector.hasPermission(player, ("griefdetector.viewgriefs"))) //if they can view grief reports
			{
				if(fromChat) // if grief text was caught from the chat, don't show it AGAIN to the madmin
				{
					player.sendMessage("GCD detected a grief-related chat message - type /sg to view current grief reports");
				}
				else // we don't mind putting the message in chat because it hasn't been in chat yet
				{
					player.sendMessage("GCD New Grief: " + message + " Type /sg to see the list of unresolved grief reports.");
				}
			}
		}
	}
}
