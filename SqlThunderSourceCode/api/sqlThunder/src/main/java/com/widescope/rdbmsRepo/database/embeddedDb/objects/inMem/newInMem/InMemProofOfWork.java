package com.widescope.rdbmsRepo.database.embeddedDb.objects.inMem.newInMem;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class InMemProofOfWork implements RestInterface {
	private InMemSource inMemSource;
	private InMemClusterDest inMemClusterDest;
	private long timestamp;
	private String comment;
	
	public InMemProofOfWork(final long timestamp) {
		this.setInMemSource(new InMemSource() );
		this.setInMemClusterDest(new InMemClusterDest());
		this.setTimestamp(timestamp);
		this.setComment("");
	}

	public InMemSource getInMemSource() { return inMemSource; }
	public void setInMemSource(InMemSource inMemSource) { this.inMemSource = inMemSource; }

	public InMemClusterDest getInMemClusterDest() { return inMemClusterDest; }
	public void setInMemClusterDest(InMemClusterDest inMemClusterDest) { this.inMemClusterDest = inMemClusterDest; }

	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	



	
}
