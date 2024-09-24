package com.widescope.rdbmsRepo.database.mongodb.sql.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;
import com.widescope.rdbmsRepo.database.mongodb.ComplexAndSearch;
import com.widescope.rdbmsRepo.database.mongodb.Range;

public class ComplexAndMongoSearch implements RestInterface {

	private Map<String, Range> range;
	private Map<String, Object> equal;
	private Map<String, Object> lessThan;
	private Map<String, Object> greaterThan;
	private Map<String, Object> like;
	private Map<String, List<Object>> in;   /*maybe better List<? extends Object>  or List<?> to be reifiable*/
	private Map<String, List<Object>> notIn;
	private Map<String, Integer> sort;
	private int fromRow;
	private int noRow;
	

	public ComplexAndMongoSearch() {
		this.setRange(new HashMap<String, Range>());
		this.setEqual(new HashMap<String, Object>());
		this.setLessThan(new HashMap<String, Object>());
		this.setGreaterThan(new HashMap<String, Object>());
		this.setLike(new HashMap<String, Object>());
		this.setIn(new HashMap<String, List<Object>>());
		this.setNotIn(new HashMap<String, List<Object>>());
		this.setSort(new HashMap<String, Integer>());
		this.setFromRow(-1);
		this.setNoRow(-1);
	}

	public Map<String, Range> getRange() { return range; }
	public void setRange(Map<String, Range> range) { this.range = range; }

	public Map<String, Object> getEqual() { return equal; }
	public void setEqual(Map<String, Object> equal) { this.equal = equal; }

	public Map<String, Object> getLessThan() { return lessThan; }
	public void setLessThan(Map<String, Object> lessThan) { this.lessThan = lessThan; }

	public Map<String, Object> getGreaterThan() { return greaterThan; }
	public void setGreaterThan(Map<String, Object> greaterThan) { this.greaterThan = greaterThan; }

	public Map<String, Object> getLike() { return like; }
	public void setLike(Map<String, Object> like) { this.like = like; }

	public Map<String, List<Object>> getIn() { return in; }
	public void setIn(Map<String, List<Object>> in) { this.in = in; }

	public Map<String, List<Object>> getNotIn() { return notIn; }
	public void setNotIn(Map<String, List<Object>> notIn) { this.notIn = notIn; }

	public Map<String, Integer> getSort() {	return sort; }
	public void setSort(Map<String, Integer> sort) { this.sort = sort; }

	public int getFromRow() { return fromRow; }
	public void setFromRow(int fromRow) { this.fromRow = fromRow; }

	public int getNoRow() { return noRow; }
	public void setNoRow(int noRow) { this.noRow = noRow; }



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public static ComplexAndMongoSearch fromComplexAndSearch(final ComplexAndSearch r) {
		
		ComplexAndMongoSearch c = new ComplexAndMongoSearch();
		
		c.setRange(r.getRange());
		c.setEqual(r.getEqual());
		c.setLessThan(r.getLessThan());
		c.setGreaterThan(r.getGreaterThan());
		c.setLike(r.getLike());
		c.setIn(r.getIn());
		c.setNotIn(r.getNotIn());
		c.setSort(r.getSort());
		c.setFromRow(r.getFromRow());
		c.setNoRow(r.getNoRow());
		
		
		return c;
		
	}
	
	public boolean addRange(final String name, Range r) {
		if(getRange() == null) {
			setRange(new HashMap<>());
		}
		
		if( !getRange().containsKey(name) ) {
			getRange().put(name, com.widescope.rdbmsRepo.database.mongodb.Range.getRangeQuery(name));
			return true;
		} else {
			return false;
		}
	}

	public boolean addEqual(final String name) {
		if(getEqual() == null) {
			setEqual(new HashMap<>());
		}
		
		if( !getEqual().containsKey(name) ) {
			getEqual().put(name, "@equal@");
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean addLessThan(final String name) {
		if(getLessThan() == null) {
			setLessThan(new HashMap<>());
		}
		
		if( !getLessThan().containsKey(name) ) {
			getLessThan().put(name, "@lessThan@");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addGreaterThan(final String name) {
		if(getGreaterThan() == null) {
			setGreaterThan(new HashMap<>());
		}
		
		if( !getGreaterThan().containsKey(name) ) {
			getGreaterThan().put(name, "@greaterThan@");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addLike(final String name) {
		if(getLike() == null) {
			setLike(new HashMap<>());
		}
		
		if( !getLike().containsKey(name) ) {
			getLike().put(name, "@like@");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addIn(final String name, int count) {
		if(getIn() == null) {
			setIn(new HashMap<>());
		}
		
		if( !getIn().containsKey(name) ) {
			
			List<Object> inList = new ArrayList<>();
			for(int x = 0;x < count; x++) {
				inList.add("@in" + count + "@");
			}
			
			getIn().put(name, inList);
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public boolean addNotIn(final String name, int count) {
		if(getNotIn() == null) {
			setNotIn(new HashMap<>());
		}
		
		if( !getNotIn().containsKey(name) ) {
			List<Object> notInList = new ArrayList<>();
			for(int x = 0;x < count; x++) {
				notInList.add("@in" + count + "@");
			}
			getNotIn().put(name, notInList);
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean addSort(final String name, int sort) {
		if(getSort() == null) {
			setSort(new HashMap<>());
		}
		
		if( !getSort().containsKey(name) ) {
			getSort().put(name, sort);
			return true;
		} else {
			return false;
		}
	}
	

	
	
	
}
