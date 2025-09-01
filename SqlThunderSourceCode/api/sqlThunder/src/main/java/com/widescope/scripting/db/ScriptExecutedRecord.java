package com.widescope.scripting.db;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.ExecutedStatement;
import com.widescope.rdbmsRepo.database.elasticsearch.repo.ElasticExecutedQuery;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.rest.RestInterface;

public class ScriptExecutedRecord extends ExecutedStatement  {

    private	long scriptId;
    private	String scriptName;
    private	String sourceMachine;
    private String destinationMachine;
    private long interpreterId;
    private String scriptContent;
    private String jsonParam;


    public ScriptExecutedRecord(  final long id,
                                  final long scriptId,
                                  final String requestId,
                                  final String scriptName,
                                  final String sourceMachine,
                                  final String destinationMachine,
                                  final long interpreterId,
                                  final long groupId,
                                  final String source,
                                  final long userId,
                                  final String scriptContent,
                                  final String jsonParam,
                                  final String repPath,
                                  final String comment,
                                  final long timestamp,
                                  final int accessRec,
                                  final String isValid) {
        this.setId(id);
        this.setScriptId(scriptId);
        this.setRequestId(requestId);
        this.setScriptName(scriptName);
        this.setSourceMachine(sourceMachine);
        this.setDestinationMachine(destinationMachine);
        this.setInterpreterId(interpreterId);
        this.setGroupId(groupId);
        this.setSource(source);
        this.setUserId(userId);
        this.setScriptContent(scriptContent);
        this.setJsonParam(jsonParam);
        this.setRepPath(repPath);
        this.setComment(comment);
        this.setTimestamp(timestamp);
        this.setFlag(0);
        this.setCntAccess(accessRec);
        this.setIsValid(isValid);
    }



    public ScriptExecutedRecord(  final String requestId,
                                  final String scriptName,
                                  final String sourceMachine,
                                  final String destinationMachine,
                                  final long interpreterId,
                                  final long groupId,
                                  final String source,
                                  final long userId,
                                  final String scriptContent,
                                  final String jsonParam,
                                  final String comment,
                                  final long timestamp) {
        this.setId(-1);
        this.setScriptId(-1);
        this.setRequestId(requestId);
        this.setScriptName(scriptName);
        this.setSourceMachine(sourceMachine);
        this.setDestinationMachine(destinationMachine);
        this.setInterpreterId(interpreterId);
        this.setGroupId(groupId);
        this.setSource(source);
        this.setUserId(userId);
        this.setScriptContent(scriptContent);
        this.setJsonParam(jsonParam);
        this.setRepPath("");
        this.setComment(comment);
        this.setTimestamp(timestamp);
        this.setFlag(0);
        this.setCntAccess(-1);
        this.setIsValid("N");
    }


    public ScriptExecutedRecord() {
        this.setId(-1);
        this.setScriptId(-1);
        this.setRequestId("");
        this.setScriptName("");
        this.setSourceMachine("");
        this.setDestinationMachine("");
        this.setInterpreterId(-1);
        this.setGroupId(-1);
        this.setSource(Constants.adhocShort);
        this.setUserId(-1);
        this.setScriptContent("");
        this.setJsonParam("");
        this.setRepPath("");
        this.setComment("");
        this.setTimestamp(-1);
        this.setFlag(0);
        this.setCntAccess(0);
    }

    public ScriptExecutedRecord(final long id) {
        this.setId(id);
        this.setScriptId(-1);
        this.setRequestId("");
        this.setScriptName("");
        this.setSourceMachine("");
        this.setDestinationMachine("");
        this.setInterpreterId(-1);
        this.setGroupId(-1);
        this.setSource(Constants.adhocShort);
        this.setUserId(-1);
        this.setScriptContent("");
        this.setJsonParam("");
        this.setRepPath("");
        this.setComment("");
        this.setTimestamp(-1);
        this.setFlag(0);
        this.setCntAccess(0);
    }



    public long getScriptId() { return scriptId; }
    public void setScriptId(long scriptId) { this.scriptId = scriptId; }
    public String getScriptName() { return scriptName; }
    public void setScriptName(String scriptName) { this.scriptName = scriptName; }
    public String getSourceMachine() { return sourceMachine; }
    public void setSourceMachine(String sourceMachine) { this.sourceMachine = sourceMachine; }
    public String getDestinationMachine() { return destinationMachine; }
    public void setDestinationMachine(String destinationMachine) { this.destinationMachine = destinationMachine; }
    public long getInterpreterId() { return interpreterId; }
    public void setInterpreterId(long interpreterId) { this.interpreterId = interpreterId; }
    public String getScriptContent() { return scriptContent; }
    public void setScriptContent(String scriptContent) { this.scriptContent = scriptContent; }
    public String getJsonParam() { return jsonParam; }
    public void setJsonParam(String jsonParam) { this.jsonParam = jsonParam; }



    public static ScriptExecutedRecord toScriptExecutedRecord(String j) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(j, ScriptExecutedRecord.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
