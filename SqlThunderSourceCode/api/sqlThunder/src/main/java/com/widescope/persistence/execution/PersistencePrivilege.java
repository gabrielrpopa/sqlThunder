package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PersistencePrivilege {


    /*Privileges*/
    public static final String pTypeNone = "N";
    public static final String pTypeDownload = "D";
    public static final String pTypeProcess = "P";
    public static final String pTypeAdmin = "A";



    private String privilege;
    private String comment;

    public PersistencePrivilege(final String privilege,
                                final String comment) {
        this.privilege = privilege;
        this.comment = comment;
    }


    public String getPrivilege() { return privilege; }
    public void setPrivilege(String privilege) { this.privilege = privilege; }
    public String getComment() { return comment; }
    public void setComment(String comment) {this.comment = comment; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistencePrivilege toPersistenceSource(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistencePrivilege.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
