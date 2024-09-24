package com.widescope.scripting.streaming;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class StreamingStatic {
	

	private static final
    ConcurrentHashMap<	String /*sessionId*/,
						ConcurrentHashMap<	String /*requestId*/, 
											TableVal > > 
	inmemStreamingDb = new ConcurrentHashMap<>();
	
	
	public static TableVal 
	getTableVal(final String sessionId, final String requestId) {
		return StreamingStatic.inmemStreamingDb.get(sessionId).get(requestId);
	}
	
	public static boolean 
	isTableVal(final String sessionId, final String requestId) {
		try {
			return StreamingStatic.inmemStreamingDb.get(sessionId).containsKey(requestId);
		} catch(NullPointerException ex) {
			return false;
		}
		
	}
	
	public static void
	initTableVal(	final String sessionId, 
					final String requestId,
					final List<ColumnDef> columnDefs
				) {
		TableVal t = new TableVal(columnDefs);
		StreamingStatic.inmemStreamingDb.get(sessionId).putIfAbsent(requestId, t);
	}
	
	public static void
	addTableValRow(	final String sessionId, 
					final String requestId,
					final RowVal row
					) {
		StreamingStatic.inmemStreamingDb.get(sessionId).get(requestId).addRow(row);
	}
	
	public static void
	addTableValRow(	final String sessionId, 
					final String requestId,
					final RowVal row,
					final boolean isDone
					) {
		StreamingStatic.inmemStreamingDb.get(sessionId).get(requestId).addRow(row, isDone);
	}
	
	
		
	
}
