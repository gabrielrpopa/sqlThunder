package com.widescope.scripting;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;


public class ScriptingReturnObject implements RestInterface {
	private ScriptingSharedDataObject data;
	private List<LogDetail> logDetailList;
	private String isStreaming;
	private String requestId;
	private long startTime = 0;
	private long endTime = 0;
	private String isCompleted;
	private String isError;

	public ScriptingReturnObject(String requestId) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted("N");
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError("N");
		this.setIsStreaming("N");
	}


	public ScriptingReturnObject(String requestId, String isStreaming) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted("N");
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError("N");
	}

	public ScriptingReturnObject(String requestId, LogDetail logDetail) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.addLogDetail(logDetail);
		this.setIsStreaming("N");
		this.setRequestId(requestId);
		this.setIsCompleted("N");
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError("N");
	}

	public ScriptingReturnObject(String requestId, String isStreaming, LogDetail logDetail) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.addLogDetail(logDetail);
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted("N");
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError("N");
	}

	public ScriptingReturnObject(String requestId, String isStreaming, String isCompleted, LogDetail logDetail) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.addLogDetail(logDetail);
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted(isCompleted);
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError("N");
	}

	public ScriptingReturnObject(String requestId, String isStreaming, String isCompleted, String isError, LogDetail logDetail) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.addLogDetail(logDetail);
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted(isCompleted);
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError(isError);
	}



	public ScriptingReturnObject(String requestId, String isStreaming, String isError) {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
		this.setIsStreaming(isStreaming);
		this.setRequestId(requestId);
		this.setIsCompleted("N");
		this.setStartTime(DateTimeUtils.millisecondsSinceEpoch());
		this.setIsError(isError);
	}

	public void clearData() {
		this.data = new ScriptingSharedDataObject();
		this.setLogDetailList(new ArrayList<>());
	}


	public String getCountLogLinesStr() {
		return String.valueOf(this.logDetailList.size());
	}

	synchronized private void concatScriptingReturnObjectSync(final ScriptingReturnObject result) {
		this.setIsCompleted(result.getIsCompleted());
		if(this.startTime == 0)
			this.setStartTime(result.getStartTime());
		this.endTime = DateTimeUtils.millisecondsSinceEpoch();
		this.setIsStreaming(result.isStreaming);

		if(this.getRequestId() == null)
			this.setRequestId(result.getRequestId());

		if(this.getIsCompleted() == null)
			this.setIsCompleted(result.getIsCompleted());

		if(this.getIsError().compareToIgnoreCase("N") == 0)
			this.setIsCompleted(result.getIsError());

	}


	 public void concatScriptingReturnObject(final ScriptingReturnObject result) {
		this.concatScriptingReturnObjectSync(result);
		this.data.createIfNotPresent(result.getScriptingSharedDataObject());
		this.logDetailList.addAll(result.getLogDetailList());
	}

	public ScriptingSharedDataObject getScriptingSharedDataObject() {
		return data;
	}
	public void setScriptingSharedDataObject(ScriptingSharedDataObject s) { this.data = s; }

	public void putIfAbsent(ScriptingSharedDataObject s) {
		this.data.createIfNotPresent(s);
	}

	public List<LogDetail> getLogDetailList() {
		return logDetailList;
	}
	public void setLogDetailList(List<LogDetail> logDetailList) {
		this.logDetailList = logDetailList;
	}
	public void addLogDetail(LogDetail logDetail) {
		this.logDetailList.add(logDetail);
	}

	public void addLogDetails(List<LogDetail> logDetails) {
		this.logDetailList.addAll(logDetails);
	}

	public void addLogDetailWithClean(LogDetail logDetail) {
		this.clearData();
		this.logDetailList.add(logDetail);
	}

	public void addLastLogDetailWithClean(LogDetail logDetail) {
		this.clearData();
		this.logDetailList.add(logDetail);
		this.setIsCompleted("Y");
		this.setEndTime(DateTimeUtils.millisecondsSinceEpoch());
	}

	public String getIsStreaming() {
		return isStreaming;
	}
	public void setIsStreaming(String isStreaming) {
		this.isStreaming = isStreaming;
	}

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getIsCompleted() {
		return isCompleted;
	}
	public void setIsCompleted(String isCompleted) {
		this.isCompleted = isCompleted;
	}

	public long getStartTime() { return startTime; }
	public void setStartTime(long startTime) { this.startTime = startTime; }

	public long getEndTime() { return endTime; }
	public void setEndTime(long endTime) { this.endTime = endTime; }

	public String getIsError() { return isError; }
	public void setIsError(String isError) { this.isError = isError; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}

	public static ScriptingReturnObject toScriptingReturnObject(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ScriptingReturnObject.class);
		} catch(JsonSyntaxException ex) {
			return null;
		}
	}
}
