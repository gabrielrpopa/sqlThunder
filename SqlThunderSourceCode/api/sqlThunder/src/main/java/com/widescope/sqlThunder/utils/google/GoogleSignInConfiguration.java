package com.widescope.sqlThunder.utils.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;


@Component
public class GoogleSignInConfiguration {

    private final GoogleIdTokenVerifier verifier;
    public GoogleIdTokenVerifier getGoogleIdTokenVerifier() {return this.verifier;}
    private final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow;
    public GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() {return this.googleAuthorizationCodeFlow;}
    
    public GoogleSignInConfiguration() throws IOException {
    	GoogleSecurity g = GoogleSecurity.loadGoogleSecurity();
    	if(g.getClientId() == null 
    		|| g.getClientId().isBlank()
    		|| g.getClientId().isEmpty() 
    		|| g.getClientSecret() == null
    		|| g.getClientSecret().isBlank()
    		|| g.getClientSecret().isEmpty()) {
    		this.verifier = null;
    		this.googleAuthorizationCodeFlow = null; 
    	} else {
    		this.verifier = this.tokenVerifier(g.getClientId());
        	this.googleAuthorizationCodeFlow = this.authorizationCodeFlow(g.getClientId(), g.getClientSecret());
			System.out.println("Google Account Verified!");
    	}
    			
    	
    	
    }


    private
    GoogleIdTokenVerifier 
    tokenVerifier(final String clientId) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

    }

    private 
    GoogleAuthorizationCodeFlow 
    authorizationCodeFlow(	final String clientId,
    						final String clientSecret) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        return new GoogleAuthorizationCodeFlow.Builder(
                transport, jsonFactory, clientId, clientSecret,
                Arrays.asList("profile", "email")
        ).build();
    }
    
   
    
}
