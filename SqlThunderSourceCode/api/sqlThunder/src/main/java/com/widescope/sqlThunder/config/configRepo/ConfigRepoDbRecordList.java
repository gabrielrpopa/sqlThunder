package com.widescope.sqlThunder.config.configRepo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;

public class ConfigRepoDbRecordList implements RestInterface{

	
	private List<ConfigRepoDbRecord> configRepoDbRecordLst;
	
	public ConfigRepoDbRecordList(final List<ConfigRepoDbRecord> configRepoDbRecordLst) {
		this.setConfigRepoDbRecordLst(configRepoDbRecordLst);
	}
	
	public ConfigRepoDbRecordList() {
		this.setConfigRepoDbRecordLst(new ArrayList<ConfigRepoDbRecord>());
	}
	
	public List<ConfigRepoDbRecord> getConfigRepoDbRecordLst() {
		return configRepoDbRecordLst;
	}

	public void setConfigRepoDbRecordLst(List<ConfigRepoDbRecord> configRepoDbRecordLst) {
		this.configRepoDbRecordLst = configRepoDbRecordLst;
	}
	
	public void addConfigRepoDbRecordLst(final ConfigRepoDbRecord configRepoDbRecord) {
		this.configRepoDbRecordLst.add(configRepoDbRecord);
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	

	public static ConfigRepoDbRecordList toConfigRepoDbRecordList(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, ConfigRepoDbRecordList.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	
	
}
