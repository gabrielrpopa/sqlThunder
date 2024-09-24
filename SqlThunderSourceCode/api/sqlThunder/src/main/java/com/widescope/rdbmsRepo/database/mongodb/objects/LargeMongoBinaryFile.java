package com.widescope.rdbmsRepo.database.mongodb.objects;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class LargeMongoBinaryFile implements RestInterface{

	private String filename;
    private LargeObjectAssociatedMetadata largeObjectAssociatedMetadata;
    private long fileSize;
    private byte[] file;
    

    public LargeMongoBinaryFile() {
		this.setFilename(null);
		this.setLargeObjectAssociatedMetadata(null);
		this.setFileSize(-1);
		this.setFile(null);
	}

	public String getFilename() { return filename; }
	public void setFilename(String filename) { this.filename = filename; }

	public LargeObjectAssociatedMetadata getLargeObjectAssociatedMetadata() { return largeObjectAssociatedMetadata; }
	public void setLargeObjectAssociatedMetadata(LargeObjectAssociatedMetadata largeObjectAssociatedMetadata) { this.largeObjectAssociatedMetadata = largeObjectAssociatedMetadata; }

	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }
	
	public byte[] getFile() { return file; }
	public void setFile(byte[] file) { this.file = file; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
