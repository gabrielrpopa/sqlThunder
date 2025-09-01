package com.widescope.storage.dataExchangeRepo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class FileDescriptorList {

	private List<FileDescriptor> fileDescriptorList;
	
	public FileDescriptorList() {
		this.setFileDescriptorList(new ArrayList<FileDescriptor>());
	}
	
	public FileDescriptorList(List<FileDescriptor> fileDescriptorList_) {
		this.setFileDescriptorList(fileDescriptorList_);
	}

	public List<FileDescriptor> getFileDescriptorList() {
		return fileDescriptorList;
	}

	public void setFileDescriptorList(List<FileDescriptor> fileDescriptorList) {
		this.fileDescriptorList = fileDescriptorList;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
