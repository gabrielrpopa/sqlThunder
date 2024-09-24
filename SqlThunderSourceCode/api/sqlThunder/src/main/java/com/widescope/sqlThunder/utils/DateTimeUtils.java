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

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;


public class DateTimeUtils {
	
	
	/**
	 * 
	 * @return time in seconds Since EPOCH
	 */
	public static long secondsSinceEpoch() {
		return Instant.now().toEpochMilli() / 1000L;
	}
	
	/**
	 * 
	 * @param m time in milliseconds Since EPOCH
	 * @return Date
	 */
	public static Date epochMillisecToDate(long m) {
		java.util.Date d = new java.util.Date();
		d.setTime(m);
		return d;
	}
	
	
	
	public static long millisecondsSinceEpoch(Date dt) {
		return dt.getTime();
	}
	
	public static Date getOneHourSinceNow() {
		//LocalDateTime.now()
		// or LocalDateTime.now().minusHours(1)
		return new Date(System.currentTimeMillis() - 3600 * 1000);
	}
	
	public static long millisecondsSinceEpoch() {
		return Instant.now().toEpochMilli();
	}

	public static long nanosecondsSinceEpoch() {
		return Instant.now().getEpochSecond() *1000000000 + Instant.now().getNano();
	}
	
	
	public static long millisecondsUnix() {
		return System.currentTimeMillis();
	}
	
	public static String getCurrentYearString() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
        return String.valueOf(year);
	}
	
	public static int getCurrentYearInt() {
		Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
	}
	
	public static int getCurrentMonthInt() {
		LocalDate currentdate = LocalDate.now();
	    Month currentMonth = currentdate.getMonth();
		return currentMonth.getValue();
    }
	
	public static String getCurrentMonthString() {
		LocalDate currentdate = LocalDate.now();
	    Month currentMonth = currentdate.getMonth();
	    DecimalFormat mFormat= new DecimalFormat("00");
		return mFormat.format(currentMonth.getValue());
    }
	
	public static String getCurrentDayIntString() {
		LocalDate currentdate = LocalDate.now();
		int currentDay = currentdate.getDayOfMonth();
		DecimalFormat mFormat= new DecimalFormat("00");
		return mFormat.format(currentDay);
    }
	
	public static int getCurrentDayInt() {
		LocalDate currentdate = LocalDate.now();
		return currentdate.getDayOfMonth();
    }
	
	public static int getCurrentHourInt() {
	    LocalTime now = LocalTime.now();
		return now.getHour();
    }
	
	public static String getCurrentHourString()
    {
	    LocalTime now = LocalTime.now();
	    DecimalFormat mFormat= new DecimalFormat("00");
		return mFormat.format(now.getHour());
    }
	
	
	public static String getCurrentMonthYear() {
		LocalDate currentdate = LocalDate.now();
	    Month currentMonth = currentdate.getMonth();
	    DecimalFormat mFormat= new DecimalFormat("00");
        return mFormat.format(currentdate.getDayOfMonth()) 
        		+ "-" + mFormat.format(currentMonth.getValue()) 
        		+ "-" + mFormat.format(currentdate.getYear());
    }
	
	public static String getCurrentDayMonthYear() {
		LocalDate currentdate = LocalDate.now();
	    DecimalFormat mFormat= new DecimalFormat("00");
        return mFormat.format(currentdate.getDayOfMonth()) 
        		+ "_" + mFormat.format(currentdate.getMonth().getValue()) 
        		+ "_" + mFormat.format(currentdate.getYear());
    }

	
	
	public static String getCurrentHourDayMonthYear() {
		LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
	    Month currentMonth = currentdate.getMonth();
	    int currentYear = currentdate.getYear();
	    LocalTime now = LocalTime.now();
	    return String.valueOf(now.getHour()) 
	    		+ "_" + String.valueOf(currentDay) 
	    		+ "_" + String.valueOf(currentMonth.getValue()) 
	    		+ "_" + String.valueOf(currentYear);
    }
	
	public static String getCurrentTimeForFileName() {
		LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
	    Month currentMonth = currentdate.getMonth();
	    int currentYear = currentdate.getYear();
	    LocalTime now = LocalTime.now();
	    
        return String.valueOf(now.getNano() 
        		+ "_" + now.getSecond() 
        		+ "_" + now.getMinute() 
        		+ "_" + now.getHour()) 
        		+ "_" + String.valueOf(currentDay) 
        		+ "_" + String.valueOf(currentMonth.getValue()) 
        		+ "_" + String.valueOf(currentYear);
        
    }
	
	public static String getCurrentTimeForFileName(final String userName) {
		LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
	    Month currentMonth = currentdate.getMonth();
	    int currentYear = currentdate.getYear();
	    LocalTime now = LocalTime.now();
	    
        return String.valueOf(userName 
        		+ "_" + now.getNano()
        		+ "_" + now.getSecond() 
        		+ "_" + now.getMinute() 
        		+ "_" + now.getHour()) 
        		+ "_" + String.valueOf(currentDay) 
        		+ "_" + String.valueOf(currentMonth.getValue()) 
        		+ "_" + String.valueOf(currentYear);
        
    }

	public static int getMaxDayOfTheMonth(	final int month, 
											final int year)
	{
		int noOfDaysInMonth = 31;
		switch (month) {
			case 1, 5, 3, 7, 8, 10:
				break;
			case 2:
				if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0))) {
					noOfDaysInMonth = 29;
				} else {
					noOfDaysInMonth = 28;
				}
				break;
			case 4:
				noOfDaysInMonth = 30;
				break;
			case 6:
				noOfDaysInMonth = 30;
				break;
			case 9:
				noOfDaysInMonth = 30;
				break;
			case 11:
				noOfDaysInMonth = 30;
				break;
			case 12:
        }
		return noOfDaysInMonth;
	}
	
}
