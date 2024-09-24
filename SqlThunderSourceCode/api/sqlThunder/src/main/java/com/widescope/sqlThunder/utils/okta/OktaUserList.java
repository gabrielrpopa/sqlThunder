package com.widescope.sqlThunder.utils.okta;

import com.okta.sdk.resource.user.UserList;
import com.widescope.rest.RestInterface;

public class OktaUserList implements RestInterface {

	private UserList userList;
	
	public OktaUserList(final UserList userList) {
		this.setUserList(userList);
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}
	
}
