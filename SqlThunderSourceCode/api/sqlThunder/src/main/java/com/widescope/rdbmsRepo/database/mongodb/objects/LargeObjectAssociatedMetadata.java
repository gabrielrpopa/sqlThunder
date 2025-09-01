package com.widescope.rdbmsRepo.database.mongodb.objects;



import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LargeObjectAssociatedMetadata {

	private String folder;
	private long userId;
	private String type;
	private long lastModified;


	public LargeObjectAssociatedMetadata() {
		this.setFolder(null);
		this.setUserId(-1);
		this.setType(null);
		this.setLastModified(-1);
	}

	public String getFolder() { return folder; }
	public void setFolder(String folder) { this.folder = folder; }
	
	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public long getLastModified() { return lastModified; }
	public void setLastModified(long lastModified) { this.lastModified = lastModified; }


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
