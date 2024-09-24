/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.rest;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter; 



public class JsonResponse implements RestInterface {
	private Object payload;
	public JsonResponse() { }
	public JsonResponse(final Object payload) { this.payload = payload; }
	public JsonResponse(final String payloadStr) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        this.payload = mapper.readValue(payloadStr, Object.class);
    }
	public Object getGenericPayload() { return payload; }
	public void setGenericPayload(final Object payload) { this.payload = payload; }
	
	public static String getJsonStr (Object payload) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(payload);
	}
	
	public static JSONObject getJsonObj (Object payload) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(payload);
		ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, JSONObject.class);
    }
}