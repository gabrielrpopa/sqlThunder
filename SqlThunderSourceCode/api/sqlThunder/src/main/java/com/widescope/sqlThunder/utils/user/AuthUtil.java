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

import java.util.ArrayList;
import java.util.List;

import com.widescope.logging.AppLogger;
import com.widescope.rdbmsRepo.database.DbConnectionInfo;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.sqlThunder.StaticApplicationProperties;
import com.widescope.sqlThunder.utils.user.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.widescope.sqlThunder.config.AppConstants;
import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.security.SHA512Hasher;



@Component
public class AuthUtil {
	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	private static String localSession = "";



	@Autowired
	private AppConstants appConstants;
	
	@Autowired
	private InternalUserDb internalUserDb;
	
	
	public static void setLocalSession(String localSession) {
		AuthUtil.localSession = localSession;
	}
	
	public String getLocalSession() {
		return AuthUtil.localSession;
	}
	
	public boolean isSessionAuthenticated(String user, String session) {
		boolean isAuthenticated = false;
		
		if(0 == appConstants.getUserSessionCacheLocation()) { // internal cache
			if(InternalUserDb.loggedUsers.containsKey(session))	{
				UserShort us = InternalUserDb.loggedUsers.get(session);
				long currentTimeStamp = DateTimeUtils.millisecondsSinceEpoch();
				if(currentTimeStamp - us.getTimeStamp() > 1000 * 60 && user.compareTo(us.getUser()) == 0) {
					return false;
				}
				assert user != null;
				if(user.compareTo(us.getUser()) == 0) {
					InternalUserDb.loggedUsers.get(session).setTimeStamp(DateTimeUtils.millisecondsSinceEpoch());
					return true;
				}
			}
			return false;
		}
		else if(1 == appConstants.getUserSessionCacheLocation()) { //  Native Cache Server
			/*TO-BE-IMPLEMENTED*/
			return true;
		}

		return isAuthenticated;
	}

	public static boolean isSessionAuthenticated_(String user, String session) {
		boolean isAuthenticated = false;

		if(0 == StaticApplicationProperties.userSessionCacheLocation) { // internal cache
			if(InternalUserDb.loggedUsers.containsKey(session))	{
				UserShort us = InternalUserDb.loggedUsers.get(session);
				long currentTimeStamp = DateTimeUtils.millisecondsSinceEpoch();
				if(currentTimeStamp - us.getTimeStamp() > 1000 * 60 && user.compareTo(us.getUser()) == 0) {
					return false;
				}
				assert user != null;
				if(user.compareTo(us.getUser()) == 0) {
					InternalUserDb.loggedUsers.get(session).setTimeStamp(DateTimeUtils.millisecondsSinceEpoch());
					return true;
				}
			}
			return false;
		}
		else if(1 == StaticApplicationProperties.userSessionCacheLocation) { //  Native Cache Server
			/*TO-BE-IMPLEMENTED*/
			return true;
		}

		return isAuthenticated;


	}


	public boolean isUserSession(String user) {
		boolean isAuthenticated = false;

		if(0 == appConstants.getUserSessionCacheLocation()) { // internal cache
			return InternalUserDb.loggedUsers.values().stream().anyMatch(u-> u.getUser().compareToIgnoreCase(user) == 0);
		}
		else if(1 == appConstants.getUserSessionCacheLocation()) { //  Native Cache Server
			/*TO-BE-IMPLEMENTED*/
			return true;
		}

		return isAuthenticated;
	}
	
	
	public void removeSession(String user, String session) {
		InternalUserDb.loggedUsers.remove(session);
    }
		
	

	
	
	public UserShort isUserAuthenticated(String session) {
		return InternalUserDb.loggedUsers.get(session);
	}





