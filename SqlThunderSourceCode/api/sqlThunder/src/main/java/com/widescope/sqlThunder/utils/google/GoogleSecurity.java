package com.widescope.sqlThunder.utils.google;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.utils.FileUtilWrapper;


public class GoogleSecurity {
	private String clientId;
	public String getClientId() {return this.clientId;}
	private String clientSecret; 
	public String getClientSecret() {return this.clientSecret;}
	
	public GoogleSecurity () throws IOException {
		
    }





	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static GoogleSecurity toGoogleSecurity(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, GoogleSecurity.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}
	
	
	
	public static GoogleSecurity loadGoogleSecurity() throws IOException {
		GoogleSecurity ret;
		String fileName = "./googleSecurity.json";
		if(FileUtilWrapper.isFilePresent(fileName)) {
			String content = FileUtilWrapper.readFileToString(fileName);
			ret = GoogleSecurity.toGoogleSecurity(content);
		} else {
			ret = new GoogleSecurity();
			String content = ret.toString();
			FileUtilWrapper.writeFile("./googleSecurity.json", content);
		}
		
		return ret;
		
	}
	
	
	
}
