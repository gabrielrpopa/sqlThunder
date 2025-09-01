package com.widescope.rdbmsRepo.database.mongodb.objects;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class LargeMongoBinaryFile implements RestInterface {

	private String  fileId;
	private String filename;
    private LargeObjectAssociatedMetadata largeObjectAssociatedMetadata;
    private long fileSize;
	private String fileStr;
    

    public LargeMongoBinaryFile() {
		this.setFilename(null);
		this.setLargeObjectAssociatedMetadata(null);
		this.setFileSize(-1);
		this.setFileId(null);
		this.setFileStr(null);
	}

	public String getFileStr() { return fileStr; }
	public void setFileStr(String fileStr) { this.fileStr = fileStr; }

	public String getFileId() { return fileId; }
	public void setFileId(String fileId) { this.fileId = fileId; }

	public String getFilename() { return filename; }
	public void setFilename(String filename) { this.filename = filename; }

	public LargeObjectAssociatedMetadata getLargeObjectAssociatedMetadata() { return largeObjectAssociatedMetadata; }
	public void setLargeObjectAssociatedMetadata(LargeObjectAssociatedMetadata largeObjectAssociatedMetadata) { this.largeObjectAssociatedMetadata = largeObjectAssociatedMetadata; }

	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
