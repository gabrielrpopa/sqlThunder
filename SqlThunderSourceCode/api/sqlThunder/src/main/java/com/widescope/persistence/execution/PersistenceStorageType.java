package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PersistenceStorageType {

    private long persistenceStorageId;
    private String persistenceStorage;
    private String comment;

    public PersistenceStorageType() {

    }

    public PersistenceStorageType(final long persistenceStorageId,
                                    final String persistenceStorage,
                                    final String comment) {
        this.persistenceStorageId = persistenceStorageId;
        this.persistenceStorage = persistenceStorage;
        this.comment = comment;
    }


    public long getPersistenceStorageId() { return persistenceStorageId; }
    public void setPersistenceStorageId(long persistenceStorageId) { this.persistenceStorageId = persistenceStorageId; }

    public String getPersistenceStorage() { return persistenceStorage; }
    public void setPersistenceStorage(String persistenceStorage) { this.persistenceStorage = persistenceStorage; }

    public String getComment() { return comment; }
    public void setComment(String comment) {this.comment = comment; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistenceStorageType toPersistenceStorageType(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistenceStorageType.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


}
