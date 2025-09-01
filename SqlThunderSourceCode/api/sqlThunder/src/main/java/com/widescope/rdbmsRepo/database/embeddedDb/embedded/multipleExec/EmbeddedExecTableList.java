package com.widescope.rdbmsRepo.database.embeddedDb.embedded.multipleExec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class EmbeddedExecTableList {

	public static final Map<String, String> ASSEMBLY= new HashMap<String, String>();
	
	static {
        Map<String, String> ASSEMBLY = Map.of("UNION", "1", "GLUE", "2");
    }

	private List<EmbeddedExecTable> lst;
	private String assembly;  /*UNION/GLUE/SQL*/
	private String sqlFinal;  /*query executed if assembly = SQL is elected*/
	private long clusterId;
	
	public EmbeddedExecTableList() {
		this.setLst(new ArrayList<>());
		this.setAssembly("UNION");
		this.setSqlFinal("");
		this.setClusterId(-1);
	}

	public List<EmbeddedExecTable> getLst() { return lst; }
	public void setLst(List<EmbeddedExecTable> lst) { this.lst = lst; }
	
	public String getAssembly() {	return assembly; }
	public void setAssembly(String assembly) { this.assembly = assembly; }
	
	public String getSqlFinal() { return sqlFinal; }
	public void setSqlFinal(String sqlFinal) { this.sqlFinal = sqlFinal; }
	
	public long getClusterId() { return clusterId; }
	public void setClusterId(long clusterId) { this.clusterId = clusterId; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
