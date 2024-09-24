package com.widescope.rdbmsRepo.database.mongodb.objects;



import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LargeObjectAssociatedMetadata {

	private String originalFolder;
	private long originalUserId;
	private String originalType;
	private long originalLastModified;


	public LargeObjectAssociatedMetadata() {
		this.setOriginalFolder(null);
		this.setOriginalUserId(-1);
		this.setOriginalType(null);
		this.setOriginalLastModified(-1);
	}

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
	

	public static LargeObjectAssociatedMetadata toLargeObjectAssociatedMetadata(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, LargeObjectAssociatedMetadata.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
