package com.widescope.sqlThunder.config.configRepo;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class ConfigRepoDbRecord implements RestInterface{

	private int id;
	private String configName;
	private String configValue;
	private String configDescription;
	private String configType;

	public ConfigRepoDbRecord ( final int id,
								final String configName,
								final String configValue,
								final String configDescription,
								final String configType) {
		this.setId(id);
		this.setConfigName(configName);
		this.setConfigValue(configValue);
		this.setConfigDescription(configDescription);
		this.setConfigType(configType);
	}
	
	public ConfigRepoDbRecord () {
		this.setId(-1);
		this.setConfigName("");
		this.setConfigValue("");
		this.setConfigDescription("");
		this.setConfigType("");
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getConfigDescription() {
		return configDescription;
	}
	public void setConfigDescription(String configDescription) {
		this.configDescription = configDescription;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
