package com.widescope.scripting.storage;

import java.util.List;

import com.google.gson.Gson;


public class HistoryScript {
	
	private long userId;
	private String scriptName;
	private String type;       			/*repo/adhoc*/
	private String interpreter;       /*python/julia/bash/etc*/
	private List<HistoryScriptVersion> histScriptList;
	
	
	public HistoryScript (	final long userId, 
							final String type,
							final String interpreter,
							final String scriptName,
							final List<HistoryScriptVersion> histScriptList
							) {
		this.setUserId(userId);
		this.setType(type);
		this.setInterpreter(interpreter);
		this.setScriptName(scriptName);
		this.setHistScriptList(histScriptList);

	}
	

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getInterpreter() { return interpreter; }
	public void setInterpreter(String interpreter) { this.interpreter = interpreter; }
	
	public String getScriptName() { return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }

	public List<HistoryScriptVersion> getHistScriptList() {	return histScriptList; }
	public void setHistScriptList(List<HistoryScriptVersion> histScriptList) { this.histScriptList = histScriptList; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
