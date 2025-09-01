package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

public class PersistenceGroup implements RestInterface  {

    private long groupId;
    private String groupName;
    private String comment;

    public PersistenceGroup(final long groupId,
                            final String groupName,
                            final String comment) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.comment = comment;
    }


    public long getGroupId() { return groupId; }
    public void setGroupId(long groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getComment() { return comment; }
    public void setComment(String comment) {this.comment = comment; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistenceGroup toPersistenceGroup(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistenceGroup.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
