package com.widescope.scripting;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.DateTimeUtils;

public class LogDetail implements RestInterface{

	public String type;  /*one the 6 types: stdin/stderr/exception/header/footer/count*/
	public String logLine;
	public long tStamp;
	
	public LogDetail(	final String type, 
						final String logLine) {
		this.type = type;
		this.logLine = logLine;
		this.tStamp = DateTimeUtils.millisecondsSinceEpoch();
		System.out.println(logLine);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
