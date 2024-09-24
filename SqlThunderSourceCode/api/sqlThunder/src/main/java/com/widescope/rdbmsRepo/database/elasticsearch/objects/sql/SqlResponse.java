package com.widescope.rdbmsRepo.database.elasticsearch.objects.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.rest.RestInterface;


public class SqlResponse implements RestInterface {

	private List<List<Object>> rows; /*maybe better List<? extends Object>  or List<?> to be reifiable*/
	private List<RecordDescription> cols;

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static SqlResponse toSqlResponse(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, SqlResponse.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

	public List<List<Object>> getRows() {
		return rows;
	}
	public void setRows(List<List<Object>> rows) {
		this.rows = rows;
	}
	public List<RecordDescription> getColumns() {
		return cols;
	}
	public void setColumns(List<RecordDescription> cols) {
		this.cols = cols;
	}
	
	
	public List<String> toListOfJsonStrings() {
		List<String> lst = new ArrayList<String>();
		for (List<Object> temp : rows) {
			Map<String, Object> body = new HashMap<>();
			int count = 0;
			for(Object o: temp) {
				body.put(cols.get(count++).getName(), o);
			}
			String x = new Gson().toJson(body );
			lst.add(x);
        }
		return lst;
	}
}
