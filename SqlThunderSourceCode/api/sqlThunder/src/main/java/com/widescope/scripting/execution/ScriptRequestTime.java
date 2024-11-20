package com.widescope.scripting.execution;

public class ScriptRequestTime {

    private long startTime = 0;  /*Time Start when first transaction is added*/
    private long currentTime = 0; /*Time when current transaction is added*/
    private String isTerminal = "N"; /*flag of terminal log*/

    public ScriptRequestTime(final long currentTime) {
        this.startTime = currentTime;
        this.currentTime = currentTime;
    }

    public long getStartStart() { return startTime;}
    public void setStartStart(long startTime) { this.startTime = startTime;}

    public long getCurrentTime() { return currentTime;}
    public void setCurrentTime(long currentTime) { this.currentTime = currentTime; }

    public String getIsTerminal() { return isTerminal; }
    public void setIsTerminal(String isTerminal) { this.isTerminal = isTerminal; }

    public void setCurrentTime(long currentTime, String isTerminal) { this.currentTime = currentTime; this.isTerminal = isTerminal; }

}
