package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RepoIndexTableList implements RestInterface {

	private List<RepoIndexTable> rList;
	public RepoIndexTableList(List<RepoIndexTable> rList) {
		this.setList(rList);
	}
	public void addRepoIndexTable(RepoIndexTable rList) {
		this.rList.add(rList);
	}
	public List<RepoIndexTable> getrList() {
		return rList;
	}
	public void setList(List<RepoIndexTable> rList) {
		this.rList = rList;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
