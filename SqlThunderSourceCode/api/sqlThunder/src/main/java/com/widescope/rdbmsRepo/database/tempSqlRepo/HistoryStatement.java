package com.widescope.rdbmsRepo.database.tempSqlRepo;

import com.google.gson.Gson;


public class HistoryStatement {
	private long userId;
	private String shaHash;
	private String content;
	private String comment;
	private long timeStamp;
	private String type;       /*repo/adhoc*/
	private String source;     /*es/rdbms/mongo*/
	
	public HistoryStatement (	final long userId, 
								final String shaHash
							) {
		
		this.setUserId(userId);
		this.setShaHash(shaHash);
		this.setContent(null);
		this.setComment(null);
		this.setTimeStamp(-1);
		this.setType(null);
		this.setSource(null);
	}
	
	
	public HistoryStatement (final long userId, 
							final String shaHash,
							final String content,
							final String comment,
							final long timeStamp,
							final String type,
							final String source
							) {

		this.setUserId(userId);
		this.setShaHash(shaHash);
		this.setContent(content);
		this.setComment(comment);
		this.setTimeStamp(timeStamp);
		this.setType(type);
		this.setSource(source);
	}
	
	
	public HistoryStatement () {
		this.setUserId(-1);
		this.setShaHash(null);
		this.setContent(null);
		this.setComment(null);
		this.setTimeStamp(-1);
		this.setType(null);
		this.setSource(null);
	}

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getShaHash() { return shaHash; }
	public void setShaHash(String shaHash) { this.shaHash = shaHash; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	public String getSource() {	return source; }
	public void setSource(String source) { this.source = source; }
	

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
