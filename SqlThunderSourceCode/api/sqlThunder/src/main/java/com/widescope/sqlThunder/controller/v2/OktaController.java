

package com.widescope.sqlThunder.controller.v2;



import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.okta.idx.sdk.api.response.ErrorResponse;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.okta.commons.lang.Assert;
import com.okta.commons.lang.Strings;
import com.okta.idx.sdk.api.client.Authenticator;
import com.okta.idx.sdk.api.client.IDXAuthenticationWrapper;
import com.okta.idx.sdk.api.client.ProceedContext;
import com.okta.idx.sdk.api.model.AuthenticationOptions;
import com.okta.idx.sdk.api.model.AuthenticationStatus;
import com.okta.idx.sdk.api.model.ContextualData;
import com.okta.idx.sdk.api.model.Credentials;
import com.okta.idx.sdk.api.model.FormValue;
import com.okta.idx.sdk.api.model.QrCode;
import com.okta.idx.sdk.api.model.TokenType;
import com.okta.idx.sdk.api.model.UserProfile;
import com.okta.idx.sdk.api.model.VerifyAuthenticatorAnswer;
import com.okta.idx.sdk.api.model.VerifyAuthenticatorOptions;
import com.okta.idx.sdk.api.model.VerifyChannelDataOptions;
import com.okta.idx.sdk.api.request.WebAuthnRequest;
import com.okta.idx.sdk.api.response.AuthenticationResponse;
import com.okta.idx.sdk.api.response.TokenResponse;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.AuthorizationMode;

import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.utils.okta.OktaAuthWrapper;
import com.widescope.sqlThunder.utils.okta.OktaModelAndView;
import com.widescope.sqlThunder.utils.okta.OktaUser;
import com.widescope.sqlThunder.utils.okta.OktaUserList;
import com.widescope.sqlThunder.utils.okta.PollResults;
import com.widescope.sqlThunder.utils.okta.ResponseHandler;
import com.widescope.sqlThunder.utils.okta.Util;


import static com.widescope.sqlThunder.utils.okta.Util.constructRequestContext;


@CrossOrigin
@RestController
@Schema(title = "Okta Controller")
public class OktaController {

    @Autowired
    private ResponseHandler responseHandler;
    
    @Autowired
    private OktaAuthWrapper o;


    @PostConstruct
    public void initialize() {

    }
   

	private static com.okta.sdk.client.Client setClient(OktaAuthWrapper o) {
        return com.okta.sdk.client.Clients.builder()
				.setAuthorizationMode(AuthorizationMode.SSWS) //set the SSWS authentication mode
				.setClientId(o.getClientId()) // okta.oauth2.client-id
				.setPrivateKey(o.getClientSecret()) // okta.oauth2.client-secret
				.setOrgUrl(o.getRedirectUrl()) // okta.oauth2.redirect
				.setClientCredentials(new TokenClientCredentials(o.getAccessToken()))  // okta.client.token
			    .build();
	}
	
		
	
	@CrossOrigin(origins = "*")
	@GetMapping("/okta/users:get") 
    @Operation(summary = "Get all users")
    public 
    ResponseEntity<RestObject> 
    getUsers(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		com.okta.sdk.client.Client client = setClient(o);
		OktaUserList o = new OktaUserList(client.listUsers());
		return RestObject.retOKWithPayload(o , requestId, methodName);
    }
	
