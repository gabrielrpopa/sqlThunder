package com.widescope.rdbmsRepo.database.tempSqlRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class HistListShaHash implements RestInterface{

	private List<String> tempSqlList;
	public HistListShaHash(final List<String> tempSqlList) {
		this.setTempSqlList(tempSqlList);
	}
	public HistListShaHash() {
		this.setTempSqlList(new ArrayList<String>());
	}
	public List<String> getTempSqlList() {
		return tempSqlList;
	}
	public void setTempSqlList(List<String> tempSqlList) {
		this.tempSqlList = tempSqlList;
	}
	public static HistListShaHash getTempSqlList(List<String> tempSqlList) {
		return new HistListShaHash(tempSqlList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
