package com.widescope.scripting;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.utils.DateTimeUtils;


public class ScriptingSharedLogObject {
	
	private String count;
	private String type;
	private long timeStart;
	private long timeLastUpdated;
	private boolean isStart;
	private List<String> poolData;
	private boolean isEnd;
	
	public ScriptingSharedLogObject() {
		this.setType("Ad-hoc");
		this.setTimeStart(DateTimeUtils.millisecondsSinceEpoch());
		this.setTimeLastUpdated(timeStart);
		this.setStart("Ad-hoc");
		this.poolData = new ArrayList<>();
		
	}


	public String getType() { return type; }
	public void setType(String type) { 
		this.setTimeStart(DateTimeUtils.millisecondsSinceEpoch());
		this.setTimeLastUpdated(timeStart);
		this.type = type; 
	}
	
	
	public List<String> getPoolData() {
		return poolData;
	}


	public void setPoolData(List<String> poolData) {
		this.poolData = poolData;
		this.setTimeLastUpdated(DateTimeUtils.millisecondsSinceEpoch());
	}
	
		
	public void addLogsToPool(List<String> poolLogs) { 
		this.poolData.addAll(poolLogs);
		this.setTimeLastUpdated(DateTimeUtils.millisecondsSinceEpoch());
	}
	
	public void addLogToPool(String line) {
		this.poolData.add(line); 
		this.setTimeLastUpdated(DateTimeUtils.millisecondsSinceEpoch());
	}

	public boolean isStart() { return isStart; }
	public void setStart(String type) {
		this.isStart = true; 
		this.type = type; 
		this.isEnd = false;
		this.setTimeStart(DateTimeUtils.millisecondsSinceEpoch());
		this.setTimeLastUpdated(timeStart);
	}

	public boolean isEnd() { return isEnd; }
	public void setEnd() { this.isEnd = true; }
	public void setEnd(String count) { this.isEnd = true; this.setCount(count);}
	public void setEnd(boolean isEnd) { this.isEnd = isEnd; }
	
	public long getTimeLastUpdated() {	return timeLastUpdated; }
	public void setTimeLastUpdated(long timeLastUpdated) { this.timeLastUpdated = timeLastUpdated; }
	
	public long getTimeStart() { return timeStart; }
	public void setTimeStart(long timeStart) { this.timeStart = timeStart; }
	
	public String getCount() { return count; }
	public void setCount(String count) { this.count = count; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
