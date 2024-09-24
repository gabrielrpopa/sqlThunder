package com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class TableList implements RestInterface{

	private List<String> lst;
	public List<String> getLst() {
		return lst;
	}
	public void setLst(List<String> lst) {
		this.lst = lst;
	}
	
	public TableList(final List<String> lst) {
		this.setLst(lst);
	}
	
	public TableList() {
		this.setLst(new ArrayList<>());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
