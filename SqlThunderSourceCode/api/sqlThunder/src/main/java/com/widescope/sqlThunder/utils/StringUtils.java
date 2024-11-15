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

package com.widescope.sqlThunder.utils;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;
import com.widescope.cache.service.GlobalStorage;
import com.widescope.logging.AppLogger;
import org.apache.commons.lang3.RandomStringUtils;



public class StringUtils {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	
	public StringUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String generateUniqueString() {
		return RandomStringUtils.randomAlphabetic(20);
	}
	
	public static String generateUniqueString32() {
		return RandomStringUtils.randomAlphabetic(32);
	}

	public static String generateKey(final String str) {
		int count = 0;
		String key = StringUtils.generateUniqueString();
		String keyToSearch = key.concat(":").concat(str);
		while(true)	{
			count++;
			if( !GlobalStorage.stringMap.containsKey(keyToSearch) ) {
				return key;
			}
			if(count >= 100)
				return null;
		}
	}


	public static String generateUniqueString16() {
		return RandomStringUtils.randomAlphabetic(16);
	}
	
	public static String generateUniqueString8() {
		return RandomStringUtils.randomAlphanumeric(8);
	}
	
	
	
	public static String getKey(final String key, final String session)	{
		String keyToSearch = key.concat(":").concat(session);
		return keyToSearch;
	}
	
	
	public static String formatAsName(final String str) {
		if(!str.isEmpty())
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		else
			return str;
	}
	
	public static String getStringUniqueID(String str) 
	{
		byte[] byteArray = str.getBytes();
		String countStr = String.valueOf(byteArray.length);
		long sum = 0;
		for (byte myByte: byteArray) {
			sum+=(long)myByte;
        }
		return countStr + "_" + String.valueOf(sum);
	}
	
	
	
	public static String  getStringHashOrUniqueValue(String str)
	{
		try	{
			return getStringHashValue(str);
		} catch(Exception ex) {
			return getStringUniqueID(str); 
		}
	}
	
	
	public static String replace(String text, String replacement, String first, String last)
	{
		
		// the beginning index, inclusive.
		int beginIndex = text.indexOf(first) + 1;

		// the ending index, exclusive.
		int endIndex = text.lastIndexOf(last);

		// get substring that needs to be replaced
		String target = text.substring(beginIndex, endIndex);
		return text.replace(target, replacement);
	}
	
	public static String replaceStringBetween(String input, String start, String end, String replaceWith) {
			return replaceStringBetween(input, start, end, false, false, replaceWith);
	}

	public static String replaceStringBetween(String input, 
			                                  String start, 
			                                  String end, 
			                                  boolean startInclusive, 
			                                  boolean endInclusive, 
			                                  String replaceWith)
	{
		start = Pattern.quote(start);
		end = Pattern.quote(end);
		return input.replaceAll("(" + start + ")" + ".*" + "(" + end + ")",
		(startInclusive ? "" : "$1") + replaceWith + (endInclusive ? "" : "$2"));
	}

	//without regex
	public static String replaceBetweenWithoutRegex(String str, String start, String end, boolean startInclusive, boolean endInclusive, String replaceWith)
	{
		int i = str.indexOf(start);
		while (i != -1) {
			int j = str.indexOf(end, i + 1);
			if (j != -1) {
				String data = (startInclusive ? str.substring(0, i) : str.substring(0, i + start.length())) +
						replaceWith;
				String temp = (endInclusive ? str.substring(j + end.length()) : str.substring(j));
				data += temp;
				str = data;
				i = str.indexOf(start, i + replaceWith.length() + end.length() + 1);
			} else {
				break;
			}
		}
		return str;
	}

		
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	
	// Java method to create SHA-25 checksum, yields 64 bytes
    public static String getSHA256Hash(String data, String algo)  {
        try {
        	if(algo.compareTo("MD2") != 0 
        			&& algo.compareTo("MD5") != 0 
        			&& algo.compareTo("SHA-1") != 0 
        			&& algo.compareTo("SHA-256") != 0
        			&& algo.compareTo("SHA-384") != 0
        			&& algo.compareTo("SHA-512") != 0
        			) {
        		algo = "SHA-256";
        	}
            MessageDigest digest = MessageDigest.getInstance(algo);
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash); 
        }catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
        } catch(Throwable ex)	{
			AppLogger.logThrowable(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            return null;
		}
        
    }
 
    // Java method to create MD5 checksum
    public static String getMD5Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash); // make it printable
        }catch(Exception ex) {
			AppLogger.logException(ex, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
        }
       
    }
    
    public static String getStringHashValue(String str) throws NoSuchAlgorithmException	{
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		byte[] messageDigest = md.digest(str.getBytes());
		BigInteger no = new BigInteger(1, messageDigest); 

        // Convert message digest into hex value 
        StringBuilder hashText = new StringBuilder(no.toString(16));
        while (hashText.length() < 32) {
            hashText.insert(0, "0");
        } 
        return hashText.toString();
	}


	public static String convertByteArrayToBase64(final byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}

	public static String convertStringToBase64(final String input) {
		return Base64.getEncoder().encodeToString(input.getBytes());
	}


	public static String convertBase64ToString(final String input) {
		return new String(Base64.getDecoder().decode(input.getBytes()));
	}


	public static String generateRequestId(String requestId) {

		Pattern UUID_REGEX =
				Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
		boolean isUUID = UUID_REGEX.matcher(requestId).matches();

		if(!isUUID || requestId.isBlank() || requestId.isEmpty()) {
			return StaticUtils.getUUID();
		}

		return requestId;
	}

	public static String generateUniqueScriptName(final String scriptName) {
		if(scriptName==null || scriptName.isBlank() || scriptName.isEmpty()) {
			return "script_" + StaticUtils.getUUID();
		}

		return scriptName;
	}


}
