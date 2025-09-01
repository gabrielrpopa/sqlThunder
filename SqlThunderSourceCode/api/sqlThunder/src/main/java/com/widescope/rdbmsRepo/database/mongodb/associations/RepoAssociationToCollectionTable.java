package com.widescope.rdbmsRepo.database.mongodb.associations;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class RepoAssociationToCollectionTable implements RestInterface{

	private long associationToCollectionId;
	private long associationId;
	private long collectionId;
	
	public RepoAssociationToCollectionTable(final long associationToCollectionId,
											final long associationId,
											final long collectionId) {
		this.setAssociationToCollectionId(associationToCollectionId);
		this.setAssociationId(associationId);
		this.setCollectionId(collectionId);
	}

	public long getAssociationToCollectionId() {
		return associationToCollectionId;
	}

	public void setAssociationToCollectionId(long associationToCollectionId) {
		this.associationToCollectionId = associationToCollectionId;
	}

	public long getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(long collectionId) {
		this.collectionId = collectionId;
	}
	public long getAssociationId() {
		return associationId;
	}
	public void setAssociationId(long associationId) {
		this.associationId = associationId;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
