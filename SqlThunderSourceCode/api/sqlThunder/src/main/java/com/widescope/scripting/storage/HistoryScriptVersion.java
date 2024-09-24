package com.widescope.scripting.storage;


import com.google.gson.Gson;

public class HistoryScriptVersion {
	private String shaHash;
	private String content;
	private String comment;
	private long timeStamp;
	public HistoryScriptVersion (	final String shaHash, 
									final String content,
									final String comment,
									final long timeStamp
									) {
		this.setShaHash(shaHash);
		this.setContent(content);
		this.setComment(comment);
		this.setTimeStamp(timeStamp);
	}


	public String getShaHash() { return shaHash; }
	public void setShaHash(String shaHash) { this.shaHash = shaHash; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
