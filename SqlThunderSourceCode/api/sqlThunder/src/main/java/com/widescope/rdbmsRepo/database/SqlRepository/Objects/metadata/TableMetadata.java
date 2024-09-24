package com.widescope.rdbmsRepo.database.SqlRepository.Objects.metadata;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;

public class TableMetadata implements RestInterface{

	private Map<String, ColMetadata> lstColMetadata;
	private Map<String, Map<Integer, IndexMetadata>> lstIndexMetadata;
		
	public Map<String, ColMetadata> getLstColMetadata() {
		return lstColMetadata;
	}

	public void setLstColMetadata(Map<String, ColMetadata> lstColMetadata) {
		this.lstColMetadata = lstColMetadata;
	}
	public void addLstColMetadata(String key, ColMetadata val) {
		this.lstColMetadata.put(key, val) ;
	}
	
	public void addColMetadata(ColMetadata colMetadata) {
		this.lstColMetadata.put(colMetadata.COLUMN_NAME, colMetadata);
	}
	
	
	public Map<String, Map<Integer, IndexMetadata>> getLstIndexMetadata() {
		return lstIndexMetadata;
	}
	
	public void setLstIndexMetadata(Map<String, Map<Integer, IndexMetadata>> lstIndexMetadata) {
		this.lstIndexMetadata = lstIndexMetadata;
	}
	
	public void addIndexMetadata(IndexMetadata indexMetadata) {
		if(this.lstIndexMetadata.containsKey(indexMetadata.getINDEX_NAME())) {
			this.lstIndexMetadata.get(indexMetadata.getINDEX_NAME()).put(indexMetadata.getORDINAL_POSITION(), indexMetadata);
		} else {
			Map<Integer, IndexMetadata> e = new HashMap<>();
			e.put(indexMetadata.getORDINAL_POSITION(), indexMetadata);
			this.lstIndexMetadata.put(indexMetadata.getINDEX_NAME(), e);
		}
		
	}
	
	
	public TableMetadata(final Map<String, ColMetadata> lstColMetadata,
						 final Map<String, Map<Integer, IndexMetadata>> lstIndexMetadata) {
		this.setLstColMetadata(lstColMetadata);
		this.setLstIndexMetadata(lstIndexMetadata);
	}
	
	public TableMetadata() {
		this.setLstColMetadata(new HashMap<String, ColMetadata>());
		this.setLstIndexMetadata(new HashMap<String, Map<Integer, IndexMetadata>>());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
