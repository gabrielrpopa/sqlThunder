package com.widescope.rdbmsRepo;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class ExecutedStatement implements RestInterface  {
    protected long id;
    protected int flag = -1;  /* see ExecutedStatementFlag */
    protected long userId = -1;
    protected String repPath;
    protected String isValid;
    protected String requestId;
    protected long groupId;
    protected String source; /*A-ADHOC or R-REPO*/
    protected String comment;
    protected long timestamp;
    protected int cntAccess;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId;}
    public int getFlag() { return flag; }
    public void setFlag(int flag) { this.flag = flag; }
    public String getRepPath() { return repPath; }
    public void setRepPath(String repPath) { this.repPath = repPath; }
    public String getIsValid() { return isValid; }
    public void setIsValid(String isValid) { this.isValid = isValid; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getSource() {	return source;	}
    public void setSource(String source) {	this.source = source; }
    public long getGroupId() { return groupId; }
    public void setGroupId(long groupId) { this.groupId = groupId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getCntAccess() { return cntAccess; }
    public void setCntAccess(int cntAccess) { this.cntAccess = cntAccess; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
