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

package com.widescope.sqlThunder.controller.v2;

import java.util.List;
import java.util.stream.Collectors;


import com.widescope.chat.db.ChatDb;
import com.widescope.chat.users.ChatUser;
import com.widescope.chat.users.OnOffLineThreadToFriends;
import com.widescope.chat.users.UserToChat;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import com.widescope.sqlThunder.utils.user.UserShort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang.WordUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.auth.oauth2.TokenVerifier;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.google.GoogleSignInConfiguration;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import com.widescope.sqlThunder.utils.user.InternalUserDb;
import com.widescope.sqlThunder.utils.user.User;




@CrossOrigin
@RestController
@Schema(title = "Google Controller")
public class GoogleController {


	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private InternalUserDb internalUserDb;


	@Autowired
	private ChatDb chatDb;

	@Autowired
	private GoogleSignInConfiguration googleSignInConfiguration;
	
	

	@PostConstruct
	public void initialize() {
		
	}
	
	
	
		
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/google/token", method = RequestMethod.POST)
	@Operation(summary = "Call Back to get the token")
	public 
	ResponseEntity<RestObject>  
	getClientToken (@RequestHeader(value="requestId", defaultValue = "") String requestId,
					@RequestHeader(value="code") final String code) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);
		try	{
			GoogleAuthorizationCodeTokenRequest tokenRequest =
					 googleSignInConfiguration.getGoogleAuthorizationCodeFlow().newTokenRequest(code);
	        tokenRequest.setRedirectUri("http://localhost:8080/google/callback");
		    GoogleTokenResponse tokenResponse = tokenRequest.execute();
		    //GoogleIdToken token = tokenResponse.parseIdToken();
		    return RestObject.retOKWithPayload(new GenericResponse(tokenResponse.getIdToken()), requestId, methodName);
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return null;
		} catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
            return null;
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/google/authenticate", method = RequestMethod.POST)
	@Operation(summary = "Authenticate by verifying token")
	public ResponseEntity<RestObject> 
	authenticateToken(@RequestHeader(value="requestId", defaultValue = "") String requestId,
					  @RequestHeader(value="token") final String token,
					  @RequestHeader(value="email") final String email,
					  @RequestHeader(value="firstName")  String firstName,
					  @RequestHeader(value="lastName")  String lastName,
					  @RequestHeader(value="avatarUrl") final String avatarUrl) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);

        try {
			requestId = StringUtils.generateRequestId(requestId);
        	if(googleSignInConfiguration.getGoogleIdTokenVerifier() == null 
        			|| googleSignInConfiguration.getGoogleAuthorizationCodeFlow() == null) {
        		return RestObject.retException(requestId, methodName, "ERROR");
        	}
        	// Review this logic
        	User u = new User("USER", email, null, firstName, lastName, email, -1, -1, -1, "", "", "Y");
			u.setAvatarUrl(avatarUrl);
            Boolean isVerified = googleSignInConfiguration.getGoogleIdTokenVerifier().verify(token).getPayload().getEmailVerified();
            if(isVerified) {
            	u = authUtil.getUser(email);
                if(u == null) {  // register it 
                	firstName = WordUtils.capitalizeFully(firstName);
                	lastName = WordUtils.capitalizeFully(lastName);
                	u = new User("USER", email, null, firstName, lastName, email.toLowerCase(), -1, -1, -1, "", "", "Y");
        			internalUserDb.addUser(u);
        			u = authUtil.getUser(email);
					u.setAvatarUrl(avatarUrl);
                } 
            }
            else {
            	u.setActive("N");
            }
            
            String session = StaticUtils.generateSecureStringN(512);
            u.setSession(session);
            System.out.println("user: "+ u.getUser() + " is now auth by Google");
			UserShort us = new UserShort(session, u);
			InternalUserDb.loggedUsers.put(session, us);


			/*Send notifications to friends that I'm on-line now
			 * Also, send me notifications about the status of each of my friends
			 * */
			try {
				UserToChat uToChat = new UserToChat(u);
				List<ChatUser> chatUsers = chatDb.getUsersTo(u.getUser());
				List<User> friends = authUtil.getUsers(chatUsers.stream().map(ChatUser::getToId).collect(Collectors.toList()));
				new OnOffLineThreadToFriends(friends, uToChat).start();
				//new OnOffLineThreadFromFriends(friends, uToChat).start();
			} catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}


            return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
	}
	
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/google/authenticate-new", method = RequestMethod.POST)
	@Operation(summary = "Authenticate by verifying token")
	public ResponseEntity<RestObject> 
	authenticateTokenNew(@RequestHeader(value="requestId", defaultValue = "") String requestId,
						 @RequestHeader(value="token") final String token,
						 @RequestHeader(value="email") final String email,
						 @RequestHeader(value="firstName") String firstName,
						 @RequestHeader(value="lastName") String lastName,
						 @RequestHeader(value="avatarUrl") final String avatarUrl) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		requestId = StringUtils.generateRequestId(requestId);

        try {
        	
        	// The service name for which the id token was requested.
            String targetAudience = "https://sqlthunder.ca";
            String jwkUrl = "https://www.googleapis.com/oauth2/v3/certs";
            verifyGoogleIdToken(token, targetAudience, jwkUrl);
            
            
        	if(googleSignInConfiguration.getGoogleIdTokenVerifier() == null 
        			|| googleSignInConfiguration.getGoogleAuthorizationCodeFlow() == null) {
        		return RestObject.retException(requestId, methodName, "ERROR");
        	}
        	// Review this logic
        	User u = new User("USER", email, null, firstName, lastName, email, -1, -1, -1, "", "", "Y");
            Boolean isVerified = googleSignInConfiguration.getGoogleIdTokenVerifier().verify(token).getPayload().getEmailVerified();
            if(isVerified) {
            	u = authUtil.getUser(email);
                if(u == null) {  // register it 
                	firstName = WordUtils.capitalizeFully(firstName);
                	lastName = WordUtils.capitalizeFully(lastName);
                	u = new User("USER", email, null, firstName, lastName, email.toLowerCase(), -1, -1, -1, "", "", "Y");
					u.setAvatarUrl(avatarUrl);
        			internalUserDb.addUser(u);
        			u = authUtil.getUser(email);
                } 
            }
            else {
            	u.setActive("N");
            }
            
            String session = StaticUtils.generateSecureString32();
            u.setSession(session);
			System.out.println("user: "+ u.getUser() + " is now auth by Google");
			UserShort us = new UserShort(session, u);
			InternalUserDb.loggedUsers.put(session, us);

			/* Send notifications to parties that I'm on-line now
			 * Also, send me notifications about the status of each of my connections
			 * */
			try {
				UserToChat uToChat = new UserToChat(u);
				List<ChatUser> chatUsers = chatDb.getUsersTo(u.getUser());
				List<User> friends = authUtil.getUsers(chatUsers.stream().map(ChatUser::getToId).collect(Collectors.toList()));
				new OnOffLineThreadToFriends(friends, uToChat).start();
				//new OnOffLineThreadFromFriends(friends, uToChat).start();
			} catch (Exception ex) {
				AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			}


            return RestObject.retOKWithPayload(u, requestId, methodName);
		} catch(Exception ex) {
			return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
		}
    }
	
	
	

	public boolean verifyGoogleIdToken(final String idToken, final String audience, final String jwkUrl) {

		TokenVerifier tokenVerifier = TokenVerifier	.newBuilder()
													.setAudience(audience)
	            									//Optional, when verifying a Google ID token, the jwk url is set by default.
	            									.setIssuer(jwkUrl)
	            									.build();

	    try {
	      // Verify the token.
	      JsonWebToken jsonWebToken = tokenVerifier.verify(idToken);

	      // Verify that the token contains subject and email claims.
	      JsonWebToken.Payload payload = jsonWebToken.getPayload();
	      // Get the user id.
	      String userId = payload.getSubject();
	      System.out.println("User ID: " + userId);

	      // Optionally, if "INCLUDE_EMAIL" was set in the token options, check if the
	      // email was verified.
	      if (payload.get("email") != null) {
	        System.out.printf("Email verified: %s", payload.get("email"));
	        return true;
	      }
	      return false;
	    } catch (TokenVerifier.VerificationException ex) {
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
	      	return false;
	    } catch(Throwable ex)	{
			AppLogger.logThrowable(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl);
			return false;
		}
	}

	
	
}
