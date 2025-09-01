package com.widescope.sqlThunder.utils.okta;

import com.okta.idx.sdk.api.model.AuthenticationStatus;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.List;


public class PollResults implements RestInterface{

	/**
     * Contains errors after poll request.
     */
    private List<String> errors;

    /**
     * Contains AuthenticationStatus after poll request.
     */
    private AuthenticationStatus status;

    /**
     * Return errors after poll request.
     *
     * @return List of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Set errors after poll request.
     *
     * @param listErrors list of errors
     */
    public void setErrors(List<String> listErrors) {
        this.errors = listErrors;
    }

   /**
     * Get AuthenticationStatus after poll request.
     *
     * @return AuthenticationStatus value
     */
    public AuthenticationStatus getStatus() {
        return status;
    }

    /**
     * Set AuthenticationStatus after poll request.
     *
     * @param authenticationStatus is AuthenticationStatus value after poll request
     */
    public void setStatus(AuthenticationStatus authenticationStatus) {
        this.status = authenticationStatus;
    }
     
  
}