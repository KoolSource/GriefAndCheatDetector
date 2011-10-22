package me.koolsource.GriefDetector.db;

import java.sql.*;
import java.util.Hashtable;
import java.util.logging.Logger;
import me.koolsource.GriefDetector.GriefDetector;
import me.koolsource.GriefDetector.limits.BreakLimit;

import org.bukkit.entity.Player;
import Util.SyncMethods;


public class MySQLDatabase extends Database
{
	Connection conn = null;
	private GriefDetector instance;
	Logger log = Logger.getLogger("db");
	private String username;
	private String password;
	private String connStr;

	public MySQLDatabase(String hostname, String username, String password, String database, GriefDetector instance)
	{
		this.instance = instance;
		this.username = username;
		this.password = password;
		this.connStr = "jdbc:mysql://" + hostname + "/" + database;
		try
		{

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			DriverManager.setLoginTimeout(10); // If it takes 10 seconds, then it's a lost 'cause
			conn = DriverManager.getConnection(connStr, username, password);

			// Check if the tables exists... (if not, initialise tables)

			DatabaseMetaData dbm = conn.getMetaData();
			// check if "griefs" table exists -> if it doesn't, then the other
			// table won't exist either
			ResultSet tables = dbm.getTables(null, null, "griefs", null);
			if (!tables.next())
			{
				// Table does not exist, create it!
				createTables();
			}
			
			 // Set up a repeating task thread which will query the server every so often to stop the SQL connection timing out
			int id = instance.getServer().getScheduler()
					.scheduleAsyncRepeatingTask(instance, new Runnable()
					{

						public void run()
						{
							try
							{
								if (conn.isValid(20))
								{
									// If the connection is open: ping!
									// log.info("GCD: pinging database");
									ping();
								} else
								{
									log.info("GCD: Attempting reconnect via the keep-alive thread");
									if (!reconnect())
									{
										// If we weren't able to reconnect... Try again on next thread run
										log.info("GCD: Reconnect via the keep-alive thread unsuccessful. Retrying in 1 minute.");
									} else
									{
										log.info("GCD: successfully reconnected to database via keep-alive thread");
									}
								}
							} catch (SQLException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, 1200L, 1200L);

			tables.close();

		} catch (InstantiationException e)
		{

			e.printStackTrace();
			instance.disable();
		} catch (IllegalAccessException e)
		{

			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{

			e.printStackTrace();
			instance.disable();
		} catch (SQLException e)
		{
			log.info("Database credentials were incorrect. Disabling GCD");
			log.info(e.getMessage());
			//Aha! take that! (it wasn't visible from outside the class, y'see)
			instance.disable();
			
			
		} finally
		{

		}

	}

	public void createTables()
	{
		String query;
		Statement stmt;

		// id,time,location,player,message
		try
		{
			query = "CREATE TABLE griefs "
					+ "(id int NOT NULL AUTO_INCREMENT,"
					+ "location varchar(100) NOT NULL, "
					+ "player varchar(20) NOT NULL, "
					+ "message varchar(500) NOT NULL, time DECIMAL(30,0) NOT NULL,"
					+ "PRIMARY KEY (id))";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);

			// Cheats

			query = "CREATE TABLE cheats "
					+ "(id int NOT NULL AUTO_INCREMENT,"
					+ "location varchar(20) NOT NULL, "
					+ "player varchar(20) NOT NULL, "
					+ "message varchar(500) NOT NULL, time DECIMAL(30,0) NOT NULL,"
					+ "PRIMARY KEY (id))";
			stmt = conn.createStatement();
			stmt.executeUpdate(query);

			stmt.close();
			// conn.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addReport(final String locationAsString, String playerName,
			final String message, final long timeAsLong, final Player player)
	{
		instance.getServer().getScheduler().scheduleAsyncDelayedTask(instance,
				new Runnable() {
			public void run() {

				try
				{
					if (!conn.isValid(20)) {
						SyncMethods.syncSendMessage(player, "There was a problem connecting to the database, retrying.", instance);
						if (reconnect()) {
							SyncMethods.syncSendMessage(player, "Successfully reconnected, adding report.", instance);
						}
						else {
							SyncMethods.syncSendMessage(player, "Unable to reconnect to the database. Please contact an admin or try again later.", instance);
							return;
						}
					}

					addGrief(locationAsString, player.getName(), message, timeAsLong);
				} catch (SQLException e)
				{
					SyncMethods.syncSendMessage(player, "Unable to reconnect to the database. Please contact an admin or try again later.", instance);
					return;
				}

			}

		});

	}
	
	
	/**
	 * Returns an array of hash tables of all the griefs
	 * 
	 * (with field -> Value mappings i.e. Message -> "dsdfsdf"
	 * 
	 * Limit -> the number of results to retrieve
	 * 
	 * returns null if there are no griefs in the DB
	 */
	public Hashtable<String, String>[] getGriefs(int lowerlimit, int upperlimit)
	{
		
		
		return getEntries(lowerlimit, upperlimit, "griefs");
	}

	/**
	 * 'When' is the date/time formatted in System.currentTimeMillis which can
	 * be converted to a date later
	 */
	public void addGrief(String location, String player, String message,
			long when)
	{
		addEntry(location, player, message, when, "griefs");
	}

	/**
	 * Delete the grief with the given grief id Returns 'true' if successful.
	 * False probably means an invalid id was given
	 */
	public Boolean delGrief(int griefid)
	{
		return delEntry(griefid, "griefs");
	}

	/**
	 * Retrieve the grief with ID griefid
	 * 
	 * Returns null if grief ID is invalid (row doesn't exist)
	 */
	public Hashtable<String, String> getGrief(int griefid)
	{
		return getEntry(griefid, "griefs");
	}

	public Hashtable<String, String> getCheat(int cheatId)
	{
		return getEntry(cheatId, "cheats");
	}

	public Hashtable<String, String>[] getCheats(int lowerlimit, int upperlimit)
	{
		return getEntries(lowerlimit, upperlimit, "cheats");
	}

	public void addCheat(String location, String player, String message, long when)
	{
		addEntry(location, player, message, when, "cheats");
	};

	public Boolean delCheat(int griefid)
	{
		return delEntry(griefid, "cheats");
	}

	public int countCheats() {
		return countRows("cheats");
	}


	public int countGriefs()
	{
		return countRows("griefs");
	}
	
	private int countRows(String tableName) {
		
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

	private synchronized Hashtable<String, String>[] getEntries(int lowerlimit, int duration, String tableName)
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

	private synchronized Hashtable<String, String> getEntry(int griefid, String tableName)
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

	private synchronized Boolean delEntry(int griefid, String tableName)
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
	

	private synchronized void addEntry(final String location, final String player, final String message,
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

	private synchronized boolean reconnect()
	{
		int attempts = 0;
		try
		{
			while (attempts < 5 && conn.isClosed())
			{
				try
				{
					Thread.sleep(1000);
					conn = DriverManager.getConnection(connStr, username,
							password);
					attempts++;

					if (!conn.isClosed())
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
					attempts++;
					return false;
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					attempts++;
					e.printStackTrace();
				}

			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public synchronized void ping()
	{
		Statement stmt;
		String query = "/* ping */ SELECT 1";
		try
		{
			stmt = conn.createStatement();
			stmt.executeQuery(query);
			stmt.close();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Hashtable<Integer, BreakLimit> getBreakEntry(int itemid, String playername)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBreakEntry(String playername, BreakLimit limit)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBreakEntry(String playername, BreakLimit limit)
	{
		// TODO Auto-generated method stub
		
	}

}
