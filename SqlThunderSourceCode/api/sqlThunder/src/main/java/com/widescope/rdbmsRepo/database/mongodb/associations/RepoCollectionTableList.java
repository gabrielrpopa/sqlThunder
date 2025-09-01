package com.widescope.rdbmsRepo.database.mongodb.associations;

import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class RepoCollectionTableList implements RestInterface {

	private List<RepoCollectionTable> rList;
	public RepoCollectionTableList(List<RepoCollectionTable> rList) {
		this.setrList(rList);
	}

	public List<RepoCollectionTable> getrList() {
		return rList;
	}
	public void setrList(List<RepoCollectionTable> rList) {
		this.rList = rList;
	}
	public void addRepoCollectionTable(RepoCollectionTable r) {
		this.rList.add(r);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