	@CrossOrigin(origins = "*")
	@GetMapping("/okta/users:query")
    @Operation(summary = "The the list of users, based on a query")
    @ApiResponses(value = {
            @ApiResponse(   responseCode = "200",
                            description = "Ok",
                            content = { @Content(mediaType = "application/json",
                                                schema = @Schema(implementation = com.okta.sdk.resource.user.UserList.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ID "),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = { @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class)) }) })
	public
	ResponseEntity<RestObject>  
	searchUserByEmail(@RequestHeader(value="requestId", defaultValue = "") String requestId,
                      @RequestParam final String query) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		com.okta.sdk.client.Client client = setClient(o);
		OktaUserList o = new OktaUserList(client.listUsers(query, null, null, null, null));
		return RestObject.retOKWithPayload(o , requestId, methodName);
	}
	
	
	
	@CrossOrigin(origins = "*")
	@GetMapping("/okta/user:create")
    @Operation(summary = "Create an new user")
    @ApiResponses(value = {
            @ApiResponse(   responseCode = "200",
                            description = "Ok",
                            content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.okta.sdk.resource.user.User.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)) }) })

    public
	ResponseEntity<RestObject>  
	createUser(@RequestHeader(value="requestId", defaultValue = "") String requestId) {
        requestId = StringUtils.generateRequestId(requestId);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();


		com.okta.sdk.client.Client client = setClient(o);
	    char[] tempPassword = {'P','a','$','$','w','0','r','d'};
	    
	    com.okta.sdk.resource.user.User user 
	    = com.okta.sdk.resource.user.UserBuilder.instance()
										        .setEmail("normal.lewis@email.com")
										        .setFirstName("Norman")
										        .setLastName("Lewis")
										        .setPassword(tempPassword)
										        .setActive(true)
										        .buildAndCreate(client);
	    OktaUser o = new OktaUser(user);
	    return RestObject.retOKWithPayload(o , requestId, methodName);
	}
	


	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/login", method = RequestMethod.POST)
    @Operation(summary = "Set an object in the cache")
	public 
	ResponseEntity<RestObject> 
	login (@RequestHeader(value="requestId", defaultValue = "") String requestId,
           @RequestParam("username") final String username,
           @RequestParam(value = "password", required = false) final String password,
           final HttpSession session) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		try	{
			IDXAuthenticationWrapper idxAuthenticationWrapper 
			= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());
			// begin transaction
	        AuthenticationResponse beginResponse = idxAuthenticationWrapper.begin(constructRequestContext());

	        // get proceed context
	        ProceedContext proceedContext = beginResponse.getProceedContext();

	        // trigger authentication
	        AuthenticationResponse authenticationResponse;

	        if (Strings.hasText(password)) {
	            authenticationResponse = idxAuthenticationWrapper.authenticate(
	                    new AuthenticationOptions(username, password.toCharArray()), proceedContext);
	        } else {
	            authenticationResponse = idxAuthenticationWrapper.authenticate(new AuthenticationOptions(username), proceedContext);
	        }

	        if (responseHandler.needsToShowErrors(authenticationResponse)) {
	            ModelAndView modelAndView = new ModelAndView("redirect:/login");
	            modelAndView.addObject("errors", authenticationResponse.getErrors());
	            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
	        }

	        if (authenticationResponse.getAuthenticatorEnrollments() != null) {
	        	
	        	authenticationResponse	.getAuthenticatorEnrollments()
				.stream()
				.filter(x->x.getCredentialId().equals("Okta Verify"))
				.findFirst()
				.flatMap(enroll->Arrays.stream(enroll.getMethods())
						.filter(methodType -> methodType.getType().equals("totp"))
						.findFirst()
						).ifPresent(methodType -> session.setAttribute("totp", "totp"));
	        }

	        ModelAndView modelAndView = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
	        return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        } catch(Exception ex) {
            return RestObject.retException(new GenericResponse(ex.getMessage()), requestId, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.ctrl));
        }
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/logout", method = RequestMethod.POST)
    @Operation(summary = "Logout Okta")
    public 
    ResponseEntity<RestObject> 
	logout(@RequestHeader(value="requestId", defaultValue = "") String requestId,
           final HttpSession session) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		
        // retrieve access token
        TokenResponse tokenResponse =
                (TokenResponse) session.getAttribute("tokenResponse");

