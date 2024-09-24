/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.widescope.sqlThunder.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonUtils {
	
	public static String removeWhitespaces(final String json) {
	    boolean quoted = false;
	    boolean escaped = false;
	    StringBuilder out = new StringBuilder("");

	    char[] charArray = json.toCharArray();
	    for(char c : charArray) {
	        if(escaped) {
	        	out.append(c);
	            escaped = false;
	            continue;
	        }

	        if(c == '"') {
	            quoted = !quoted;
	        } else if(c == '\\') {
	            escaped = true;
	        }

	        if(c == ' ' &! quoted) {
	            continue;
	        }
	        out.append(c);
	    }
	    return out.toString();
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public static boolean isJsonValid(String jsonString) {
	    try {
	        new JSONObject(jsonString);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(jsonString);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	
	public static String commastringToJsonString(String jsonString) {
	    try {
	    	String [] arrayStr=jsonString.split(",");
	    	Map<String,String> map = new HashMap<>();
	    	String key = null;
	    	for (String s: arrayStr){
	    	    if(key == null) {
	    	       key = s;
	    	    } else {
	    	    	
	    	       map.put(key, s);
	    	       key = null;
	    	    }
	    	}
	    	
	    	
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	String json = objectMapper.writeValueAsString(map);
            return json;
            
	    } catch (JSONException ex) {
	    	return null;
	    } catch (JsonProcessingException e) {
	    	return null;
		}
	}
	
	public static List<Object> toList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	
	
	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();

	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);
	        
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }
	        
	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}
	
	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
	    Map<String, Object> retMap = new HashMap<String, Object>();
	    
	    if(json != JSONObject.NULL) {
	        retMap = toMap(json);
	    }
	    return retMap;
	}
	
	public static Map<String, Object> jsonToMap(String jsonString) throws JSONException, ParseException {
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(jsonString);  
		
	    Map<String, Object> retMap = new HashMap<String, Object>();
	    
	    if(json != JSONObject.NULL) {
	        retMap = toMap(json);
	    }
	    return retMap;
	}
	
	
}
