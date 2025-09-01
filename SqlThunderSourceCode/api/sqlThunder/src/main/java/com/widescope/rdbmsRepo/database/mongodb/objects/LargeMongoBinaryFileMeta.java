package com.widescope.rdbmsRepo.database.mongodb.objects;

import com.google.gson.Gson;

import java.util.List;

public class LargeMongoBinaryFileMeta {

	private String name;
	private List<LargeMongoBinaryFileMetaRev> revList;
	
	public LargeMongoBinaryFileMeta(final String name,
									final List<LargeMongoBinaryFileMetaRev> revList) {
		this.setName(name);
		this.setRevList(revList);
	}

	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public List<LargeMongoBinaryFileMetaRev> getRevList() {	return revList; }
	public void setRevList(List<LargeMongoBinaryFileMetaRev> revList) { this.revList = revList; }
	public void addRev(LargeMongoBinaryFileMetaRev revList) { this.revList.add(revList); }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
