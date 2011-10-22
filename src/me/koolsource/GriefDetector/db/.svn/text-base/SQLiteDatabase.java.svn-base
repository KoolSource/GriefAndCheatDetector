package me.koolsource.GriefDetector.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import me.koolsource.GriefDetector.GriefDetector;
import me.koolsource.GriefDetector.limits.BreakLimit;

import org.bukkit.entity.Player;

public class SQLiteDatabase extends Database
{
	private Connection conn = null;
	private GriefDetector instance;
	
	public SQLiteDatabase(String path, GriefDetector instance) {
		try
		{
			this.instance = instance;
			
			Class.forName("org.sqlite.JDBC").newInstance();
		    DriverManager.setLoginTimeout(10); // If it takes 10 seconds, then
		    // it's a lost 'cause
		    conn = DriverManager.getConnection("jdbc:sqlite:" + path +  "/database.db");
		    
		    DatabaseMetaData dbm = conn.getMetaData();
		    // check if "griefs" table exists -> if it doesn't, then the other
		    // table won't exist either
		    ResultSet tables = dbm.getTables(null, null, "griefs", null);
		    
		    if (!tables.next())
			{
				// Table does not exist, create it!
				createTables();
			}
		    
		    tables.close();
		    
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void createTables()
	{
		String query;
		Statement stmt;

		// id,time,location,player,message
		try
		{
		    DatabaseMetaData dbm = conn.getMetaData();
		    ResultSet tables = dbm.getTables(null, null, "griefs", null);
		    
		    if (!tables.next())
			{
		    	query = "CREATE TABLE griefs "
					+ "(id INTEGER PRIMARY KEY,"
					+ "location varchar(100) NOT NULL, "
					+ "player varchar(20) NOT NULL, "
					+ "message varchar(500) NOT NULL, time DECIMAL(30,0) NOT NULL)";
					//+ "PRIMARY KEY (id))";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			}
		
			// Cheats
		    
		    tables = dbm.getTables(null, null, "cheats", null);
		    
		    if (!tables.next())
			{

			query = "CREATE TABLE cheats "
					+ "(id INTEGER PRIMARY KEY,"
					+ "location varchar(20) NOT NULL, "
					+ "player varchar(20) NOT NULL, "
					+ "message varchar(500) NOT NULL, time DECIMAL(30,0) NOT NULL)";
					//+ "PRIMARY KEY (id))";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			}
			
			// breaks
		    
		    tables = dbm.getTables(null, null, "breaks", null);
		    
		    if (!tables.next())
			{
			
			query = "CREATE TABLE breaks "
				+ "(id INTEGER PRIMARY KEY, " +
				   "player varchar(20) NOT NULL, "
				+ "itemid INT NOT NULL, "
				+ "threshold INT NOT NULL, "
				+ "overide BOOLEAN NOT NULL, " +
				  "broke INT NOT NULL, " +
				  "placed INT NOT NULL, " +
				  "lastreported DECIMAL(30,0))";
				//+ "PRIMARY KEY (id))";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void addCheat(String location, String player, String message,
			long when)
	{
		addEntry(location, player, message, when, "cheats");
		
	}

	@Override
	public void addGrief(String location, String player, String message,
			long when)
	{
		addEntry(location, player, message, when, "griefs");
		
	}

	private void addEntry(final String location, final String player, final String message,
			final long when, final String tableName)
	{
		instance.getServer().getScheduler().scheduleAsyncDelayedTask(instance,
				new Runnable() {
			public void run() {

				PreparedStatement insertgrief = null;
				String insertStatement = "INSERT INTO " + tableName + " (location, player, message, time) "
				+ "VALUES (?, ?, ?, ?)";

				try
				{
					insertgrief = conn.prepareStatement(insertStatement);
					insertgrief.setString(1, location);
					insertgrief.setString(2, player);
					insertgrief.setString(3, message);
					insertgrief.setLong(4, when);

					insertgrief.executeUpdate();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		
	}

	@Override
	public void addReport(String locationAsString, String playerName,
			String message, long timeAsLong, Player player)
	{
		addGrief(locationAsString, player.getName(), message, timeAsLong);
		
	}

	@Override
	public int countCheats()
	{
		return countRows("cheats");
	}

	private int countRows(String tableName)
	{
		int count=0;
		PreparedStatement countCheats = null;
		String countCheatsStatement = "SELECT COUNT(*) AS rowcount FROM " + tableName;
		try
		{
			countCheats = conn.prepareStatement(countCheatsStatement);
			ResultSet rs = countCheats.executeQuery();
			rs.next();
			count = rs.getInt("rowcount");
			rs.close();
			return count;
			
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}

	@Override
	public int countGriefs()
	{
		return countRows("griefs");
	}

	@Override
	public Boolean delCheat(int griefid)
	{
		return delEntry(griefid, "cheats");
	}

	private Boolean delEntry(int griefid, String tableName)
	{
		PreparedStatement delgrief = null;
		String deleteStatement = "DELETE FROM " + tableName + " WHERE id = ?";

		try
		{
			delgrief = conn.prepareStatement(deleteStatement);
			delgrief.setInt(1, griefid);
			int success = delgrief.executeUpdate();

			if (success == 1)
			{
				return true;
			} else
			{
				return false;
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public Boolean delGrief(int griefid)
	{
		return delEntry(griefid, "griefs");
	}

	@Override
	public Hashtable<String, String> getCheat(int cheatId)
	{
		return getEntry(cheatId, "cheats");
	}

	@Override
	public Hashtable<String, String>[] getCheats(int lowerlimit, int upperlimit)
	{
		return getEntries(lowerlimit, upperlimit, "cheats");
	}

	@Override
	public Hashtable<String, String> getGrief(int griefid)
	{
		return getEntry(griefid, "griefs");
	}

	private Hashtable<String, String> getEntry(int griefid, String tableName)
	{
		Hashtable<String, String> result = new Hashtable<String, String>();

		PreparedStatement getgrief = null;
		String getStatement = "SELECT id, location, player, message, time "
				+ "FROM " + tableName + " WHERE id = ? "; // Where the ID is grief ID

		try
		{
			getgrief = conn.prepareStatement(getStatement);
			getgrief.setInt(1, griefid);

			ResultSet rs = getgrief.executeQuery();

			if (rs.next())
			{
				result.put("id", new Integer(rs.getInt("id")).toString());
				result.put("location", rs.getString("location"));
				result.put("player", rs.getString("player"));
				result.put("message", rs.getString("message"));
				result.put("time", new Long(rs.getLong("time")).toString()); // convert
				// to
				// string

				return result;
			} else
			{
				return null;
			}

		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Hashtable<String, String>[] getGriefs(int lowerlimit, int upperlimit)
	{
		return getEntries(lowerlimit, upperlimit, "griefs");
	}

	private Hashtable<String, String>[] getEntries(int lowerlimit,
			int duration, String tableName)
	{
		Hashtable<String, String>[] results = new Hashtable[duration]; //TODO: establish this is correct
		for (int i = 0; i < results.length; i++)
		{
			results[i] = new Hashtable<String, String>();
		}

		PreparedStatement getgriefs = null;
		String getStatement = "SELECT id, location, player, message, time "
				+ "FROM " + tableName + " ORDER BY time DESC " + // Order by the order
				// reports were made
				// in
				"LIMIT ?, ?"; // Only the first 'limit' results

		try
		{
			getgriefs = conn.prepareStatement(getStatement);
			getgriefs.setInt(1, lowerlimit);
			getgriefs.setInt(2, duration);
			ResultSet rs = getgriefs.executeQuery();

			// Test
			int i = 0;
			while (rs.next())
			{
				results[i].put("id", new Integer(rs.getInt("id")).toString());
				results[i].put("location", rs.getString("location"));
				results[i].put("player", rs.getString("player"));
				results[i].put("message", rs.getString("message"));
				results[i].put("time", new Long(rs.getLong("time")).toString()); // convert
				// to
				// string
				i++;
			}

			// Determine if there were no results...
			if (results[0].get("location") == null)
			{
				return null;
			}

		} catch (SQLException e)
		{
			// TODO handle exception
			e.printStackTrace();
		}

		return results;
	}

	@Override
	public Hashtable<Integer, BreakLimit> getBreakEntry(int itemid, String playername)
	{
		Hashtable<Integer, BreakLimit> result = new Hashtable<Integer, BreakLimit>();
		
		// Check if it exists, if not, create a new entry, and return it...
		try {
			if (!checkBreakEntryExists(itemid, playername)) {
				return result;
			} else {
				PreparedStatement breakQuery = null;
				String query = "Select * FROM breaks WHERE itemid = ? and player = ?";
				breakQuery = conn.prepareStatement(query);
				breakQuery.setInt(1, itemid);
				breakQuery.setString(2, playername);
				
				ResultSet rs = breakQuery.executeQuery();
				
				while (rs.next()) {
					BreakLimit lim = new BreakLimit(rs.getInt("itemid"), rs
							.getInt("threshold"), rs.getBoolean("override"), rs
							.getInt("broke"), rs.getInt("placed"), rs
							.getLong("lastreported"));
					
					result.put(itemid, lim);
					return result;
				}
			}
		} catch (NullPointerException e) {
			// Error handling, y u so annoying? :(
			return null;
		} catch (SQLException e)
		{
			return null;
		}
		
		
		
		return null;
	}

	public void addBreakEntry(String playername, BreakLimit limit)
	{
		PreparedStatement addEntry = null;
		String query = "INSERT INTO breaks" + " (itemid, threshold, override, broke, placed, player) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		try
		{
			addEntry = conn.prepareStatement(query);
			addEntry.setInt(1, limit.getId());
			addEntry.setInt(2, limit.getThreshold());
			addEntry.setBoolean(3, limit.getOveride());
			addEntry.setInt(4, limit.getBroke());
			addEntry.setInt(5, limit.getPlaced());
			addEntry.setString(6, playername);
			
			addEntry.executeQuery();
		} catch (SQLException e)
		{
			// :( Y U NO WORKIE!
			e.printStackTrace();
		}
		
	}

	@Override
	public void updateBreakEntry(String playername, BreakLimit limit)
	{
		PreparedStatement updateBreakEntry = null;
		
		int itemid = limit.getId();
		int threshold = limit.getThreshold();
		Boolean override = limit.getOveride();
		int broke = limit.getBroke();
		int placed = limit.getPlaced();
		
		// Y U NO INTERNETS..
		// ^^ from when i had no internet, and couldn't google how to do an SQL update
		String updateQuery = "UPDATE breaks SET itemid = ?, threshold = ?, override = ?, broke = ?, placed = ?" +
				" WHERE player = ? and itemid = ?";
		
		try
		{
			updateBreakEntry = conn.prepareStatement(updateQuery);
			updateBreakEntry.setInt(1, limit.getId());
			updateBreakEntry.setInt(2, limit.getThreshold());
			updateBreakEntry.setBoolean(3, limit.getOveride());
			updateBreakEntry.setInt(4, limit.getBroke());
			updateBreakEntry.setInt(5, limit.getPlaced());
			updateBreakEntry.setString(6, playername);
			
			updateBreakEntry.executeQuery();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Boolean checkBreakEntryExists(int itemid, String player) {
		PreparedStatement checkEntry = null;
		String query = "SELECT itemid FROM breaks WHERE itemid = ? and player = ?";
		
		try
		{
			checkEntry = conn.prepareStatement(query);
			checkEntry.setInt(0, itemid);
			checkEntry.setString(1, player);
			ResultSet rs = checkEntry.executeQuery();
			
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
			
		} catch (SQLException e)
		{
			// aw for fuck sake, pahhlll...
			e.printStackTrace();
		}
		
		
		return null;
		
	}

}
