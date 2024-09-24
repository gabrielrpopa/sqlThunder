package com.widescope.rdbmsRepo.database.structuredFiles.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.ColMetadata;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata.TableMetadata;
import com.widescope.rdbmsRepo.database.rdbmsRepository.SqlMetadataWrapper;
import com.widescope.rdbmsRepo.database.tableFormat.TableFormatMap;
import com.widescope.sqlThunder.utils.compression.InMemCompression;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.CDL;

public class CsvWrapper {
	public static TableFormatMap 
	stringToTable(	final String csvContent, 
					TableMetadata tM,
					final String isCompressed) throws Exception {
		TableFormatMap ret = new TableFormatMap();
		List<String> headers = new ArrayList<String>();
		List<String[]> csv = new ArrayList<String[]>();
        CSVParser parser = new CSVParserBuilder()	.withSeparator(',')
                                                    .withIgnoreQuotations(true)
                                                    .build();

        if(isCompressed.toUpperCase().equals("Y")) {
            byte[] uncompressed = null;
            try {
                uncompressed = InMemCompression.decompressZlib(csvContent.getBytes());
            } catch(Exception ex1) {
                uncompressed =InMemCompression.decompressGZIP(csvContent.getBytes());
            }
            String str = new String(uncompressed);
            try (var br = new BufferedReader(new StringReader(str))) {
                var reader = new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(0).build();
                csv = reader.readAll();
            }
        } else {
            try (var br = new BufferedReader(new StringReader(csvContent))) {
                var reader = new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(0).build();
                csv = reader.readAll();
            }
        }
        String[] h = csv.get(0);
        headers = Arrays.asList(h);

        Map<String, ColMetadata> lstColMetadata = new HashMap<String, ColMetadata>();
		if(tM == null) {
			tM = new TableMetadata();
			Map<String, CsvHeader> lstColMetadataH = new HashMap<String, CsvHeader>();
            for (String header : headers) {
                lstColMetadataH.put(header, new CsvHeader(header, "", false, 0, 0));
            }
			
			/*analyze*/
			for (int i = 1; i < csv.size(); i++) {
				String[] rawRow= csv.get(i);
				for (int j = 0; j < rawRow.length; j++) {
					String hdName = headers.get(j);
					if(NumberUtils.isCreatable(rawRow[j])) {
						lstColMetadataH.get(hdName).incrementIsNumberCount();
					} 
					lstColMetadataH.get(hdName).incrementTotalCount();
				}
	        }
			
			for(Entry<String, CsvHeader> entry: lstColMetadataH.entrySet()) {
				if(entry.getValue().getIsNumberCount() == entry.getValue().getTotalCount()) {
					ret.addMetadata(entry.getKey(), SqlMetadataWrapper.getType(Types.FLOAT) );
					lstColMetadata.put(entry.getKey(), new ColMetadata(Types.FLOAT)  );
				} else {
					ret.addMetadata(entry.getKey(), SqlMetadataWrapper.getType(Types.VARCHAR) );
					lstColMetadata.put(entry.getKey(), new ColMetadata(Types.VARCHAR)  );
				}
			}
			
			
			
		} else {
			lstColMetadata = tM.getLstColMetadata();
			for(Entry<String, ColMetadata> entry: lstColMetadata.entrySet()) {
			      ret.addMetadata(entry.getKey(), SqlMetadataWrapper.getType(entry.getValue().DATA_TYPE) );
			}
		}
		
		
		
		/*skip the first row / header*/
		for (int i = 1; i < csv.size(); i++) {
			Map<String,Object> row = new HashMap<>();
			
			String[] rawRow= csv.get(i);
			for (int j = 0; j < rawRow.length; j++) {
				String hdName = headers.get(j);
				Object val = new Object();
				val = SqlMetadataWrapper.getTypeValue(lstColMetadata.get(hdName).DATA_TYPE, rawRow[j]);
				row.put(hdName, val);
			}
			ret.addRow(row);
        }
		return ret;
	}
	
	
	
	public static List<String> 
	stringToJsonList(	final String csvContent, 
						final String isCompressed) throws Exception {
		List<String> ret = new ArrayList<>();
		
		List<String> headers = new ArrayList<String>();
		List<String[]> csv = new ArrayList<String[]>();
		try	{
	
			CSVParser parser = new CSVParserBuilder()	.withSeparator(',')
				    									.withIgnoreQuotations(false)
				    									.withQuoteChar('"')
				    									.build();
			
			if(isCompressed.equalsIgnoreCase("Y")) {
				byte[] uncompressed = null;
				try {
					uncompressed = InMemCompression.decompressZlib(csvContent.getBytes());
				} catch(Exception ex1) {
					try {
						uncompressed =InMemCompression.decompressGZIP(csvContent.getBytes());
					} catch(Exception ex2) {
						throw ex2;
					}
				}
				String str = new String(uncompressed);
			    try (var br = new BufferedReader(new StringReader(str))) {
			        var reader = new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(0).build();
			        csv = reader.readAll();
			    }
			} else {
				try (var br = new BufferedReader(new StringReader(csvContent))) {
					var reader = new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(0).build();
					csv = reader.readAll();
				}
			}
			String[] h = csv.get(0);
			headers = Arrays.asList(h);
			
		} catch(Exception ex) {
			throw ex;
		}
		
		
		Map<String, CsvHeader> lstColMetadata = new HashMap<String, CsvHeader>();
		for (int i = 0; i < headers.size(); i++) {
			lstColMetadata.put(headers.get(i), new CsvHeader(headers.get(i),"",false,0,0));
		}
		
		/*analyze*/
		for (int i = 1; i < csv.size(); i++) {
			String[] rawRow= csv.get(i);
			for (int j = 0; j < rawRow.length; j++) {
				String hdName = headers.get(j);
				if(NumberUtils.isCreatable(rawRow[j])) {
					lstColMetadata.get(hdName).incrementIsNumberCount();
				} 
				lstColMetadata.get(hdName).incrementTotalCount();
			}
        }
		
		
		
		/*skip the first row / header*/
		for (int i = 1; i < csv.size(); i++) {
			String[] rawRow= csv.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (int j = 0; j < rawRow.length; j++) {
				String hdName = headers.get(j);
				if( lstColMetadata.get(hdName).getIsNumberCount() == lstColMetadata.get(hdName).getTotalCount()) {
					sb.append("\"").append(hdName).append("\"").append(": ").append(rawRow[j]);
				} else {
					sb.append("\"").append(hdName).append("\"").append(": ").append("\"").append(rawRow[j]).append("\"");
				}
				if(j < rawRow.length -1) sb.append(",");
			}
			sb.append("}");
			ret.add(sb.toString());
        }
		return ret;
	}
	
	public static String 
	readFile(final String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.lineSeparator();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();
        return stringBuilder.toString();
	}
	
	
	
	
}
