package com.widescope.persistence.execution;

public class ExecutedStatementAccess {
    private long userId;
    private long executedId;

    public ExecutedStatementAccess() {
        setUserId(-1);
        setExecutedId(-1);
    }

    public ExecutedStatementAccess(final long userId_, final long executedId_) {
        setUserId(userId_);
        setExecutedId(executedId_);
    }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getExecutedId() { return executedId; }
    public void setExecutedId(long executedId) { this.executedId = executedId; }
}
