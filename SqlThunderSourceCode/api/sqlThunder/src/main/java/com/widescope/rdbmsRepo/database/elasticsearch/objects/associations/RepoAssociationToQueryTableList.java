package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RepoAssociationToQueryTableList implements RestInterface{

	private List<RepoAssociationToQueryTable> rList;
	public RepoAssociationToQueryTableList(List<RepoAssociationToQueryTable> rList) {
		this.setList(rList);
	}
	public void addRepoAssociationToQueryTable(RepoAssociationToQueryTable r) {
		this.rList.add(r);
	}
	public List<RepoAssociationToQueryTable> getrList() {
		return rList;
	}
	public void setList(List<RepoAssociationToQueryTable> rList) {
		this.rList = rList;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
