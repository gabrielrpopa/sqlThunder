package com.widescope.rdbmsRepo.database.mongodb.sql.query;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


/**
 * Class representing a simple search that can be saved for later dynamic execution
 */
public class SimpleMongoSearch implements RestInterface {
	
	private String itemToSearch;
	private String valueToSearch;
	private String operator;
	private String valueToSearchType;
	
	public SimpleMongoSearch(	final String itemToSearch,
								final String valueToSearch,
								final String operator,
								final String valueToSearchType) {
		
		this.setItemToSearch(itemToSearch);
		this.setValueToSearch(valueToSearch);
		this.setOperator(operator);
		this.setValueToSearchType(valueToSearchType);
		
	}
	
	
	public SimpleMongoSearch() {
		
	}

	public String toJsonString() {
		Map<String, String> ret = new HashMap<>();
		ret.put("itemToSearch", "@itemToSearch@");
		ret.put("valueToSearch", "@valueToSearch@");
		ret.put("operator", "@operator@");
		ret.put("valueToSearchType", "@valueToSearchType@");
		
		
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
	public String getValueToSearch() {
		return valueToSearch;
	}
	public void setValueToSearch(String valueToSearch) {
		this.valueToSearch = valueToSearch;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValueToSearchType() {
		return valueToSearchType;
	}
	public void setValueToSearchType(String valueToSearchType) {
		this.valueToSearchType = valueToSearchType;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
