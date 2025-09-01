package com.widescope.rdbmsRepo.database.tempSqlRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class HistSqlList implements RestInterface {

	private List<HistoryStatement> statementList;
	public List<HistoryStatement> getStatementList() {
		return statementList;
	}
	public void setStatementList(List<HistoryStatement> tempSqlList) {
		this.statementList = tempSqlList;
	}
	public static HistSqlList getTempSqlList(List<HistoryStatement> tempSqlList) {
		return new HistSqlList(tempSqlList);
	}

	public HistSqlList(final List<HistoryStatement> tempSqlList) {
		this.setStatementList(tempSqlList);
	}
	public HistSqlList() {
		this.setStatementList(new ArrayList<>());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
