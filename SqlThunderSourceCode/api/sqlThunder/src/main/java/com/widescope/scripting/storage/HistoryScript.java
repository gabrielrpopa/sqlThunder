package com.widescope.scripting.storage;

import java.util.List;

import com.google.gson.Gson;


public class HistoryScript {
	
	private String user;
	private String scriptName;
	private String scriptId;
	private String type;       			/*repo/adhoc*/
	private String interpreter;       /*python/julia/bash/etc*/
	private List<HistoryScriptVersion> histScriptList;
	
	
	public HistoryScript (final String user,
						  final String type,
						  final String interpreter,
						  final String scriptName,
						  final long scriptId,
						  final List<HistoryScriptVersion> histScriptList) {
		this.setUserId(user);
		this.setType(type);
		this.setInterpreter(interpreter);
		this.setScriptName(scriptName);
		this.setHistScriptList(histScriptList);

	}
	

	public String getUserId() { return user; }
	public void setUserId(String userId) { this.user = user; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getInterpreter() { return interpreter; }
	public void setInterpreter(String interpreter) { this.interpreter = interpreter; }
	
	public String getScriptName() { return scriptName; }
	public void setScriptName(String scriptName) { this.scriptName = scriptName; }

	public String getScriptId() { return scriptId; }
	public void setScriptId(String scriptId) { this.scriptId = scriptId; }

	public List<HistoryScriptVersion> getHistScriptList() {	return histScriptList; }
	public void setHistScriptList(List<HistoryScriptVersion> histScriptList) { this.histScriptList = histScriptList; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}



}
