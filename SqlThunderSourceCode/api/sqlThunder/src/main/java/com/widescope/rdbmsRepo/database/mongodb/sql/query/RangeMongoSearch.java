package com.widescope.rdbmsRepo.database.mongodb.sql.query;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class RangeMongoSearch implements RestInterface {

	private String itemToSearch;
	private String fromValue;
	private String toValue;
	private String valueSearchType;
	
	
	public RangeMongoSearch (	final String itemToSearch,
								final String fromValue,
								final String toValue,
								final String valueSearchType) {
		
		this.setItemToSearch(itemToSearch);
		this.setFromValue(fromValue);
		this.setToValue(toValue);
		this.setValueSearchType(valueSearchType);
	}
	
	public RangeMongoSearch () {
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public String toJsonString() {
		Map<String, String> ret = new HashMap<>();
		ret.put("itemToSearch", "@itemToSearch@");
		ret.put("fromValue", "@fromValue@");
		ret.put("toValue", "@toValue@");
		ret.put("valueSearchType", "@valueSearchType@");
		ObjectMapper mapper = new ObjectMapper();
		try	{
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ret);
		}
		catch(JsonProcessingException ex) {
			return null;
		}
	}



	public String getItemToSearch() {
		return itemToSearch;
	}
	public void setItemToSearch(String itemToSearch) {
		this.itemToSearch = itemToSearch;
	}
	public String getFromValue() {
		return fromValue;
	}
	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}
	public String getToValue() {
		return toValue;
	}
	public void setToValue(String toValue) {
		this.toValue = toValue;
	}
	public String getValueSearchType() {
		return valueSearchType;
	}
	public void setValueSearchType(String valueSearchType) {
		this.valueSearchType = valueSearchType;
	}
	
	
}
