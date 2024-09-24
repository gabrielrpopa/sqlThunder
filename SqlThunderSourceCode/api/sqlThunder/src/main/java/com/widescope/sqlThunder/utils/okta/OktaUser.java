package com.widescope.sqlThunder.utils.okta;

import com.widescope.rest.RestInterface;

public class OktaUser implements RestInterface{

	private com.okta.sdk.resource.user.User user;
	
	public OktaUser(final com.okta.sdk.resource.user.User user) {
		this.setUser(user);
	}

	public com.okta.sdk.resource.user.User getUser() {
		return user;
	}

	public void setUser(com.okta.sdk.resource.user.User user) {
		this.user = user;
	}
	
}
