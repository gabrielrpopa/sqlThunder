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

public class UserType {
	public static final String SUPER = "SUPER";
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	
	public static boolean isValid(final String val) {
        return val.compareTo(SUPER) == 0 || val.compareTo(ADMIN) == 0 || val.compareTo(USER) == 0;
	}
	
}
