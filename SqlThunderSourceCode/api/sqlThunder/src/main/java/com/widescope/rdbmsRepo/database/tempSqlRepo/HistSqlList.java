package com.widescope.rdbmsRepo.database.tempSqlRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class HistSqlList implements RestInterface {

	private List<HistoryStatement> tempSqlList;
	public HistSqlList(final List<HistoryStatement> tempSqlList) {
		this.setTempSqlList(tempSqlList);
	}
	public HistSqlList() {
		this.setTempSqlList(new ArrayList<HistoryStatement>());
	}
	public List<HistoryStatement> getTempSqlList() {
		return tempSqlList;
	}
	public void setTempSqlList(List<HistoryStatement> tempSqlList) {
		this.tempSqlList = tempSqlList;
	}
	public static HistSqlList getTempSqlList(List<HistoryStatement> tempSqlList) {
		return new HistSqlList(tempSqlList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
