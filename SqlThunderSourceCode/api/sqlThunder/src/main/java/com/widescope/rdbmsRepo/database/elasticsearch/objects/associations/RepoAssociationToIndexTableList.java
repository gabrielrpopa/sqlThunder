package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import java.util.List;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class RepoAssociationToIndexTableList implements RestInterface{

	private List<RepoAssociationToIndexTable> rList;
	
	public RepoAssociationToIndexTableList(final List<RepoAssociationToIndexTable> rList) {
		this.setList(rList);
	}
	public List<RepoAssociationToIndexTable> getrList() {
		return rList;
	}
	public void setList(List<RepoAssociationToIndexTable> rList) {
		this.rList = rList;
	}
	public void addRepoAssociationToIndexTable(RepoAssociationToIndexTable r) {
		this.rList.add(r);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
