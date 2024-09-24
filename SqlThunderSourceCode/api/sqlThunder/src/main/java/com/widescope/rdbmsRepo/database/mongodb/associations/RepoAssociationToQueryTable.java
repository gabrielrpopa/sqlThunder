package com.widescope.rdbmsRepo.database.mongodb.associations;

import com.google.gson.Gson;

public class RepoAssociationToQueryTable {
	private long associationToQueryId;
	private long associationId;
	private long queryId;


	
	public  RepoAssociationToQueryTable(final long associationToQueryId,
										final long associationId,
										final long queryId) {
		this.setAssociationToQueryId(associationToQueryId);
		this.setAssociationId(associationId);
		this.setQueryId(queryId);
	}

	public long getAssociationToQueryId() {
		return associationToQueryId;
	}
	public void setAssociationToQueryId(long associationToQueryId) {
		this.associationToQueryId = associationToQueryId;
	}
	public long getAssociationId() {
		return associationId;
	}
	public void setAssociationId(long associationId) {
		this.associationId = associationId;
	}
	public long getQueryId() {
		return queryId;
	}
	public void setQueryId(long queryId) {
		this.queryId = queryId;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
