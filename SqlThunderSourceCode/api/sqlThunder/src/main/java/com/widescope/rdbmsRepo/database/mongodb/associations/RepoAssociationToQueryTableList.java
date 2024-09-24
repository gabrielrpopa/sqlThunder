package com.widescope.rdbmsRepo.database.mongodb.associations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RepoAssociationToQueryTableList implements RestInterface{

	private List<RepoAssociationToQueryTable> rList;
	public List<RepoAssociationToQueryTable> getrList() {
		return rList;
	}
	public void setrList(List<RepoAssociationToQueryTable> rList) {
		this.rList = rList;
	}
	public void addRepoAssociationToQueryTable(RepoAssociationToQueryTable r) {
		this.rList.add(r);
	}

	public RepoAssociationToQueryTableList(List<RepoAssociationToQueryTable> rList) {
		this.setrList(rList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
