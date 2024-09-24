package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class RepoAssociationTableList implements RestInterface {
	private List<RepoAssociationTable> rList;
	public RepoAssociationTableList(final List<RepoAssociationTable> rList) {
		this.setList(rList);
	}
	public void addRepoAssociationTable(final RepoAssociationTable r) {
		this.rList.add(r);
	}
	public List<RepoAssociationTable> getrList() {
		return rList;
	}
	public void setList(List<RepoAssociationTable> rList) {
		this.rList = rList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
