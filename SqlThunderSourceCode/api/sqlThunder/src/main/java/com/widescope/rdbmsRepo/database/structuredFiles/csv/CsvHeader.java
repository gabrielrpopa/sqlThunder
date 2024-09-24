package com.widescope.rdbmsRepo.database.structuredFiles.csv;

import com.google.gson.Gson;


public class CsvHeader {
	private String name;
	private String type;
	private boolean isNumber;
	private long isNumberCount;
	private long totalCount; 
	
	
	public CsvHeader(	final String name,
						final String type,
						final boolean isNumber,
						final long isNumberCount,
						final long totalCount
						) {
		
		this.setName(name);
		this.setType(type);
		this.setNumber(isNumber);
		this.setIsNumberCount(isNumberCount);
		this.setTotalCount(totalCount);
		
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public boolean isNumber() {
		return isNumber;
	}
	public void setNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}
	public long getIsNumberCount() {
		return isNumberCount;
	}
	public void setIsNumberCount(long isNumberCount) {
		this.isNumberCount = isNumberCount;
	}
	public void incrementIsNumberCount() {
		this.isNumberCount++;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public void incrementTotalCount() {
		this.totalCount++;
	}
	
}