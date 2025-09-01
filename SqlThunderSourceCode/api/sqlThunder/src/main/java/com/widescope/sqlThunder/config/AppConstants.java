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

package com.widescope.sqlThunder.config;


import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties()
public class AppConstants {
	
	@Value("${spring.profiles.active}")
	private String spring_profiles_active;
	public String getSpringProfilesActive() {	return spring_profiles_active; }
	public void setSpringProfilesActive(final String spring_profiles_active) { this.spring_profiles_active = spring_profiles_active; }


	@Value("${logging.file.name}")
	private String logging_file;
	public String getLoggingFile() {	return logging_file; }
	public void setLoggingFile(final String logging_file) { this.logging_file = logging_file; }
	

	@Value("${active.repo.dbname}")
	private String active_repo_dbname;  
	public String getActiveRepo() {	return active_repo_dbname; }
	public void setActiveRepo(final String active_repo_dbname) { this.active_repo_dbname = active_repo_dbname; }

	/////////////////////////// Application Specific /////////////////////////////////////////////////////////////
	@NotBlank
	@Value("${server.port}")
	private String server_port;
	public String getServerPort() {	return server_port; }
	public void setServerPort(String server_port) { this.server_port = server_port; }
	
	
	@NotBlank
	@Value("${server.servlet.context-path}")
	private String server_servlet_context_path;
	public String getServerServletContextPath() {	return server_servlet_context_path; }
	public void setServerServletContextPath(String server_servlet_context_path) { this.server_servlet_context_path = server_servlet_context_path; }
	




	/*Built-in Accounts*/
	@NotBlank
	@Value("${user}")
	private String user;
	public String getUser( ) { return user; }
	public void setUser(String user) { this.user=user; }
	
	@NotBlank
	@Value("${userPasscode}")
	private String userPasscode;
	public String getUserPasscode( ) { return userPasscode; }
	public void setUserPasscode(String userPasscode) { this.userPasscode=userPasscode; }

	@NotBlank
	@Value("${admin}")
	private String admin;
	public String getAdmin( ) { return admin; }
	public void setAdmin(String admin) { this.admin=admin; }
	
	@NotBlank
	@Value("${adminPasscode}")
	private String adminPasscode;
	public String getAdminPasscode( ) { return adminPasscode; }
	public void setAdminPasscode(String adminPasscode) { this.adminPasscode=adminPasscode; }

	@NotBlank
	@Value("${superUser}")
	private String superUser;
	public String getSuperUser( ) { return superUser; }
	public void setSuperUser(String superUser) { this.superUser=superUser; }

	@NotBlank
	@Value("${superPasscode}")
	private String superPasscode;
	public String getSuperPasscode( ) { return superPasscode; }
	public void setSuperPasscode(String superPasscode) { this.superPasscode=superPasscode; }

	@NotBlank
	@Value("${testUser}")
	private String testUser;
	public String getTestUser( ) { return testUser; }
	public void setTestUser(String testUser) { this.testUser=testUser; }

	@NotBlank
	@Value("${testPasscode}")
	private String testPasscode;
	public String getTestPasscode( ) { return testPasscode; }
	public void setTestPasscode(String testPassword) { this.testPasscode=testPasscode; }

	@NotBlank
	@Value("${mobileUser}")
	private String mobileUser;
	public String getMobileUser( ) { return mobileUser; }
	public void setMobileUser(String mobileUser) { this.mobileUser=mobileUser; }

	@NotBlank
	@Value("${mobilePassword}")
	private String mobilePassword;
	public String getMobilePassword( ) { return mobilePassword; }
	public void setMobilePassword(String mobilePassword) { this.mobilePassword=mobilePassword; }

	
	@NotBlank
	@Value("${auth.implementation}")
	private int authImplementation;
	public int getAuthImplementation( ) { return authImplementation; }
	public void setAuthImplementation(int authImplementation) { this.authImplementation=authImplementation; }
	
	@NotBlank
	@Value("${userSessionCacheLocation}")
	private int userSessionCacheLocation;
	public int getUserSessionCacheLocation( ) { return userSessionCacheLocation; }
	public void setUserSessionCacheLocation(int userSessionCacheLocation) { this.userSessionCacheLocation=userSessionCacheLocation; }

	@NotBlank
	@Value("${isSwagger}")
	private boolean isSwagger;
	public boolean getIsSwagger( ) { return isSwagger; }


	@NotBlank
	@Value("${instanceType}")
	private String instanceType;
	public String getInstanceType() { return instanceType; }

		
	////////////////////////////// Scripting ///////////////////////////////////////////////////////////////////////

	@Value("${storage.path}")
	private String storagePath;
	public String getStoragePath() { return storagePath; }


	@Value("${temp.path}")
	private String tempPath;
	public String getTempPath() {	return tempPath; }



	@NotBlank
	@Value("${cacheFreeMemory}")
	private long cacheFreeMemory;
	public long getCacheFreeMemory( ) { return cacheFreeMemory; }



	@NotBlank
	@Value("${logging.thread.sleep}")
	private long logging_thread_sleep;
	public long getLoggingThreadSleep( ) { return logging_thread_sleep; }
	public void setLoggingThreadSleep(long logging_thread_sleep) { this.logging_thread_sleep=logging_thread_sleep; }

	@NotBlank
	@Value("${server.ssl.enabled}")
	private boolean server_ssl_enabled;
	public boolean getServerSslEnabled( ) { return server_ssl_enabled; }




	@NotBlank
	@Value("${tcp.server.port}")
	private String tcp_server_port;
	public String getTcpServerPort( ) { return tcp_server_port; }

	@NotBlank
	@Value("${server.ip.static}")
	private String server_ip_static;
	public String getServerIpStatic( ) { return server_ip_static; }

	@NotBlank
	@Value("${application.owner}")
	private String applicationOwner;
	public String getApplicationOwner( ) { return applicationOwner; }

	@NotBlank
	@Value("${maintenance.thread.sleep}")
	private String maintenanceThreadSleep;
	public String getMaintenanceThreadSleep( ) { return maintenanceThreadSleep; }

	@NotBlank
	@Value("${encryptionKey}")
	private String encryptionKey;
	public String getEncryptionKey( ) { return encryptionKey; }



}
