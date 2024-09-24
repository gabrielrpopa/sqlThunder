package com.widescope.rdbmsRepo.database.elasticsearch.objects.associations;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class RepoAssociationToIndexTable implements RestInterface{

	private long associationToIndexId;
	private long associationId;
	private long indexId;
	
	public RepoAssociationToIndexTable( final long associationToIndexId,
										final long associationId,
										final long indexId) {
		this.setAssociationToIndexId(associationToIndexId);
		this.setAssociationId(associationId);
		this.setIndexId(indexId);
	}

	public long getAssociationToIndexId() {
		return associationToIndexId;
	}
	public void setAssociationToIndexId(long associationToIndexId) {
		this.associationToIndexId = associationToIndexId;
	}
	public long getIndexId() {
		return indexId;
	}
	public void setIndexId(long indexId) {
		this.indexId = indexId;
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
