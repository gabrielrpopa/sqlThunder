package com.widescope.scripting;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.FileCharacteristic;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

public class DownloadScriptStructureList implements RestInterface{
	private List<DownloadScriptStructure> downloadScriptStructureLst;

	public DownloadScriptStructureList(final List<DownloadScriptStructure> downloadScriptStructureLst) {
		this.setDownloadScriptStructureList(downloadScriptStructureLst);
	}
	
	public DownloadScriptStructureList() {
		this.setDownloadScriptStructureList(new ArrayList<DownloadScriptStructure>());
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public List<DownloadScriptStructure> getDownloadScriptStructureList() {
		return downloadScriptStructureLst;
	}
	public void setDownloadScriptStructureList(final List<DownloadScriptStructure> downloadScriptStructureLst) {
		this.downloadScriptStructureLst = downloadScriptStructureLst;
	}
	public void addDownloadScriptStructure(DownloadScriptStructure downloadScriptStructure) {
		this.downloadScriptStructureLst.add(downloadScriptStructure);
	}
	
	
	public void addBulkDownloadScriptStructure(	final List<FileCharacteristic> lst) throws Exception {
		for(FileCharacteristic fc: lst) {
			DownloadScriptStructure d = new DownloadScriptStructure();
			d.setPath(fc.getRelativePath());
			String content = FileUtilWrapper.getFileContent(fc.getCanonicalPath());
			d.setFileName(fc.getFileName());
			d.setContent(content);
			d.setFileType(FileUtilWrapper.getFileType(fc.getCanonicalPath())); 
			d.setUuid(UUID.randomUUID().toString());
			d.setIsMain("N");
			d.setScriptDetail(null);
			this.downloadScriptStructureLst.add(d);
		}
	}
	
}