        if (tokenResponse != null) {
			IDXAuthenticationWrapper idxAuthenticationWrapper 
			= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());
            idxAuthenticationWrapper.revokeToken(TokenType.ACCESS_TOKEN, o.getAccessToken());
        }

        // invalidate session
        session.invalidate();
        return RestObject.retOKWithPayload(new GenericResponse("OK") , requestId, methodName);
    }
	
	
	
	
	
	
	/**
     * Handle forgot password (password recovery) functionality.
     *
     * @param username the username
     * @param session the session
     * @return the verify view (if password recovery operation is successful),
     * else the forgot password page with errors.
    */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/forgot-password", method = RequestMethod.POST)
    @Operation(summary = "Forgot password")
    public 
    ResponseEntity<RestObject> 
	forgotPassword(@RequestHeader(value="requestId", defaultValue = "") String requestId,
                   @RequestParam("username") final String username,
                   final HttpSession session) {
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		
		ModelAndView modelAndView = null;
       
		IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());
		
        ProceedContext proceedContext = Util.getProceedContextFromSession(session);

        AuthenticationResponse authenticationResponse =
                idxAuthenticationWrapper.recoverPassword(username, proceedContext);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            modelAndView = new ModelAndView("forgot-password");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            
            //return modelAndView;
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        modelAndView = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView), requestId, methodName);
    }
    
	
	
	/**
     * Handle authenticator selection during authentication.
     *
     * @param authenticatorType the authenticatorType
     * @param session the session
     * @param action the submit or cancel action from form post
     * @return select authenticator view or select factor view or error view
    */
     
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/select-authenticator", method = RequestMethod.POST)
    @Operation(summary = "Select Authenticator")
    public 
    ResponseEntity<RestObject> 
	selectAuthenticator(@RequestHeader(value="requestId", defaultValue = "") String requestId,
                        @RequestParam("authenticator-type") final String authenticatorType,
                        @RequestParam(value = "action") final String action,
                        final HttpSession session) {

		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        AuthenticationResponse authenticationResponse = null;
        Authenticator foundAuthenticator = null;

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
       
		IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());
		authenticationResponse = idxAuthenticationWrapper.skipAuthenticatorEnrollment(proceedContext);
        if ("skip".equals(action)) {
            ModelAndView modelAndView =  responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        @SuppressWarnings("unchecked")
		List<Authenticator> authenticators = (List<Authenticator>) session.getAttribute("authenticators");

        if ("webauthn".equals(authenticatorType)) {
            ModelAndView modelAndView;

            Optional<Authenticator> authenticatorOptional =
                    authenticators.stream().filter(auth -> auth.getType().equals(authenticatorType)).findFirst();
            String authId = authenticatorOptional.get().getId();

            AuthenticationResponse enrollResponse = idxAuthenticationWrapper.enrollAuthenticator(proceedContext, authId);

            Util.updateSession(session, enrollResponse.getProceedContext());

            String webauthnCredentialId = enrollResponse.getWebAuthnParams().getWebauthnCredentialId();

            if (webauthnCredentialId != null) {
                modelAndView = new ModelAndView("select-webauthn-authenticator");
                modelAndView.addObject("title", "Select Webauthn Authenticator");
                modelAndView.addObject("webauthnCredentialId", webauthnCredentialId);
                modelAndView.addObject("challengeData", enrollResponse.getWebAuthnParams()
                        .getCurrentAuthenticator().getValue().getContextualData().getChallengeData());
            } else {
                modelAndView = new ModelAndView("enroll-webauthn-authenticator");
                modelAndView.addObject("title", "Enroll Webauthn Authenticator");
                modelAndView.addObject("currentAuthenticator",
                        enrollResponse.getWebAuthnParams().getCurrentAuthenticator());
            }
            //return modelAndView;
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        if ("okta_verify".equals(authenticatorType)) {
            ModelAndView modelAndView;

            Optional<Authenticator> authenticatorOptional = authenticators.stream()
                    .filter(auth -> auth.getType().equals(authenticatorType)).findFirst();
            Assert.isTrue(authenticatorOptional.isPresent(), "Authenticator not found");

            //Looking for QRCODE factor
            Optional<Authenticator.Factor> factorOptional = authenticatorOptional.get().getFactors().stream()
                    .filter(x -> "QRCODE".equals(x.getLabel())).findFirst();
            Assert.isTrue(factorOptional.isPresent(), "Authenticator not found");

            authenticationResponse = idxAuthenticationWrapper.selectFactor(proceedContext, factorOptional.get());
            Util.setProceedContextForPoll(session, authenticationResponse.getProceedContext());

            List<Authenticator.Factor> factors = authenticatorOptional.get().getFactors().stream()
                    .filter(x -> !"QRCODE".equals(x.getLabel())).collect(Collectors.toList());

            modelAndView = new ModelAndView("setup-okta-verify");
            modelAndView.addObject("qrCode", authenticationResponse.getContextualData().getQrcode().getHref());
            modelAndView.addObject("channelName", "qrcode");
            modelAndView.addObject("factors", factors);
            modelAndView.addObject("authenticatorId", authenticatorOptional.get().getId());
            modelAndView.addObject("pollTimeout", authenticationResponse.getProceedContext().getRefresh());
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        if ("Security Question".equals(authenticatorType)) {
            Optional<Authenticator> authenticatorOptional = authenticators.stream()
                    .filter(auth -> auth.getType().equals("security_question")).findFirst();
            Assert.isTrue(authenticatorOptional.isPresent(), "Authenticator not found");

            String authId = authenticatorOptional.get().getId();

            AuthenticationResponse enrollResponse = idxAuthenticationWrapper.enrollAuthenticator(proceedContext, authId);

            Util.updateSession(session, enrollResponse.getProceedContext());
        }

        for (Authenticator authenticator : authenticators) {
            if (authenticatorType.equals(authenticator.getType())) {
                foundAuthenticator = authenticator;

                if (foundAuthenticator.getFactors().size() == 1) {
                    authenticationResponse = idxAuthenticationWrapper.selectAuthenticator(proceedContext, authenticator);
                    if (authenticationResponse.getContextualData() != null) {
                        session.setAttribute("totp", authenticationResponse.getContextualData());
                    } else {
                        session.removeAttribute("totp");
                    }
                } else {
                    // user should select the factor in a separate view
                    ModelAndView modelAndView = new ModelAndView("select-factor");
                    modelAndView.addObject("title", "Select Factor");
                    modelAndView.addObject("authenticatorId", foundAuthenticator.getId());
                    modelAndView.addObject("factors", foundAuthenticator.getFactors());
                    return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
                }
            }
        }

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("select-authenticator");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        ModelAndView terminalTransition = responseHandler.handleTerminalTransitions(authenticationResponse, session, o.getIssuer());
        if (terminalTransition != null) {
            return RestObject.retOKWithPayload(new OktaModelAndView(terminalTransition) , requestId, methodName);
        }

        return switch (authenticationResponse.getAuthenticationStatus()) {
            case AWAITING_AUTHENTICATOR_VERIFICATION_DATA -> {
                ModelAndView m1 = responseHandler.verifyForm();
                yield RestObject.retOKWithPayload(new OktaModelAndView(m1), requestId, methodName);
            }
            case AWAITING_AUTHENTICATOR_ENROLLMENT, AWAITING_AUTHENTICATOR_ENROLLMENT_DATA -> {
                assert foundAuthenticator != null;
                ModelAndView m2 = responseHandler.registerVerifyForm(foundAuthenticator, authenticationResponse);
                yield RestObject.retOKWithPayload(new OktaModelAndView(m2), requestId, methodName);
            }
            case AWAITING_POLL_ENROLLMENT -> {
                ModelAndView m3 = responseHandler.setupOktaVerifyForm(session);
                yield RestObject.retOKWithPayload(new OktaModelAndView(m3), requestId, methodName);
            }
            default -> {
                ModelAndView m4 = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
                yield RestObject.retOKWithPayload(new OktaModelAndView(m4), requestId, methodName);
            }
        };
    }
	
	
	/**
     * Handle factor selection during authentication.
     *
     * @param authenticatorId the authenticator ID of selected authenticator
     * @param mode the sms or voice factor mode
     * @param session the session
     * @return the view associated with authentication response.
     */
    
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/select-factor", method = RequestMethod.POST)
    @Operation(summary = "Select Factor")
    public 
    ResponseEntity<RestObject>  
    selectFactor(@RequestHeader(value="requestId", defaultValue = "") String requestId,
                 @RequestParam("authenticatorId") String authenticatorId,
                 @RequestParam("mode") final String mode,
                 final HttpSession session) {

    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);

        @SuppressWarnings("unchecked")
		List<Authenticator> authenticators = (List<Authenticator>) session.getAttribute("authenticators");

        Authenticator foundAuthenticator = null;
        for (Authenticator auth : authenticators) {
            if (auth.getId().equals(authenticatorId)) {
                foundAuthenticator = auth;
            }
        }

        Assert.notNull(foundAuthenticator, "Authenticator not found");

        AuthenticationResponse authenticationResponse = null;
        Authenticator.Factor foundFactor = null;

        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        for (Authenticator.Factor factor : foundAuthenticator.getFactors()) {
            if (factor.getMethod().equals(mode)) {
                foundFactor = factor;
                authenticationResponse = idxAuthenticationWrapper.selectFactor(proceedContext, foundFactor);
                Optional.ofNullable(authenticationResponse.getContextualData())
                        .map(ContextualData::getQrcode)
                        .map(QrCode::getHref)
                        .ifPresent(qrCode -> {
                            session.setAttribute("qrCode", qrCode);
                            session.setAttribute("channelName", "qrcode");
                        });
                if ("totp".equals(foundFactor.getMethod())) {
                    session.setAttribute("totp", "totp");
                }
                break;
            }
        }

        Assert.notNull(foundFactor, "Factor not found");

        ModelAndView terminalTransition = responseHandler.handleTerminalTransitions(authenticationResponse, session, o.getIssuer());
        if (terminalTransition != null) {
            return RestObject.retOKWithPayload(new GenericResponse("OK") , requestId, methodName);
        }

        switch (authenticationResponse.getAuthenticationStatus()) {
            case AWAITING_AUTHENTICATOR_VERIFICATION_DATA:
            	terminalTransition = responseHandler.verifyForm();
            case AWAITING_AUTHENTICATOR_ENROLLMENT:
            case AWAITING_AUTHENTICATOR_ENROLLMENT_DATA:
            	terminalTransition = responseHandler.registerVerifyForm(foundFactor, authenticationResponse);
            case AWAITING_CHANNEL_DATA_ENROLLMENT:
            	terminalTransition =  responseHandler.oktaVerifyViaChannelDataForm(foundFactor, session);
            case AWAITING_POLL_ENROLLMENT:
            	terminalTransition =  responseHandler.setupOktaVerifyForm(session);
            case AWAITING_CHALLENGE_POLL:
            	terminalTransition =  responseHandler.oktaVerifyChallenge(authenticationResponse);
            default:
            	terminalTransition =  responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
                
        }
        return RestObject.retOKWithPayload(new OktaModelAndView(terminalTransition) , requestId, methodName);
        
    }
    
    
    
    
    
    /**
     * Handle authenticator verification functionality.
     *
     * @param code                  the verification code
     * @param securityQuestion      the security question (custom case)
     * @param securityQuestionKey   the security question key
     * @param session               the session
     * @return the view associated with authentication response.
     */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/verify-channel-data", method = RequestMethod.POST)
    @Operation(summary = "Select Factor")
    public 
    ResponseEntity<RestObject> 
    verify(@RequestHeader(value="requestId", defaultValue = "") String requestId,
           @RequestParam("code") final String code,
           @RequestParam(value = "security_question", required = false) final String securityQuestion,
           @RequestParam(value = "security_question_key", required = false) final String securityQuestionKey,
           final HttpSession session) {
    	
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());


        AuthenticationResponse authenticationResponse;
        if (!Strings.isEmpty(securityQuestionKey)) {
            authenticationResponse = idxAuthenticationWrapper
                    .verifyAuthenticator(proceedContext, new VerifyAuthenticatorAnswer(code, null, securityQuestionKey));
        } else if (!Strings.isEmpty(securityQuestion)) {
            authenticationResponse = idxAuthenticationWrapper
                    .verifyAuthenticator(proceedContext, new VerifyAuthenticatorAnswer(code, securityQuestion, "custom"));
        } else if ("totp".equals(String.valueOf(session.getAttribute("totp")))) {
            authenticationResponse = idxAuthenticationWrapper
                    .verifyAuthenticator(proceedContext, new VerifyChannelDataOptions("totp", code));
        } else {
            VerifyAuthenticatorOptions verifyAuthenticatorOptions = new VerifyAuthenticatorOptions(code);
            authenticationResponse = idxAuthenticationWrapper
                    .verifyAuthenticator(proceedContext, verifyAuthenticatorOptions);
        }

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("verify");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView(modelAndView) , requestId, methodName);
        }

        if (session.getAttribute("isPasswordRequired") != null) {
            session.removeAttribute("isPasswordRequired");
        }

        ModelAndView terminalTransition = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView(terminalTransition) , requestId, methodName);
    }
    
    
    
    /**
     * Handle poll functionality.
     *
     * @param session the session
     * @return the view associated with authentication response.
    */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/poll", method = RequestMethod.GET)
    @Operation(summary = "Handle Poll functionality")
    public 
    ResponseEntity<RestObject> 
    pollResults(@RequestHeader(value="requestId", defaultValue = "") String requestId,
                final HttpSession session) {
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        PollResults pollResults = new PollResults();
        ProceedContext proceedContext = Util.getProceedContextForPoll(session);
        if (proceedContext == null) {
            proceedContext = Util.getProceedContextFromSession(session);
        }
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        AuthenticationResponse authenticationResponse = idxAuthenticationWrapper.poll(proceedContext);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            pollResults.setErrors(authenticationResponse.getErrors());
        }
        pollResults.setStatus(authenticationResponse.getAuthenticationStatus());

        if (authenticationResponse.getAuthenticationStatus() == AuthenticationStatus.SUCCESS) {
            responseHandler.handleTerminalTransitions(authenticationResponse, session, o.getIssuer());
        }

        return RestObject.retOKWithPayload(pollResults , requestId, methodName);
    }
    
    
     /**
     * Handle Okta verify functionality.
     *
     * @param session the session
     * @return the view associated with authentication response.
     */

    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/poll", method = RequestMethod.POST)
    @Operation(summary = "Handle Okta verify functionality")
    public 
    ResponseEntity<RestObject>  
    poll(@RequestHeader(value="requestId", defaultValue = "") String requestId,
         final HttpSession session) {
    	
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		IDXAuthenticationWrapper idxAuthenticationWrapper 
			= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

		 
        ProceedContext proceedContext = Util.getProceedContextForPoll(session);
        if (proceedContext == null) {
            proceedContext = Util.getProceedContextFromSession(session);
        }
        AuthenticationResponse authenticationResponse = idxAuthenticationWrapper.poll(proceedContext);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        ModelAndView modelAndView =  responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
    }
    
    
    
    /**
     * Handle webauthn authenticator verification functionality.
     *
     * @param webauthnRequest web authn request object
     * @param session the session
     * @return the view associated with authentication response.
     */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/verify-webauthn", method = RequestMethod.POST)
    @Operation(summary = "Handle webauthn authenticator verification functionality")
    public 
    ResponseEntity<RestObject>   
    verifyWebAuthn(@RequestBody final WebAuthnRequest webauthnRequest,
                   @RequestHeader(value="requestId", defaultValue = "") String requestId,
                   final HttpSession session) {

    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        AuthenticationResponse authenticationResponse = idxAuthenticationWrapper.verifyWebAuthn(
                        proceedContext, webauthnRequest);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("verify-webauthn");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }
        ModelAndView modelAndView =responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        
    }
    
    
    /**
     * Handle change password functionality.
     *
     * @param newPassword the new password
     * @param confirmNewPassword the confirmation of the new password
     * @param session the session
     * @return the view associated with authentication response.
     */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/register-password", method = RequestMethod.POST)
    @Operation(summary = "Handle change password functionality")
    public 
    ResponseEntity<RestObject> 
    registerPassword(@RequestParam("new-password") final String newPassword,
                     @RequestParam("confirm-new-password") final String confirmNewPassword,
                     @RequestHeader(value="requestId", defaultValue = "") String requestId,
                     final HttpSession session) {

    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        if (!newPassword.equals(confirmNewPassword)) {
            ModelAndView mav = new ModelAndView("register-password");
            mav.addObject("errors", "Passwords do not match");
            return RestObject.retOKWithPayload(new OktaModelAndView( mav) , requestId, methodName);
        }

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        VerifyAuthenticatorOptions verifyAuthenticatorOptions = new VerifyAuthenticatorOptions(newPassword);
        AuthenticationResponse authenticationResponse =
                idxAuthenticationWrapper.verifyAuthenticator(proceedContext, verifyAuthenticatorOptions);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("register-password");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        ModelAndView modelAndView = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
    }
    
    
    /**
     * Handle new user registration functionality.
     *
     * @param userProfileAttributes string array for user profile attributes from register form
     * @param password the password (optional)
     * @param session the session
     * @return the enroll authenticators view.
     */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/register", method = RequestMethod.POST)
    @Operation(summary = "Handle new user registration functionality")
    public 
    ResponseEntity<RestObject>  
    register(@RequestParam(value = "userProfileAttribute[]") final String[] userProfileAttributes,
             @RequestParam(value = "password", required = false) final char[] password,
             @RequestHeader(value="requestId", defaultValue = "") String requestId,
             final HttpSession session) {
    	
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

		IDXAuthenticationWrapper idxAuthenticationWrapper 
			= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

		 
        AuthenticationResponse beginResponse = idxAuthenticationWrapper.begin(constructRequestContext());
        if (responseHandler.needsToShowErrors(beginResponse)) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errors", beginResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }
        ProceedContext beginProceedContext = beginResponse.getProceedContext();

        AuthenticationResponse newUserRegistrationResponse = idxAuthenticationWrapper.fetchSignUpFormValues(beginProceedContext);
        if (responseHandler.needsToShowErrors(newUserRegistrationResponse)) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errors", newUserRegistrationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        if (responseHandler.needsToShowErrors(newUserRegistrationResponse)) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("errors", newUserRegistrationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( mav) , requestId, methodName);
        }

        UserProfile userProfile = new UserProfile();

        Optional<FormValue> userProfileFormValue = newUserRegistrationResponse.getFormValues()
                    .stream()
                    .filter(x -> x.getName().equals("userProfile"))
                    .findFirst();

        if (userProfileFormValue.isEmpty()) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errors", "Unknown error occurred!");
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        int i = 0;
        for (FormValue value: userProfileFormValue.get().form().getValue()) {
            //Build the user profile
            userProfile.addAttribute(value.getName(), userProfileAttributes[i]);
            i++;
        }

        ProceedContext proceedContext = newUserRegistrationResponse.getProceedContext();

        Credentials credentials = new Credentials();
        credentials.setPasscode(password);

        AuthenticationResponse authenticationResponse =
                idxAuthenticationWrapper.register(proceedContext, userProfile, credentials);

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        ModelAndView modelAndView = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
    }
    
    
    
   /**
     * Fetch the factor associated with factor method.
     *
     * @param session the http session
     * @param method  the factor method
     * @return the factor associated with the supplied factor method.
     * @throws IllegalStateException if factor could not be found.
     */
    private 
    Authenticator.Factor 
    getPhoneFactorFromMethod(	final HttpSession session,
    							final String method) {

        @SuppressWarnings("unchecked")
		List<Authenticator> authenticators = (List<Authenticator>) session.getAttribute("authenticators");
        for (Authenticator authenticator : authenticators) {
            for (Authenticator.Factor factor : authenticator.getFactors()) {
                if (authenticator.getLabel().equals("Phone") && factor.getMethod().equals(method)) {
                    return factor;
                }
            }
        }
        throw new IllegalStateException("Factor not found: " + method);
    }
    
    /**
     * Handle phone authenticator enrollment functionality.
     *
     * @param phone the phone number
     * @param mode the delivery mode - sms or voice
     * @param session the session
     * @return the view associated with authentication response.
     */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/register-phone", method = RequestMethod.POST)
    @Operation(summary = "Handle phone authenticator enrollment functionality")
    public 
    ResponseEntity<RestObject> 
    registerPhone(@RequestParam("phone") final String phone,
                  @RequestParam(value = "mode", required = false) final String mode,
                  @RequestHeader(value="requestId", defaultValue = "") String requestId,
                  final HttpSession session) {
    	
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);
        if (!Strings.hasText(phone)) {
            ModelAndView mav = new ModelAndView("register-phone");
            mav.addObject("errors", "Phone is required");
            return RestObject.retOKWithPayload(new OktaModelAndView( mav) , requestId, methodName);
        }

        if (!Strings.hasText(mode)) {
            ModelAndView modelAndView = new ModelAndView("select-phone-factor");
            modelAndView.addObject("phone", phone);
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        AuthenticationResponse authenticationResponse =
                idxAuthenticationWrapper.submitPhoneAuthenticator(proceedContext,
                        phone, getPhoneFactorFromMethod(session, mode));

        if (responseHandler.needsToShowErrors(authenticationResponse)) {
            ModelAndView modelAndView = new ModelAndView("register-phone");
            modelAndView.addObject("mode", mode);
            modelAndView.addObject("errors", authenticationResponse.getErrors());
            return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
        }

        ModelAndView terminalTransition = responseHandler.handleTerminalTransitions(authenticationResponse, session, o.getIssuer());
        if (terminalTransition != null) {
        	return RestObject.retOKWithPayload(new OktaModelAndView( terminalTransition) , requestId, methodName);
        }

        ModelAndView modelAndView = responseHandler.verifyForm();
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
    }

    
    /**
     * Handle webauthn authenticator enrollment functionality.
     *
     * @param webauthnRequest body
     * @param session         the session
     * @return the view associated with authentication response.
    */
    @CrossOrigin(origins = "*")
	@RequestMapping(value = "/okta/enroll-webauthn", method = RequestMethod.POST)
    @Operation(summary = "Handle webauthn authenticator enrollment functionality")
    public 
    ResponseEntity<RestObject> 
    enrollWebauthn(@RequestBody final WebAuthnRequest webauthnRequest,
                   final HttpSession session,
                   @RequestHeader(value="requestId", defaultValue = "") String requestId) {
    	String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        requestId = StringUtils.generateRequestId(requestId);

        ProceedContext proceedContext = Util.getProceedContextFromSession(session);
        IDXAuthenticationWrapper idxAuthenticationWrapper 
		= new IDXAuthenticationWrapper(o.getIssuer(), o.getClientId(), o.getClientSecret(), o.getScopes(), o.getRedirectUrl());

        AuthenticationResponse authenticationResponse = idxAuthenticationWrapper.verifyWebAuthn(
                proceedContext, webauthnRequest);

        ModelAndView modelAndView = responseHandler.handleKnownTransitions(authenticationResponse, session, o.getIssuer());
        return RestObject.retOKWithPayload(new OktaModelAndView( modelAndView) , requestId, methodName);
    }
    

    
}
	
	
	
	
	
	

