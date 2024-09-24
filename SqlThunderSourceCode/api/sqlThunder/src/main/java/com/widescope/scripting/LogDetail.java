package com.widescope.scripting;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;

public class LogDetail implements RestInterface{

	public String type;  /*stdin/stderr/exception*/
	public String logLine;
	public long tStamp;
	
	public LogDetail(	final String type, 
						final String logLine) {
		this.type = type;
		this.logLine = logLine;
		this.tStamp = DateTimeUtils.millisecondsSinceEpoch();
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
