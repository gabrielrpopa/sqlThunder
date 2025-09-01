/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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

package com.widescope.rdbmsRepo.utils;

import java.util.UUID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class StaticUtils {

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
		InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            return "Host Name:  " + ip.getHostName() + "  IP Address:  " + ip.getHostAddress() ;
        }
        catch (UnknownHostException e)  {
        	return "!!!!!!!!!!!!!!!!!!!!!!!!!!Local Host info cannot be gotten!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
        }
	}

	
	public static String pingAssociateServer(String server)  {
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
        }
		catch (IOException e) {
			return e.getMessage();
		}
	}
	
	public static void writeToFile(String data, String folder, String fileName) throws Exception {
        File file = new File(folder + "/" + fileName);
        try (FileWriter fr = new FileWriter(file)) {
            fr.write(data);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
	
	public static String currentTimeAndDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);
	}
	
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
        return uuid.toString();
	}

	public static String normalizeString(final String str)
	{
		return StringUtils.normalizeSpace(str);
	}

}
