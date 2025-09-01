package com.widescope.rdbmsRepo.database.mongodb.response;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class StringList implements RestInterface{
	private List<String> resultSet;
	
	public StringList(final List<String> resultSet) {
		this.setResultSet(resultSet);
	}
	public StringList() {
		this.setResultSet(new ArrayList<String>());
	}
	public List<String> getResultSet() {
		return resultSet;
	}
	public void setResultSet(List<String> resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
