package com.widescope.scripting;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class DownloadScriptStructure implements RestInterface {
	private String uuid;
	private String fileName;
	private String content;
	private String path;
	private String isMain;
	private String fileType;  
	
	private ScriptDetail scriptDetail;
	
	public DownloadScriptStructure (final String uuid,
									final String fileName,
									final String content,
									final String path,
									final String isMain,
									final String fileType,
									final ScriptDetail scriptDetail) {
		this.setUuid(uuid);
		this.setContent(content);
		this.setPath(path);
		this.setIsMain(isMain);
		this.setFileType(fileType);
		this.setFileName(fileName);
		this.setScriptDetail(ScriptDetail.toScriptDetail(scriptDetail.toString()));  
	}
	
	public DownloadScriptStructure () {
		this.setUuid(null);
		this.setContent(null);
		this.setPath(null);
		this.setIsMain("N");
		this.setFileType(null);
		this.setFileName(null);
		this.setScriptDetail(null);  
	}
	
	
	
	public String getUuid() { return uuid; }
	public void setUuid(String uuid) { this.uuid = uuid; }

	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }
	
	public String getIsMain() { return isMain; }
	public void setIsMain(String isMain) { this.isMain = isMain; }
	
	public String getFileType() { return fileType; }
	public void setFileType(String fileType) { this.fileType = fileType; }
	
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	
	public ScriptDetail getScriptDetail() { return scriptDetail; }
	public void setScriptDetail(ScriptDetail scriptDetail) { this.scriptDetail = scriptDetail; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
