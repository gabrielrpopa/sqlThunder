package com.widescope.rdbmsRepo.database.tempSqlRepo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AdhocStatement {

    private String stm;
    private long timeStamp;
    private String type;
    private String comment;
    private long originalUserId;

    public AdhocStatement() {
        this.stm = "";
        this.timeStamp = 0;
        this.type = "";
        this.comment = "";
        this.originalUserId = 0;
    }

    public AdhocStatement(final String stm,
                          final long timeStamp,
                          final String type,
                          final String comment,
                          final long originalUserId) {
        this.stm = stm;
        this.timeStamp = timeStamp;
        this.type = type;
        this.comment = comment;
        this.originalUserId = originalUserId;
    }

    public String getStm() { return stm; }
    public void setStm(String stm) { this.stm = stm; }

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    public long getOriginalUserId() { return this.originalUserId; }
    public void setOriginalUserId(long originalUserId) { this.originalUserId = originalUserId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public static AdhocStatement toAdhocStatement(String str) {
        try	{
            Gson g = new Gson();
            return g.fromJson(str, AdhocStatement.class);
        }
        catch(Exception ex)	{
            return null;
        }
    }

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


}
