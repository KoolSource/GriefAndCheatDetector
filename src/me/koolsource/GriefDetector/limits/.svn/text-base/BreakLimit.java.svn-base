package me.koolsource.GriefDetector.limits;

public class BreakLimit
{
	private int id;
	private int threshold;
	private Boolean overide;
	
	private int broke = 0;
	private int placed = 0;
	private long lastReported = 0;
	
	public BreakLimit(int id, int threshold, Boolean override, int broke, int placed, long lastReported) {
		this.id = id;
		this.threshold = threshold;
		this.overide = override;
		
		this.broke = broke;
		this.placed = placed;
		
		this.lastReported = lastReported;
	}
	
	public Boolean broke() {
		// If placed - broke && (currentTime - lastReported) > 15 minutes)
		//
		broke++;
		if (broke - placed > threshold && (System.currentTimeMillis() - lastReported > 90000)) {
			lastReported = System.currentTimeMillis();
			return true;
		} else if (broke == 1 && placed == 0 && overide) {
			return true;
		} else {
			return false;
		}	
	}
	
	public void placed() {
		placed++;
	}

	public int getBroke()
	{
		return broke;
	}

	public int getPlaced()
	{
		return placed;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getThreshold()
	{
		return threshold;
	}

	public void setThreshold(int threshold)
	{
		this.threshold = threshold;
	}

	public Boolean getOveride()
	{
		return overide;
	}

	public void setOveride(Boolean overide)
	{
		this.overide = overide;
	}

	public long getLastReported()
	{
		return lastReported;
	}

	public void setLastReported(long lastReported)
	{
		this.lastReported = lastReported;
	}

	public void setBroke(int broke)
	{
		this.broke = broke;
	}

	public void setPlaced(int placed)
	{
		this.placed = placed;
	}
	
	
	
	

}
