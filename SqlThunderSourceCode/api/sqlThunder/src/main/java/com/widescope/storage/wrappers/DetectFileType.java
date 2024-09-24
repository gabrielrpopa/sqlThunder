package com.widescope.storage.wrappers;


import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import com.opencsv.CSVReader;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultQuery;
import com.widescope.rdbmsRepo.database.embeddedDb.embedded.H2Test;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.storage.internalRepo.InternalFileStorageRecord;




public class DetectFileType implements RestInterface{
	
	/*
	 * H2
	 * ResultQuery
	 * TableFormatMap
	 * CSV
	 * PDF
	 * WORD
	 * EXCEL
	 * UNKNOWN
	 * 
	 * */
	private String type; 
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	

	public static boolean isH2(String fullPath) {
		try {
			H2Test h2 = new H2Test(fullPath);
			Connection conn = h2.getConnection();
			DatabaseMetaData md = conn.getMetaData();
			System.out.println("H2 Major Version:" + md.getDatabaseMajorVersion()); 
			System.out.println("H2 Minor Version:" + md.getDatabaseMinorVersion());
			System.out.println("H2 Product Name:" + md.getDatabaseProductName());
			System.out.println("H2 Product Version:" + md.getDatabaseProductVersion());
			conn.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isResultQuery(final String fullPath) {
		try {
			ResultQuery ret = ResultQuery.toResultQuery(FileUtilWrapper.readFileToString(fullPath));
            return ret != null;
        } catch (Exception e) {
			return false;
		}
	}

	public static boolean isTableFormatMap(final String fullPath) {
		try {
			TableFormatMap ret = TableFormatMap.toTableFormatMap(FileUtilWrapper.readFileToString(fullPath));
            return ret != null;
        } catch (Exception e) {
			return false;
		}
	}
	
	public static TableFormatMap getTableFormatMap(final String fullPath) {
		try {
			return TableFormatMap.toTableFormatMap(FileUtilWrapper.readFileToString(fullPath));
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isCsv(final String fullPath) {
		Path path = Path.of(fullPath);
		boolean ret = false;
	    try (Reader reader = Files.newBufferedReader(path)) {
	        try (CSVReader csvReader = new CSVReader(reader)) {
	        	csvReader.readAll();
	        	ret = true;
	        } catch(Exception ex) {
	        	ret = false;
	        }
	    } catch(Exception ex) {
        	ret = false;
        }
	    
	    return ret;
	}
	
	
	public static String detectMimeType(final InternalFileStorageRecord r) {
		if(r.getStorageType().toLowerCase().equals("application/pdf")) {
			return "PDF";
		} else if(r.getStorageType().equalsIgnoreCase("audio/aac")) { // AAC audio (.aac)
			return "UNKNOWN";
		} else if(r.getStorageType().equalsIgnoreCase("application/x-abiword")) { // AbiWord document (.abw)
			return "UNKNOWN";
		} else if(r.getStorageType().equalsIgnoreCase("image/avif")) { // AVIF image  (.avif)
			return "UNKNOWN";
		} else if(r.getStorageType().equalsIgnoreCase("application/vnd.ms-excel")) { // Microsoft Excel  (.xls)
			return "EXCEL";
		} else if(r.getStorageType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { // Microsoft Excel  (.xlsx)
			return "EXCEL";
		} else if(r.getStorageType().equalsIgnoreCase("text/csv")) { // .csv
			return "CSV";
		} else if(r.getStorageType().equalsIgnoreCase("application/msword")) { // .doc
			return "WORD";
		} else if(r.getStorageType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) { // .doc
			return "WORD";
		} else {
			return "UNKNOWN";
		}
	}
	
	public static String detectMimeType(final String r) {
		if(r.equalsIgnoreCase("application/pdf")) {
			return "PDF";
		} else if(r.equalsIgnoreCase("audio/aac")) { // AAC audio (.aac)
			return "UNKNOWN";
		} else if(r.equalsIgnoreCase("application/x-abiword")) { // AbiWord document (.abw)
			return "UNKNOWN";
		} else if(r.equalsIgnoreCase("image/avif")) { // AVIF image  (.avif)
			return "UNKNOWN";
		} else if(r.equalsIgnoreCase("application/vnd.ms-excel")) { // Microsoft Excel  (.xls)
			return "EXCEL";
		} else if(r.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { // Microsoft Excel  (.xlsx)
			return "EXCEL";
		} else if(r.equalsIgnoreCase("text/csv")) { // .csv
			return "CSV";
		} else if(r.equalsIgnoreCase("application/msword")) { // .doc
			return "WORD";
		} else if(r.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) { // .doc
			return "WORD";
		} else {
			return "UNKNOWN";
		}
	}
	
	
	

	public static DetectFileType findOut(final String fullPath,
										 final InternalFileStorageRecord r) {
		DetectFileType ret = new DetectFileType();
		if(DetectFileType.isH2(fullPath)) {
			ret.setType("H2");
		} else if(DetectFileType.isResultQuery(fullPath)){
			ret.setType("ResultQuery");
		} else if(DetectFileType.isTableFormatMap(fullPath)){
			ret.setType("TableFormatMap");
		} else if(DetectFileType.isCsv(fullPath)) {
			ret.setType("CSV");
		} else {
			ret.setType(DetectFileType.detectMimeType(r));;
		}
		
		return ret;
	}
	
	public static DetectFileType findOut(final String fullPath,
										 final String r) {
		DetectFileType ret = new DetectFileType();
		if(DetectFileType.isH2(fullPath)) {
			ret.setType("H2");
		} else if(DetectFileType.isResultQuery(fullPath)){
			ret.setType("ResultQuery");
		} else if(DetectFileType.isTableFormatMap(fullPath)){
			ret.setType("TableFormatMap");
		} else if(DetectFileType.isCsv(fullPath)) {
			ret.setType("CSV");
		} else {
			ret.setType(DetectFileType.detectMimeType(r));;
		}
		
		return ret;
	}
	
	
}
