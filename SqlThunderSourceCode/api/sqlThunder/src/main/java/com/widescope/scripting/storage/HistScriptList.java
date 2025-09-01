package com.widescope.scripting.storage;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class HistScriptList implements RestInterface {

	private List<HistoryScript> historyScriptList;
	public void setScriptList(List<HistoryScript> historyScriptList) {
		this.historyScriptList = historyScriptList;
	}
	public List<HistoryScript> getScriptList() {
		return historyScriptList;
	}
	
	public HistScriptList(final List<HistoryScript> historyScriptList) {
		this.setScriptList(historyScriptList);
	}
	
	public HistScriptList() {
		this.setScriptList(new ArrayList<HistoryScript>());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
