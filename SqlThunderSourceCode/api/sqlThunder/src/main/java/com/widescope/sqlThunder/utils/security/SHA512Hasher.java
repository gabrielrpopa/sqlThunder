package com.widescope.sqlThunder.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class SHA512Hasher {
	public static byte[] hexStringToByteArray() {
		String salt = "df3b024c-e37c-43f4-b1e1-353e710ab55f";
	    int len = salt.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(salt.charAt(i), 16) << 4)
	                             + Character.digit(salt.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String hash(String passwordToHash){
		byte[] saltArray = hexStringToByteArray();
	    String generatedPassword = null;
	    try {
	      MessageDigest md = MessageDigest.getInstance("SHA-512");
	      md.update(saltArray);
	      byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
	      StringBuilder sb = new StringBuilder();
		  for (byte aByte : bytes) {
             sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
          }
	      generatedPassword = sb.toString();
	    }
	    catch (NoSuchAlgorithmException ignored){
	    }
	    return generatedPassword;
	}

	
	
	
	public static boolean checkPassword(String hash, String passwordToHash){
	    String generatedHash = hash(passwordToHash);
	    return hash.equals(generatedHash);
	}
	  
	  
}
