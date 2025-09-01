package com.widescope.scripting.db;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class ScriptExecutedRecordList implements RestInterface  {
    private List<ScriptExecutedRecord> scriptExecutedRecordList;

    public List<ScriptExecutedRecord> getScriptExecutedRecordList() {	return scriptExecutedRecordList; }
    public void setScriptExecutedRecordList(List<ScriptExecutedRecord> scriptExecutedRecordList) { this.scriptExecutedRecordList = scriptExecutedRecordList; }
    public void addScriptExecutedRecord(ScriptExecutedRecord script) { this.scriptExecutedRecordList.add(script) ; }

    public ScriptExecutedRecordList(List<ScriptExecutedRecord> scriptExecutedRecordList)	{
        this.scriptExecutedRecordList = scriptExecutedRecordList;
    }

    public ScriptExecutedRecordList() {
        this.scriptExecutedRecordList = new ArrayList<ScriptExecutedRecord>();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
