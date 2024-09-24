package com.widescope.scripting.storage;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class HistScriptListShaHash implements RestInterface{

	private List<String> tempSqlList;
	public HistScriptListShaHash(final List<String> tempSqlList) {
		this.setTempSqlList(tempSqlList);
	}
	public HistScriptListShaHash() {
		this.setTempSqlList(new ArrayList<String>());
	}
	public List<String> getTempSqlList() {
		return tempSqlList;
	}
	public void setTempSqlList(List<String> tempSqlList) {
		this.tempSqlList = tempSqlList;
	}
	
	public static HistScriptListShaHash getTempSqlList(List<String> tempSqlList) {
		return new HistScriptListShaHash(tempSqlList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
