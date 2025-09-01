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


package com.widescope.sqlThunder.utils.user;



import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


/**
 * 
 * @author Gabriel Popa
 * @since   August 2020
 */

public class User implements RestInterface {
	
	private long id;
	public long getId() {	return id; }
	public void setId(final long id) { this.id = id; }
	
	private String userType;
	public String getUserType() {	return userType; }
	public void setUserType(final String userType) { this.userType = userType; }
	
	private String user;
	public String getUser() {	return user; }
	public void setUser(final String user) { this.user = user; }
	
	private String password;
	public String getPassword() {	return password; }
	public void setPassword(final String password) { this.password = password; }
	
	private String firstName;
	public String getFirstName() {	return firstName; }
	public void setFirstName(final String firstName) { this.firstName = firstName; }
	
	private String lastName;
	public String getLastName() {	return lastName; }
	public void setLastName(final String lastName) { this.lastName = lastName; }
	
	private String userDescription;
	public String getUserDescription() {	return userDescription; }
	public void setUserDescription(final String userDescription) { this.userDescription = userDescription; }
	
	private String email;
	public String getEmail() {	return email; }
	public void setEmail(final String email) { this.email = email; }
	
	private long department;
	public long getDepartment() {	return department; }
	public void setDepartment(final long department) { this.department = department; }
	
	private long title;
	public long getTitle() {	return title; }
	public void setTitle(final long title) { this.title = title; }
	
	private long manager;
	public long getManager() {	return manager; }
	public void setManager(final long manager) { this.manager = manager; }
	
	private String characteristic;
	public String getCharacteristic() {	return characteristic; }
	public void setCharacteristic(final String characteristic) { this.characteristic = characteristic; }
	
	private String active;
	public String getActive() {	return active; }
	public void setActive(final String active) { this.active = active; }
	
	private String authenticated;
	public String getAuthenticated() {	return authenticated; }
	public void setAuthenticated(final String authenticated) { this.authenticated = authenticated; }
	
	private String session;
	public String getSession() {	return session; }
	public void setSession(final String session) { this.session = session; }

	private String avatarUrl;
	public String getAvatarUrl() {	return avatarUrl; }
	public void setAvatarUrl(final String avatarUrl) { this.avatarUrl = avatarUrl; }
	
	
	
	public User() {
		this.id = -1;
		this.userType = null;
		this.user = null;
		this.password = null;
		this.firstName = null;
		this.lastName = null;
		this.userDescription = null;
		this.email = null;
		this.department = 0;
		this.title = 0;
		this.manager = 0;
	 	this.characteristic = null;
		this.active = null;
		this.authenticated = null;
		this.session = null;
		this.avatarUrl = "";
	}
	
	
	public User(final String userType,
			 	final String user,
			 	final String password,
			 	final String firstName,
			 	final String lastName,
			 	final String email,
			 	final long department,
			 	final long title,
			 	final long manager,
				String characteristic,
			 	final String userDescription,
			 	final String active) {
	this.id = -1;;
	this.userType = userType;
	this.user =user;
	this.password = password;
	this.firstName = firstName;
	this.lastName = lastName;
	this.userDescription = userDescription;
	this.email = email;
	this.department = department;
	this.title = title;
	this.manager = manager;
 	this.characteristic = characteristic;
	this.active = active;
	this.authenticated = "N";
	this.session = "N";
	this.avatarUrl = "";
	
}
	
	

	public User(	final long id,
					final String userType,
				 	final String user,
				 	final String password,
				 	final String firstName,
				 	final String lastName,
				 	final String email,
				 	final long department,
				 	final long title,
				 	final long manager,
					String characteristic,
				 	final String userDescription,
				 	final String active ) {
		this.id = id;
		this.userType = userType;
		this.user =user;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.userDescription = userDescription;
		this.title = title;
		this.manager = manager;
		this.department = department;
		this.characteristic= characteristic;
		this.active = active;
		this.avatarUrl = "";
		
	}
	
	
	
	
	public User(final long id,
				 final String userType,
				 final String user,
				 final String password,
				 final String firstName,
				 final String lastName,
				 final String email,
				 final int department,
				 final int title,
				 final int manager,
				 final String characteristic,
				 final String userDescription,
				 final String active,
				 final String authenticated,
				 final String session ) {
		this.id = id;
		this.userType = userType;
		this.user =user;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.department = department;
		this.title = title;
		this.manager = manager;
	 	this.characteristic = characteristic;
		this.userDescription = userDescription;
		this.active = active;
		this.authenticated = authenticated;
		this.session = session;
		this.avatarUrl = "";
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	
}
