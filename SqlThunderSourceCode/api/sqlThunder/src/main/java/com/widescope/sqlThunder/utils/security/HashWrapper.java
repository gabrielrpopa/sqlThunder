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


package com.widescope.sqlThunder.utils.security;

public class HashWrapper {

	private static final long HASH_INIT = 0xcbf29ce484222325L;
	private static final long HASH_PRIME = 0x100000001b3L;

	public HashWrapper() {}

	public static long hash64FNV(final String str) {
		long retVal = HASH_INIT;
	    for(int cnt = 0; cnt < str.length(); cnt++) {
	    	retVal ^= str.charAt(cnt);
	    	retVal *= HASH_PRIME;
	    }
	    return retVal;
	}
	
	
	
	
}
