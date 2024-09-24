package com.widescope.scripting;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.widescope.rdbmsRepo.database.tableFormat.RowValue;
import com.widescope.rdbmsRepo.database.tableFormat.TableDefinition;

public class ScriptingSharedData {
		
	private static final
    ConcurrentHashMap<	String /*sessionId*/, ConcurrentHashMap<String /*requestId*/, ScriptingSharedDataObject > >
	scriptSharedPool = new ConcurrentHashMap<>();
		
	private static final
    ConcurrentHashMap<	String /*sessionId*/,
						ConcurrentHashMap<	String /*requestId*/, 
											List<String> > > 
	scriptUsedPool = new ConcurrentHashMap<>();
	
	
	public static long getCurrentScriptsRunning() {
		long cnt = 0;
		for(String sessionId : scriptSharedPool.keySet()) {
			for(String requestId : scriptSharedPool.get(sessionId).keySet()) {
				cnt += scriptUsedPool.get(sessionId).get(requestId).size();
			}
		}
		return cnt;
	}
	
	public static void addEmptyEntryLog(	final String sessionId, 
											final String requestId) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId))
			scriptSharedPool.put(sessionId, new ConcurrentHashMap<>());
		
		scriptSharedPool.get(sessionId).put(requestId, new ScriptingSharedDataObject());
	}
	
	public static void addLog(	final String sessionId, 
								final String requestId, 
								final RowValue line) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).addLogToPool(line);
			}
		}
		
	}
	
	public static void addLogs(	final String sessionId, 
								final String requestId, 
								final List<RowValue> lines) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).addLogsToPool(lines);
			}
		}
	}
	
	
	public static void addLogTableDefinition(	final String sessionId, 
										final String requestId, 
										final TableDefinition tableDefinition) {
		if(sessionId!=null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).setTableDefinition(tableDefinition);
			}
		}
	}
	
	public static void addLogTableFooter(	final String sessionId, 
											final String requestId, 
											final RowValue rowValue) {
		if(sessionId!=null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).setTableFooter(rowValue);
			}
		}
	}
	
	
	public static void removeRequestData(	final String sessionId, 
											final String requestId) {
		if(sessionId!=null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).remove(requestId);
			}
		}
		
		if(sessionId!=null && !scriptUsedPool.contains(sessionId)) {
			if(requestId!= null && scriptUsedPool.get(sessionId)!= null && !scriptUsedPool.get(sessionId).contains(requestId)) {
				scriptUsedPool.get(sessionId).remove(requestId);	
			}
		} 
	}
	
	public static void removeSessionData(final String sessionId) {
		if(sessionId != null && scriptSharedPool.get(sessionId) != null ) {
			scriptSharedPool.remove(sessionId);
		}
		
		if(sessionId != null && scriptUsedPool.get(sessionId) != null ) {
			scriptUsedPool.remove(sessionId);
		}
			
	}
	
	public static ScriptingSharedDataObject getCollectedData(	final String sessionId, 
															final String requestId) {
		return scriptSharedPool	.get(sessionId).get(requestId);
	}
	
	public static ScriptingReturnObject getCollectedDataToUser(	final String sessionId,
																final String requestId) {
		return ScriptingReturnObject.toScriptingReturnObject(scriptSharedPool	.get(sessionId).get(requestId));
	}
		
	
	/**
	 * Extract unsent logs, and whatever I sent I save as reference in scriptUsedPool
	 * @param sessionId
	 * @param requestId
	 * @return
	 */
	public static Set<RowValue> extractLogs(final String sessionId, 
											final String requestId) {
		
		if(sessionId != null && scriptUsedPool.contains(sessionId)) {
			if(requestId!= null && scriptUsedPool.get(sessionId) != null && !scriptUsedPool.get(sessionId).contains(requestId)) {
				return new HashSet<RowValue>();	
			}
		} else {
			return new HashSet<RowValue>();	
		}
		
		Set<String> newKeys = scriptSharedPool	.get(sessionId)
												.get(requestId)
												.getPoolData()
												.keySet()
												.stream()
												.filter(f -> !scriptUsedPool.get(sessionId).get(requestId).contains(f))
												.collect(Collectors.toSet());
		
		
		Set<RowValue> ret = scriptSharedPool.get(sessionId)
											.get(requestId)
											.getPoolData()
											.values().stream().filter(newKeys::contains).collect(Collectors.toSet());
		
		
		scriptUsedPool.get(sessionId).get(requestId).addAll(newKeys);
		return ret;
		
	}
	
}
