package com.widescope.rdbmsRepo.database.elasticsearch.objects.sql;

public class RecordDescription {

	private String name;
	private String type;
	
	public RecordDescription(	final String name,
								final String type) {
		this.setName(name);
		this.setType(type);
	}
	public RecordDescription() {
		this.setName(null);
		this.setType(null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
