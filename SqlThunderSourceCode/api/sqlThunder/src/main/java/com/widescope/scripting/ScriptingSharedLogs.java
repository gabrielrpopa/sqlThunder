package com.widescope.scripting;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ScriptingSharedLogs {
		
	private static final
    ConcurrentHashMap<	String /*sessionId*/, ConcurrentHashMap<String /*requestId*/, ScriptingSharedLogObject > >
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
		
		scriptSharedPool.get(sessionId).put(requestId, new ScriptingSharedLogObject());
	}
	
	public static void setStart(final String sessionId, 
								final String requestId, 
								final String typeScript) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).setStart(typeScript);
			}
		}
	}
	
	public static void setEnd(	final String sessionId, 
								final String requestId,
								final String count) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).setEnd(count);
			}
		}
	}
	
	
	public static void addLog(	final String sessionId, 
								final String requestId, 
								final String line) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).addLogToPool(line);
			}
		}
	}
	
	public static void addLogs(	final String sessionId, 
								final String requestId, 
								final List<String> lines) {
		if(sessionId != null && !scriptSharedPool.contains(sessionId)) {
			if(requestId!= null && scriptSharedPool.get(sessionId)!= null && !scriptSharedPool.get(sessionId).contains(requestId)) {
				scriptSharedPool.get(sessionId).get(requestId).addLogsToPool(lines);
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
	
	public static ScriptingSharedLogObject getCollectedData(final String sessionId, 
															final String requestId) {
		return scriptSharedPool	.get(sessionId).get(requestId);
	}
	
	
	

	public static Set<String> extractLogs(	final String sessionId, 
											final String requestId) {
		
		if(sessionId != null && scriptUsedPool.contains(sessionId)) {
			if(requestId!= null && scriptUsedPool.get(sessionId) != null && !scriptUsedPool.get(sessionId).contains(requestId)) {
				return new HashSet<String>();	
			}
		} else {
			return new HashSet<String>();	
		}
		
		Set<String> newKeys = scriptSharedPool	.get(sessionId)
												.get(requestId)
												.getPoolData()
												.stream()
												.filter(f -> !scriptUsedPool.get(sessionId).get(requestId).contains(f))
												.collect(Collectors.toSet());
		
		
		Set<String> ret = scriptSharedPool.get(sessionId)
											.get(requestId)
											.getPoolData()
											.stream().filter(newKeys::contains).collect(Collectors.toSet());
		
		
		scriptUsedPool.get(sessionId).get(requestId).addAll(newKeys);
		return ret;
		
	}
	
}
