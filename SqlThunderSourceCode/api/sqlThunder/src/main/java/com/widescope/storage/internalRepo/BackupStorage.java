package com.widescope.storage.internalRepo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.ExecutedStatement;
import com.widescope.sqlThunder.utils.StringUtils;

public class BackupStorage extends ExecutedStatement {

    private String machineName;
    private String storageType;
    private long timeStart;
    private long timeEnd;


    public BackupStorage() {
        setId(-1);
        setUserId(-1);
        setRequestId(StringUtils.generateRequestId());
        setMachineName("");
        setStorageType("");
        setComment("");
        setGroupId(-1);
        setSource("");
        setRepPath("");
        setTimeStart(-1);
        setTimeEnd(-1);
        setTimestamp(-1);
        setCntAccess(-1);
    }

    public BackupStorage(final long backupId,
                         final long userId,
                         final String requestId,
                         final String machineName,
                         final String storageType,
                         final String comment,
                         final long groupId,
                         final String source,
                         final String fullPath,
                         final long timeStart,
                         final long timeEnd,
                         final int cntAccess,
                         final String isValid) {

        setId(backupId);
        setUserId(userId);
        setRequestId(requestId);
        setMachineName(machineName);
        setStorageType(storageType);
        setComment(comment);
        setGroupId(groupId);
        setSource(source);
        setRepPath(fullPath);
        setTimeStart(timeStart);
        setTimeEnd(timeEnd);
        setTimestamp(timeStart);
        setCntAccess(cntAccess);
        setIsValid(isValid);
    }

    public BackupStorage(final long userId,
                         final String machineName,
                         final String requestId,
                         final String storageType,
                         final String comment,
                         final long groupId,
                         final String source,
                         final String fullPath,
                         final long timeStart,
                         final long timeEnd) {

        setId(-1);
        setUserId(userId);
        setRequestId(requestId);
        setMachineName(machineName);
        setStorageType(storageType);
        setComment(comment);
        setGroupId(groupId);
        setSource(source);
        setRepPath(fullPath);
        setTimeStart(timeStart);
        setTimeEnd(timeEnd);
        setTimestamp(timeStart);
        setIsValid("Y");
    }


    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }

    public long getTimeStart() { return timeStart; }
    public void setTimeStart(long timeStart) { this.timeStart = timeStart; }

    public long getTimeEnd() { return timeEnd; }
    public void setTimeEnd(long timeEnd) { this.timeEnd = timeEnd; }

    public static BackupStorage toBackupStorage(String j) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(j, BackupStorage.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}


