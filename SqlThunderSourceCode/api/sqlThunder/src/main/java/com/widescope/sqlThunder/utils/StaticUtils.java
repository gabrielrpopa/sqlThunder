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

import java.io.*;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.widescope.logging.AppLogger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;


public class StaticUtils {

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

	public static String getRandomKey( ) {
		UUID uuid = UUID.randomUUID( );
		return uuid.toString( );
	}
	
	
	

	public static String strongPasswordGenerator() {
		final int password_length = 16;
		
		char[] special = new char[]{'!', '"', '#', '$', '%', '&', '\'', '(', ')', '*',  '+', '-', '.', '/', ':', '=', '?', '@', '[', ']', '^', '_', '`', '{', '|', '}', '~'};

		
		List<CharacterRule> rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
									new CharacterRule(EnglishCharacterData.LowerCase, 1), 
									new CharacterRule(EnglishCharacterData.Digit, 1),
									new CharacterRule(new org.passay.CharacterData() {
						                public String getErrorCode() {
						                    return "INSUFFICIENT_SPECIAL";
						                }

						                public String getCharacters() {
						                    return new String(special);
						                }
						            }, 1)
									);
									
		
		PasswordGenerator generator = new PasswordGenerator();
        return generator.generatePassword(password_length, rules);
	}
	
	
	public static String getHostInfo() {
		String result = "";
		InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            result ="Host Name:  " + ip.getHostName() + "  IP Address:  " + ip.getHostAddress() ;
			return result;
        } catch (UnknownHostException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			result = e.getMessage();
			return result;
        }
	}
	
	public static String getHost() {
		InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostName() ;
        } catch (UnknownHostException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
        }
	}

	public static String getIp() {
		InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostAddress() ;
        } catch (UnknownHostException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return null;
        }
	}
	
	
	public static String pingAssociateServer(final String server) {
		InetAddress inetAddress = null;
		String result = "";
		try {
			System.out.println("Sending Ping Request to " + server);
			inetAddress = InetAddress.getByName(server);
			if (inetAddress.isReachable(5000)) {
				result = "Host Name:  " + inetAddress.getHostName() + "  IP Address:  " + inetAddress.getHostAddress() + " is pingable";
            } else {
				result ="Host  " + server + " is NOT pingable";
            }
            return result;
        } catch (IOException e) {
			return e.getMessage();
		}
	}
	
	public static void writeToFile(	final String data, 
									final String folder, 
									final String fileName) throws Exception {
        File file = new File(folder + "/" + fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
			throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        } finally{
            try {
                assert fr != null;
                fr.close();
            } catch (IOException e) {
				AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
            }
        }
    }
	
	public static String currentTimeAndDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);
	}
	
	public static String generateSecureString32() {
		SecureRandom random = new SecureRandom();
		byte[] seed = random.generateSeed(55); 
		random.setSeed(seed);
	    byte[] bytes = new byte[32];
	    random.nextBytes(bytes);
	    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
	    return encoder.encodeToString(bytes);
	}

	public static String generateSecureStringN(int length) {
		SecureRandom random = new SecureRandom();
		byte[] seed = random.generateSeed(55);
		random.setSeed(seed);
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		return encoder.encodeToString(bytes);
	}
	
	public static String generateSecureString32Windows() throws NoSuchAlgorithmException {
		SecureRandom nativeRandom = SecureRandom.getInstance("NativePRNGNonBlocking"); // assuming Unix
		byte[] seed = nativeRandom.generateSeed(55); // NIST SP800-90A suggests 440 bits for SHA1 seed
		SecureRandom sha1Random = SecureRandom.getInstance("SHA1PRNG");
		sha1Random.setSeed(seed);
		byte[] values = new byte[32];
		sha1Random.nextBytes(values); // SHA1PRNG, seeded properly
	    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
	    return encoder.encodeToString(values);
	}
	
	
	public static String generateSecureString16() {
		SecureRandom random = new SecureRandom();
	    byte[] bytes = new byte[16];
	    random.nextBytes(bytes);
	    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
	}
	
	
	
	public static String getUUID() {
        return UUID.randomUUID().toString() + "-" +generateSecureString16();
	}
	
	
	/**
	 * Remove unnecessary blanks
	 * @param str
	 * @return
	 */
	public static String normalizeString(final String str) {
		return StringUtils.normalizeSpace(str);
	}
	
	
	
	
	public static String getKey(String key, String session) {
        return key.concat(":").concat(session);
	}
	
	
	public static String FormatAsName(String str) {
		if(!str.isEmpty())
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		else
			return str;
	}

	
	
	public static String getStringUniqueID(final String str) {
		byte[] byteArray = str.getBytes();
		String countStr = String.valueOf(byteArray.length);
		long sum = 0;
		for (byte myByte: byteArray) {
			sum+= myByte;
        }
		return countStr + "_" + String.valueOf(sum);
	}
	
	
	
	
	
	public static String replace(	final String text, 
									final String replacement, 
									final String first, 
									final String last) {
		
		// the beginning index, inclusive.
		int beginIndex = text.indexOf(first) + 1;

		// the ending index, exclusive.
		int endIndex = text.lastIndexOf(last);

		// get substring that needs to be replaced
		String target = text.substring(beginIndex, endIndex);
		return text.replace(target, replacement);
	}
	
	public static String replaceStringBetween(	final String input, 
												final String start, 
												final String end, 
												final String replaceWith) {
			return replaceStringBetween(input, start, end, false, false, replaceWith);
	}

	public static String replaceStringBetween(String input, 
			                                  String start, 
			                                  String end, 
			                                  final boolean startInclusive, 
			                                  final boolean endInclusive, 
			                                  final String replaceWith)	{
		start = Pattern.quote(start);
		end = Pattern.quote(end);
		return input.replaceAll("(" + start + ")" + ".*" + "(" + end + ")",
		(startInclusive ? "" : "$1") + replaceWith + (endInclusive ? "" : "$2"));
	}

	//without regex
	public static String replaceBetweenWithoutRegex(String str, 
													final String start, 
													final String end, 
													final boolean startInclusive, 
													final boolean endInclusive, 
													final String replaceWith) {
		int i = str.indexOf(start);
		while (i != -1) {
			int j = str.indexOf(end, i + 1);
			if (j != -1) {
				String data = (startInclusive ? str.substring(0, i) : str.substring(0, i + start.length())) + replaceWith;
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

	public static boolean isStringNullOrEmpty(String str) {
        return str == null || str.isBlank() || str.isEmpty();
	}
	
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static <T, U> List<U>
    convertStringListToIntList(List<T> listOfString,
                               Function<T, U> function)
    {
        return listOfString.stream()
            .map(function)
            .collect(Collectors.toList());
    }

	public static String throwableExceptionToString(Throwable ex, String methodName) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		return methodName + " fatal: " + sw;
	}
}
