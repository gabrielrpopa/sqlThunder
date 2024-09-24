package com.widescope.rdbmsRepo.database.mongodb.associations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class RepoAssociationToCollectionTableList implements RestInterface{

	private List<RepoAssociationToCollectionTable> lst;
	public List<RepoAssociationToCollectionTable> getList() {
		return lst;
	}
	public void setList(List<RepoAssociationToCollectionTable> rList) {
		this.lst = rList;
	}
	public void addRepoAssociationToCollectionTable(RepoAssociationToCollectionTable r) {
		this.lst.add(r);
	}

	public RepoAssociationToCollectionTableList(final List<RepoAssociationToCollectionTable> rList) {
		this.setList(rList);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
