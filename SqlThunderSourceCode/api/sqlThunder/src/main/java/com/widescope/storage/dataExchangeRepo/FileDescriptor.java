package com.widescope.storage.dataExchangeRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class FileDescriptor {

	private String description;
	private String path;
	private String fileName;
	private String type;
	private List<String> sqls;

	public FileDescriptor() {
		this.setDescription("");
		this.setSqls(new ArrayList<String>());
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getSqls() {
		return sqls;
	}
	public void setSqls(List<String> sqls) {
		this.sqls = sqls;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