	public User isUserAuthenticated(String user,
									String userPasscode,
									String authBody,
									String baseUrl,
									String pns,
									String deviceToken) {
		User userObject;
		String session = StaticUtils.generateSecureStringN(512);
		InternalUserDb internalUserDb = new InternalUserDb();
		List<User> u;
		try {
			String hashedPassword = null;
			if(userPasscode != null && !userPasscode.isBlank() && !userPasscode.isEmpty()){
				hashedPassword = SHA512Hasher.hash(userPasscode);
			}

			u = internalUserDb.authUser(user, hashedPassword);
		} catch (Exception e) {
			return null;
		}
		if(u.size() == 1) {
			userObject = u.get(0);
			userObject.setAuthenticated("Y");
			userObject.setSession(session);
			userObject.setPassword(null);
			UserShort us = new UserShort(session, u.get(0));
			us.setDeviceToken(deviceToken);
			us.setPns(pns);
			us.setBaseUrl(baseUrl);

			InternalUserDb.loggedUsers.put(session, us);


			if(InternalUserDb.loggedUserDevices.containsKey(user)) {
				if(InternalUserDb.loggedUserDevices.get(user).stream().noneMatch(p -> p.equals(deviceToken))) {
					InternalUserDb.loggedUserDevices.get(user).add(deviceToken);
				}
			} else {
				List<String> t = new ArrayList<>();
				t.add(deviceToken);
				InternalUserDb.loggedUserDevices.put(user, t);
				System.out.println("Added new firebase token: " + deviceToken);
			}

			return userObject;
		} else {
			return null;
		}
	}
	
	
	public boolean addUser(User u) throws Exception {
		try	{
			User usr = internalUserDb.getUser(u.getUser());
			if(usr == null ) return false; // user exists
			return internalUserDb.addUser(u);
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
		


	}
	
	
	public User addUserWithReturn(User u) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		u.setPassword(SHA512Hasher.hash(u.getPassword()));
		try	{
			User usr = internalUserDb.getUser(u.getUser());
			if(usr != null) {
				return usr;
			}
			if(internalUserDb.addUser(u)) {
				usr = internalUserDb.getUser(u.getUser().toLowerCase());
				return usr;
			} else {
				return null;
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	public boolean registerUser(User u) throws Exception {
		if(u.getPassword() != null && !u.getPassword().isBlank() && u.getPassword().isEmpty()){
			u.setPassword(SHA512Hasher.hash(u.getPassword()));
		}

		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return QuickUserUpdateTask.quickUserUpdateDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), u);
			} else {
				return internalUserDb.addRegisteredUser(u.getUserType(), u.getUser(), u.getPassword(), u.getFirstName(), u.getLastName(), u.getEmail());
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}

	}
	
	
	public boolean approveRegisteringUser(final String userName,
										  final String department,
										  final String title,
										  final String manager,
										  final String characteristic,
										  final String description,
										  final String active) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return ApproveRegisteredUserTask.approveDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), userName);
			} else {
				User u = internalUserDb.getRegisteringUser(userName);
				internalUserDb.addUser(	u.getUserType(),
										u.getUser(),
										u.getPassword(),
										u.getFirstName(),
										u.getLastName(),
										u.getEmail(),
										Integer.parseInt(department),
										Integer.parseInt(title),
										Integer.parseInt(manager),
										characteristic,
										description,
										active);
				internalUserDb.deleteRegisteredUser(userName);
				return true;
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean rejectRegisteringUser(final String userName) throws Exception {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteRegisteredUserTask.deleteTitleDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), userName);
			} else {
				internalUserDb.deleteRegisteredUser(userName);
				return true;
			}


		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean updateUser(User u) throws Exception {
		if(u.getPassword() != null && !u.getPassword().isBlank() && u.getPassword().isEmpty()){
			u.setPassword(SHA512Hasher.hash(u.getPassword()));
		}

		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return UpdateUserTask.UpdateUserDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), u);
			} else {
				User usr = internalUserDb.getUser(u.getUser());
				if(usr == null) return false; // user doesn't exist
				/*if regular user just update, but if SUPER check if limit is between 1 and 10*/
				if(usr.getUserType().toUpperCase().compareTo("SUPER") == 0
						|| u.getUserType().toUpperCase().compareTo("SUPER") == 0) {
					List<User> supers = internalUserDb.getAllUsersByType("SUPER");
					if(supers.size() >= 9 && u.getUserType().compareToIgnoreCase("SUPER") == 0 && usr.getUserType().compareToIgnoreCase("SUPER") !=0) { /*we can have another SUPER*/
						throw new Exception("No more SUPER users allowed");
					} else {
						return internalUserDb.updateSuper(u);
					}
				} else { /*Regular user can be updated regardless*/
					return internalUserDb.updateUser(u);
				}
			}


		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}


	}
	
	public boolean quickUserUpdate(User u) throws Exception {
		if(u.getPassword() != null && !u.getPassword().isBlank() && u.getPassword().isEmpty()){
			u.setPassword(SHA512Hasher.hash(u.getPassword()));
		}

		try	{

			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return QuickUserUpdateTask.quickUserUpdateDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), u);
			} else {
				User usr = internalUserDb.getUser(u.getUser());
				if(usr == null) {
					AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "user does not exist");
					return false;
				}
				/*if regular user just update, but if SUPER check if limit is between 1 and 10*/
				if(usr.getUserType().toUpperCase().compareTo("SUPER") == 0
						|| u.getUserType().toUpperCase().compareTo("SUPER") == 0) {
					List<User> supers = internalUserDb.getAllUsersByType("SUPER");
					if(supers.size() >= 9 && u.getUserType().compareToIgnoreCase("SUPER") == 0 && usr.getUserType().compareToIgnoreCase("SUPER") !=0) { /*we can have another SUPER*/
						throw new Exception(AppLogger.logError(className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, "No more SUPER users allowed"));
					} else {
						return internalUserDb.updateSuper(u);
					}
				} else { /*Regular user can be updated regardless*/
					return internalUserDb.quickUserUpdate(u);
				}
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean updateMe(final long id, 
							final String firstName, 
							final String lastName
							) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return UpdateMeTask.updateMeDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id, firstName, lastName);
			} else {
				User usr = internalUserDb.getUserById(id);
				if(usr == null) return false; // user doesn't exist
				return internalUserDb.updateMe(id, firstName, lastName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean updateMyEmailUserName(final long id, final String userName, final String email) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return UpdateMyEmailAndUserNameTask.updateMyEmailAndUSerNameDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id, userName, email);
			} else {
				User usr = internalUserDb.getUserById(id);
				if(usr == null) return false; // user doesn't exist
				return internalUserDb.updateMeEmailUserName(id, userName, email);
			}

		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean updateMyPassword(final long id, 
									final String password
									) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return UpdateMyPasswordTask.updateMyPasswordDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id, password);
			} else {
				User usr = internalUserDb.getUserById(id);
				if(usr == null) return false; // user doesn't exist
				return internalUserDb.updateMePassword(id, password);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean deleteUser(final String userName) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteUserByNameTask.deleteUserByNameDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), userName);
			} else {
				User usr = internalUserDb.getUser(userName);
				if(usr == null) return false;
				return internalUserDb.deleteUser(userName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean deleteUser(final long id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteUserByIdTask.deleteUserByIdDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				User u = internalUserDb.getUser(id);
				if(u == null) return false;
				return internalUserDb.deleteUser(id);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}

	public List<String> getUserDevices(String userName) {
		if(InternalUserDb.loggedUserDevices.containsKey(userName)) {
			return InternalUserDb.loggedUserDevices.get(userName);
		}
		else {
			return new ArrayList<String>();
		}
	}

	public User getUser(String userName) {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserByNameTask.getUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), userName);
			} else {
				return internalUserDb.getUser(userName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	public User getUser(final long id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUsersByIdTask.getUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				return internalUserDb.getUser(id);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	public List<User> getUsers(final List<Long> idList) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUsersByIdListTask.getUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), idList);
			} else {
				return internalUserDb.getUsers(idList);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	
	public List<User> getUsers(final String likeUserName) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUsersTask.getUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeUserName);
			} else {
				return internalUserDb.getUsers(likeUserName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}

	
	
	public List<User> getUsersMinusUser(final String likeUserName, final User u ) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetRegisteredUsersTask.getRegisteredUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeUserName);
			} else {
				return internalUserDb.getUsersMinusUser(likeUserName, u);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	
	public List<User> getRegisteringUsers(final String likeUserName) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetRegisteredUsersTask.getRegisteredUsersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeUserName);
			} else {
				return internalUserDb.getRegisteringUsers(likeUserName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	public List<ManagerShort> getManagers(final String likeUserName) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserManagersTask.getUserManagersDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeUserName);
			} else {
				return internalUserDb.getManagers(likeUserName);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	public boolean 
	isInternalAdminAuthenticated(	final String admin, 
									final String adminPasscode) {
		if(admin == null || adminPasscode == null) return false;
        return adminPasscode.equalsIgnoreCase(appConstants.getAdminPasscode())
                && admin.equalsIgnoreCase(appConstants.getAdmin());

	}
	
	public boolean 
	isInternalUserAuthenticated(final String user, 
								final String userPasscode)
	{
		if(user == null || userPasscode == null) return false;
        return userPasscode.equalsIgnoreCase(appConstants.getUserPasscode())
                && user.equalsIgnoreCase(appConstants.getUser());
	}
	
	
	///////////////////////////////// Departments///////////////////////////////////////////////////
	public Department addDepartment(final Department d) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				List<SqlRepoDatabase> sqlConn =  InternalUserDb.dataWhales.getSqlDbRefsList().values().stream().sorted((o1, o2) -> Math.max(o1.getTotalRecords(), o2.getTotalRecords())).toList();
				sqlConn.get(0).incrementTotalRecords();
				DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(sqlConn.get(0));
				InternalUsersPersistenceRef.addDepartment(connectionDetailInfo, d);
				return d;
			} else {
				List<Department> dList = internalUserDb.getDepartment(d.getDepartment());
				if(dList.size() == 1) return null;

				internalUserDb.addDepartment(d);
				dList = internalUserDb.getDepartment(d.getDepartment());
				if(dList.size() == 1)
					return dList.get(0);
				else {
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, " Error retrieving newly created department: " + d.getDepartment());
					return new Department(-1, "", "");
				}
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new Department(-1, "", "");
		}
	}
	
	
	public Department updateDepartment(final Department d) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				UpdateUserDeptByNameTask.updateDeptDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), d);
				return d;
			} else {
				List<Department> dList = internalUserDb.getDepartment(d.getDepartment());
				if(dList.size() != 1) return null;

				internalUserDb.updateDepartment(d);
				dList = internalUserDb.getDepartment(d.getDepartment());
				if(dList.size() == 1)
					return dList.get(0);
				else {
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.obj, " Error retrieving newly created department: " + d.getDepartment());
					return new Department(-1, "", "");
				}
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return new Department(-1, "", "");
		}
		


	}
	
	public boolean deleteDepartment(final String d) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteUserDeptByNameTask.deleteDeptByNameDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), d);
			} else {
				List<Department> dList = internalUserDb.getDepartment(d);
				if(dList.isEmpty()) return false;
				return internalUserDb.deleteDepartment(d);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public boolean deleteDepartment(final int id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteUserDeptByIdTask.deleteDeptByIdDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				Department department = internalUserDb.getDepartment(id);
				if(department == null) return false;
				return internalUserDb.deleteDepartment(id);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	public Department getDepartmentById(final int id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserDeptByIdTask.getDeptByIdDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				return internalUserDb.getDepartment(id);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	public Department getDepartmentByName(final String department) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserDeptByNameTask.getDeptByNameDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), department);
			} else {
				List<Department> ret = internalUserDb.getDepartment(department);
				if(ret.size() == 1) {
					return ret.get(0);
				} else {
					return null;
				}
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	public List<Department> getDepartments(final String likeDepartment) throws Exception	{
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserDeptsTask.getDeptsDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeDepartment);
			} else {
				return internalUserDb.getDepartments(likeDepartment);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	//////////////////////////////////////////  Titles /////////////////////////////////////////////////////////////////
	public Title addTitle(final Title t) throws Exception	{
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				List<SqlRepoDatabase> sqlConn =  InternalUserDb.dataWhales.getSqlDbRefsList().values().stream().sorted((o1, o2) -> Math.max(o1.getTotalRecords(), o2.getTotalRecords())).toList();
				sqlConn.get(0).incrementTotalRecords();
				DbConnectionInfo connectionDetailInfo = DbConnectionInfo.makeDbConnectionInfo(sqlConn.get(0));
				InternalUsersPersistenceRef.addTitle(connectionDetailInfo, t);
				return t;
			} else {
				List<Title> tList = internalUserDb.getTitle(t.getTitle());
				if(tList.size() == 1) return new Title(-1, "", "");

				internalUserDb.addTitle(t);
				tList = internalUserDb.getTitle(t.getTitle());
				if(tList.size() == 1)
					return tList.get(0);
				else {
					AppLogger.logError(Thread.currentThread().getStackTrace()[1], AppLogger.ctrl, "Error retrieving newly created title: " + t.getTitle());
					return new Title(-1, "", "");
				}
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return new Title(-1, "", "");
		}
	}
	
	
	public boolean updateTitle(final Title t) throws Exception	{
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return UpdateUserTitleByNameTask.updateTitleDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), t);
			} else {
				Title title = internalUserDb.getTitle(t.getId());
				if(title == null) return false; // no such title found
				return internalUserDb.updateTitle(t);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}



	
	public boolean deleteTitle(final int id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return DeleteUserTitleTask.deleteTitleDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				Title t = internalUserDb.getTitle(id);
				if(t == null) return false;
				return internalUserDb.deleteTitle(id);
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}
	
	
	public Title getTitle(final String t) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserTitleByNameTask.getTitleDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), t);
			} else {
				List<Title> tList = internalUserDb.getTitle(t);
				if(tList.size() == 1)
					return tList.get(0);
				else
					return null;
			}
		} catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	
	public Title getTitle(final int id) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserTitleTask.getTitleDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), id);
			} else {
				return internalUserDb.getTitle(id);
			}


		}
		catch(Exception ex)	{
			AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
			return null;
		}
	}



	public List<Title> getTitles(final String likeTitle) throws Exception {
		try	{
			if(!InternalUserDb.dataWhales.getSqlDbRefsList().isEmpty()) {
				return GetUserTitlesTask.getTitlesDistributed(InternalUserDb.dataWhales.getSqlDbRefsList(), likeTitle);
			} else {
				return internalUserDb.getTitles(likeTitle);
			}
		}
		catch(Exception ex)	{
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
		}
	}
	
	
	/**
	 * This procedure is used for non-web or web-failed connections that have no proper channel to log-off
	 * Example: a broken websocket connection or a python client that errored-out 
	 */
	public void cleanupIdleSessions() {
		final long maxIdle = 5 * 60 * 1000;
		List<UserShort> users = InternalUserDb	.loggedUsers
												.values()
												.stream()
												.filter(user->user.getTimeStampDiff() > maxIdle)
												.toList();
	
		
	
		for(UserShort u: users) {
			removeSession(u.getUser(), u.getSession());
			//WebSocketsWrapper.sendLogoff( u.getSession() );
		}
		
	}
	
	
	
	
}
