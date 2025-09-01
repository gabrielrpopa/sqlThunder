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


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import com.widescope.logging.AppLogger;
import org.apache.commons.net.util.*;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;

public class Ip4NetUtils {
	

	private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();


	public static String[] getAddressList(	final String ipAddress,
										final int range) {
		if(range > 32 || range < 0) return null;
		SubnetUtils utils = new SubnetUtils(ipAddress + "/" + String.valueOf(range) );
        return utils.getInfo().getAllAddresses();
	}
	
	
	public static void 
	updateLocalIpAddresses() {
		try {
			ConfigRepoDb.localIpList.clear();
			
			Enumeration<NetworkInterface>  e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()) {
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<InetAddress> ee = n.getInetAddresses();
			    while (ee.hasMoreElements()) {
			        InetAddress i = (InetAddress) ee.nextElement();
			        ConfigRepoDb.localIpList.put(i.getHostAddress(), i.getHostAddress());
			    }
			}
		} catch (SocketException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
	}
	
	
	public static List<String> 
	getLocalIpAddresses() {
		List<String> ret = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface>  e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()) {
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration<InetAddress> ee = n.getInetAddresses();
			    while (ee.hasMoreElements()) {
			        InetAddress i = (InetAddress) ee.nextElement();
			        ret.add(i.getHostAddress());
			    }
			}
		} catch (SocketException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
		}
		
		return ret;
	}
	
	
	

	public static long ipToLong(final InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}
	
	public static boolean isValidRange(	final String ipStart, 
										final String ipEnd,
										final String ipToCheck) {
		try {
			long ipLo = ipToLong(InetAddress.getByName(ipStart));
			long ipHi = ipToLong(InetAddress.getByName(ipEnd));
			long ipToTest = ipToLong(InetAddress.getByName(ipToCheck));
			return (ipToTest >= ipLo && ipToTest <= ipHi);
		} catch (UnknownHostException e) {
			AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
			return false;
		}
	}

	private static boolean isValid(final String ip) {
        String a[] = ip.split("[.]");
        for (String s : a) {
            int i = Integer.parseInt(s);
            if (s.length() > 3 || i < 0 || i > 255) {
                return false;
            }
            if (s.length() > 1 && i == 0)
                return false;
            if (s.length() > 1 && s.charAt(0) == '0')
                return false;
        }
        return true;
    }
	
	
	

	private static List<String> convert(final String s) {
        List<String> l = new ArrayList<>();
        int size = s.length();
 
        String snew = s;
 
        for (int i = 1; i < size - 2; i++) {
            for (int j = i + 1; j < size - 1; j++) {
                for (int k = j + 1; k < size; k++) {
                    snew = snew.substring(0, k) + "." + snew.substring(k);
                    snew = snew.substring(0, j) + "." + snew.substring(j);
                    snew = snew.substring(0, i) + "." + snew.substring(i);
                    if (isValid(snew)) {
                        l.add(snew);
                    }
                    snew = s;
                }
            }
        }
 
        l.sort(new Comparator<String>() {
            public int compare(String o1, String o2) {
                String a1[] = o1.split("[.]");
                String a2[] = o2.split("[.]");
                int result = -1;
                for (int i = 0; i < 4 && result != 0; i++) {
                    result = a1[i].compareTo(a2[i]);
                }
                return result;
            }
        });
        return l;
    }
		
	
	public static List<String>
	restoreIpAddresses(final String A) {
        if (A.length() < 3 || A.length() > 12)
            return new ArrayList<>();
        return convert(A);
    }
	
	

	public long ip4ToLong(final String ipAddress)	{
	    String[] ipAddressInArray = ipAddress.split("\\.");
	    long result = 0;
	    for (int i = 0; i < ipAddressInArray.length; i++) {
	        int power = 3 - i;
	        int ip = Integer.parseInt(ipAddressInArray[i]);
	        result += (long) (ip * Math.pow(256, power));
	    }
	    return result;
	}
	
	
	
	public String longToIp4(final long ip) {
		return ((ip >> 24) & 0xFF) + "." 
		        + ((ip >> 16) & 0xFF) + "." 
		        + ((ip >> 8) & 0xFF) + "." 
		        + (ip & 0xFF);
	}
	


	

}
