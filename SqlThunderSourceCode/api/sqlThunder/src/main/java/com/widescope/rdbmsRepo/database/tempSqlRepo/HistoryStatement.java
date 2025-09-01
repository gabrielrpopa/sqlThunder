package com.widescope.rdbmsRepo.database.tempSqlRepo;

import com.google.gson.Gson;


public class HistoryStatement {
	private long userId;
	private String statementName;
	private String content;
	private String comment;
	private long timeStamp;
	private String type;       /*repo/adhoc*/
	private String source;     /*es/rdbms/mongo*/
	
	public HistoryStatement (	final long userId, 
								final String shaHash
							) {
		
		this.setUserId(userId);
		this.setStatementName(shaHash);
		this.setContent(null);
		this.setComment(null);
		this.setTimeStamp(-1);
		this.setType(null);
		this.setSource(null);
	}
	
	
	public HistoryStatement (final long userId, 
							final String statementName,
							final String content,
							final String comment,
							final long timeStamp,
							final String type,
							final String source
							) {

		this.setUserId(userId);
		this.setStatementName(statementName);
		this.setContent(content);
		this.setComment(comment);
		this.setTimeStamp(timeStamp);
		this.setType(type);
		this.setSource(source);
	}
	
	
	public HistoryStatement () {
		this.setUserId(-1);
		this.setStatementName(null);
		this.setContent(null);
		this.setComment(null);
		this.setTimeStamp(-1);
		this.setType(null);
		this.setSource(null);
	}

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getStatementName() { return statementName; }
	public void setStatementName(String statementName) { this.statementName = statementName; }
	
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


	public static HistoryStatement toHistoryStatement(String str) {
		try	{
			Gson g = new Gson();
			return g.fromJson(str, HistoryStatement.class);
		}
		catch(Exception ex)	{
			return null;
		}
	}

	public static HistoryStatement fromAdhocStatement(AdhocStatement stm, String statementName) {
		HistoryStatement ret = new HistoryStatement();
		ret.setComment(stm.getComment());
		ret.setContent(stm.getStm());
		ret.setUserId(stm.getOriginalUserId());
		ret.setStatementName(statementName);
		ret.setTimeStamp(stm.getTimeStamp());
		ret.setType(stm.getType());
		ret.setSource(null);
		return ret;
	}

}
