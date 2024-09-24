package com.widescope.sqlThunder.utils.okta;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OktaAuthWrapper {


	private String accessToken;
	private String issuer; // the issuer url
	private String clientId; // the client id
	private String clientSecret; // the client secret
	private Set<String> scopes; // the set of scopes
	private  String redirectUrl; // the redirect uri
    
    public OktaAuthWrapper ()  {
		/*
		try {
			OktaAuthWrapper o = OktaAuthWrapper.loadOktaSecurity();
			if(o != null) {
				this.setAccessToken(o.getAccessToken());
				this.setIssuer(o.getIssuer());
				this.setClientId(o.getClientId());
				this.setScopes(o.getScopes());
				this.setRedirectUrl(o.getRedirectUrl());
			}
		} catch (Exception ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl) ;
		}
		*/

    }
       
    
	public String getAccessToken() { return accessToken; }
	public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

	public String getIssuer() { return issuer; }
	public void setIssuer(String issuer) { this.issuer = issuer; }

	public String getClientId() { return clientId; }
	public void setClientId(String clientId) { this.clientId = clientId; }

	public String getClientSecret() { return clientSecret; }
	public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

	public Set<String> getScopes() { return scopes; }
	public void setScopes(Set<String> scopes) { this.scopes = scopes; }

	public String getRedirectUrl() { return redirectUrl; }
	public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	public static OktaAuthWrapper toOktaAuthWrapper(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, OktaAuthWrapper.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}

	}
	
	
	public static OktaAuthWrapper loadOktaSecurity() throws Exception {
		OktaAuthWrapper ret;
		String fileName = "oktaSecurity.json";
		try {
			String content = FileUtilWrapper.readFileFromResToString(fileName);
			ret = OktaAuthWrapper.toOktaAuthWrapper(content);
		} catch(Exception e) {
			throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
		}
		return ret;
	}

    
}
