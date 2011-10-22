package me.koolsource.GriefDetector.limits;

import java.util.logging.Logger;

public class MineLimit {
	private int LIMIT;
	private long TIME_FRAME;
	
	private int id;
	private int amount;
	private long started_recording;

	Logger log = Logger.getLogger("mineLimit");
	private long logged_at;
	
	
	/** 
	 * limit = The amount that can be mined in the time frame without alert happening
	 * time_frame = when the counts are reset*/
	public MineLimit(int id, int limit, long time_frame) {
		this.id = id;
		this.LIMIT = limit;
		this.TIME_FRAME = time_frame;
		
		// initialise
		this.amount = 0;
		this.started_recording = 0;
	}
	
	
	/** Returns true if they exceed the mine limit*/
	public Boolean justMined() {
		
		if (started_recording == 0) {
			started_recording = System.currentTimeMillis();
			
			
			//log.info("GCD: " + "initialised some ore count");
			return mined();
		}
		else {
			long currenttime = System.currentTimeMillis();
			
			// If it's been 30 minutes since we started the timer for this user
			if (currenttime - started_recording >= TIME_FRAME) {
				//log.info("GCD: " + "Resetting item count");
				
				// Reset the timer and count
				started_recording = 0;
				amount = 0;
				
				return false;
			} 
			else {
				
				return mined();
								
				//log.info("GCD: " + "got to end of mined method");
				
			}
		}
		
	}
	
	/** Increments the counter, and returns true if over limits... */
	private Boolean mined() {
		amount += 1;
		//log.info("GCD: " + "Increasing item count: " + amount);
		
		if (amount >= LIMIT) {
			// Reset the timer and count, so it doesn't spam and continues detecting
			amount = 0;
			logged_at = started_recording;
			started_recording = 0;
			return true;
		}
		else {
			return false;
		}
	}


	public long getTIME_FRAME()
	{
		return TIME_FRAME / 60000; // Time in minutes
	}


	public void setTIME_FRAME(long tIMEFRAME)
	{
		TIME_FRAME = tIMEFRAME;
	}


	public int getLIMIT()
	{
		return LIMIT;
	}


	public void setLIMIT(int lIMIT)
	{
		LIMIT = lIMIT;
	}
	
	public long getTimeElapsed() {
		return System.currentTimeMillis() - logged_at;
	}
	
}
