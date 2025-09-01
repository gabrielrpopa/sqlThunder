package com.widescope.rdbmsRepo.database.mongodb.objects;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class LargeMongoBinaryFileSummary implements RestInterface {
	private String id;
	private String filename;
	private long timeStamp;
    private long fileSize;
	private String originalFolder;
	private long originalUserId;
	private String originalType;
	private long originalLastModified;


    public LargeMongoBinaryFileSummary() {
		this.setFilename(null);
		this.setFileSize(-1);
		this.setTimeStamp(-1);
		this.setId(null);
		this.setOriginalFolder(null);
		this.setOriginalUserId(-1);
		this.setOriginalType("");
		this.setOriginalLastModified(-1);
	}

	public LargeMongoBinaryFileSummary(final String fileName,
									   final long fileSize,
									   final long timeStamp,
									   final String id,
									   final String origFolder,
									   final long origUser,
									   final String origType,
									   final long origLastModified
									   ) {
		this.setFilename(fileName);
		this.setFileSize(fileSize);
		this.setTimeStamp(timeStamp);
		this.setId(id);
		this.setOriginalFolder(origFolder);
		this.setOriginalUserId(origUser);
		this.setOriginalType(origType);
		this.setOriginalLastModified(origLastModified);
	}

	public long getTimeStamp() { return timeStamp; }
	public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	public String getId() {	return id; }
	public void setId(String id) { this.id = id; }

	public String getFilename() { return filename; }
	public void setFilename(String filename) { this.filename = filename; }

	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }

	public String getOriginalFolder() { return originalFolder; }
	public void setOriginalFolder(String originalFolder) { this.originalFolder = originalFolder; }

	public long getOriginalUserId() { return originalUserId; }
	public void setOriginalUserId(long originalUserId) { this.originalUserId = originalUserId; }

	public String getOriginalType() { return originalType; }
	public void setOriginalType(String originalType) { this.originalType = originalType; }

	public long getOriginalLastModified() { return originalLastModified; }
	public void setOriginalLastModified(long originalLastModified) { this.originalLastModified = originalLastModified; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
