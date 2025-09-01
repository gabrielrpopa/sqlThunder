package com.widescope.rdbmsRepo.database.mongodb.objects;


import com.google.gson.Gson;

/**
 * Object built with informations found in the database. It works as on built-in metadata information
 * @author popa_
 *
 */
public class LargeMongoBinaryFileMetaRev {

	private String id;
	private int revision;
	private long timeStamp;
	private long size;
	
	public LargeMongoBinaryFileMetaRev(	final String id,
										final int revision,
										final long timeStamp,
										final long size) {
		this.setId(id);
		this.setRevision(revision);
		this.setTimeStamp(timeStamp);
		this.setSize(size);
		
	}

	public int getRevision() {	return revision; }
	public void setRevision(int revision) { this.revision = revision;}

	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	public String getId() {	return id; }
	public void setId(String id) { this.id = id; }

	public long getSize() { return size; }
	public void setSize(long size) { this.size = size; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
