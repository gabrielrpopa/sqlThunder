package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.config.configRepo.Constants;

public class PersistenceSource {

    private String source;
    private String comment;

    public PersistenceSource(final String source,
                             final String comment) {
        this.source = source;
        this.comment = comment;
    }


    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getComment() { return comment; }
    public void setComment(String comment) {this.comment = comment; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistenceSource toPersistenceSource(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistenceSource.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
