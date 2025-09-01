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

public class AuthType {
	public static final String NATIVE="NATIVE";
	public static final String GOOGLE="GOOGLE";
	public static final String OKTA="OKTA";
	
	public static final String LDAP="LDAP";
	
	public static boolean isValid(String val){
        return val.compareTo(NATIVE) == 0 || val.compareTo(GOOGLE) == 0 || val.compareTo(OKTA) == 0 || val.compareTo(LDAP) == 0;
	}
}
