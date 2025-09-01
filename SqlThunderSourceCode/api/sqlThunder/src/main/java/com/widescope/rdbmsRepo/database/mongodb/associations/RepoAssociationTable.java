package com.widescope.rdbmsRepo.database.mongodb.associations;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class RepoAssociationTable implements RestInterface {

	private long associationId;
	private String associationName;
	
	public RepoAssociationTable(final long associationId, 
								final String associationName) {
		this.setAssociationId(associationId);
		this.setAssociationName(associationName);
	}

	public long getAssociationId() {
		return associationId;
	}
	public void setAssociationId(long associationId) {
		this.associationId = associationId;
	}
	public String getAssociationName() {
		return associationName;
	}
	public void setAssociationName(String associationName) {
		this.associationName = associationName;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
