/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")){
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


package com.widescope.rdbmsRepo.database.tableFormat;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.ResultMetadata;


public class TableFormatOutput implements RestInterface  {
	private List<TableFormatMetadataOutput> metadata;
	private List<TableFormatExtMetadataOutput> extendedMetadata;
	private List<TableFormatRowOutput> rows;
	
	private int colCount;
	private long rowCount;
	
	private int type;   /*0 = pure union, when tables have perfectly identical columns*/
						/*1 = pure glue, shard like when tables have perfectly non-identical columns and row*/
						/*2 = a mix of the two, and assemble the tables as per a mix of glue/shard where possible */
	
	
	public List<TableFormatMetadataOutput> getMetadata() { return metadata; }
	public void setMetadata(List<TableFormatMetadataOutput> metadata) { this.metadata = metadata; }
	public List<TableFormatExtMetadataOutput> getExtendedMetadata() { return extendedMetadata; }
	public void setExtendedMetadata(List<TableFormatExtMetadataOutput> extendedMetadata) { this.extendedMetadata = extendedMetadata;}
	public List<TableFormatRowOutput> getRows() { return rows; }
	public void setRows(List<TableFormatRowOutput> rows) { this.rows = rows; }
	public int getColCount() { return colCount; }
	public void setColCount(int colCount) { this.colCount = colCount; }
	public long getRowCount() { return rowCount; }
	public void setRowCount(long rowCount) {this.rowCount = rowCount; }
	public int getType() { return type; }
	public void setType(int type) { this.type = type; }
	
	
	public TableFormatOutput() {
		this.setMetadata(new ArrayList<>());
		this.setExtendedMetadata(new ArrayList<>());
		this.setRows(new ArrayList<>());
		this.setColCount(0);
		this.setRowCount(0);
		this.setType(2);
	}
	
	public TableFormatOutput(final List<TableFormatMap> lst, String assembly) {
		TableFormatOutput r = assembleTables(lst);
		this.setMetadata(r.getMetadata());
		this.setExtendedMetadata(r.getExtendedMetadata());
		this.setRows(r.getRows());
		this.setColCount(r.getColCount());
		this.setRowCount(r.getRowCount());
		this.setType(r.type);
	}


	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	/**/
	private static TableFormatRowOutput 
	buildEmptyRow(final List<TableFormatMetadataOutput> metadataTmp ) {
		TableFormatRowOutput r = new TableFormatRowOutput();
		List<TableFormatCellOutput> row = new ArrayList<TableFormatCellOutput>();
		
		for(TableFormatMetadataOutput m: metadataTmp) {
			row.add(new TableFormatCellOutput(m.getColName(), m.getColPosition(), null));
		}
		
		r.setRow(row);
		return r;
	}
	
	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
	    return new HashSet<>(list1).equals(new HashSet<>(list2));
	}
	
	/**
	 * Used to decide if a UNION is permitted. 
	 * @param lst
	 * @return
	 */
	private static boolean checkIfPerfectIdenticalColumns(final List<TableFormatMap> lst) {
		for (int i = 1; i < lst.size(); i++) {
			if(!listEqualsIgnoreOrder( lst.get(i-1).getListOfColumns(), lst.get(i).getListOfColumns() )) {
				return false;
			} 
        }
		return true;
	}
	/**
	 * Used to decide if a UNION with different columns is possible
	 * If columns are completely different then, it means we cannot reconcile tables even if number of 
	 * rows are identical
	 * @param lst
	 * @return
	 */
	private static boolean checkIfCompletelyUnidenticalColumns(final List<TableFormatMap> lst) {
		for (int i = 1; i < lst.size(); i++) {
			final int cnt = i;
			List<String> differences1 = lst.get(cnt-1)	.getListOfColumns()
														.stream()
														.filter(element -> !lst.get(cnt).getListOfColumns()
																					.contains(element))
														.toList();
			
			List<String> differences2 = lst.get(cnt)	.getListOfColumns()
														.stream()
														.filter(element -> !lst.get(cnt-1).getListOfColumns()
														.contains(element))
														.toList();
			
			if(differences1.size() != lst.get(cnt-1).getListOfColumns().size()
					&& differences2.size() != lst.get(cnt).getListOfColumns().size()) {
				return false;
			} 
        }
		return true;
	}
	/*
	In case we have neither of the above, it means we have tables that can be assembled as shards, based on common columns
	This will be very expensive to assemble 
	*/
	
	private static List<String> 
	getIntersectedColumns(final List<TableFormatMap> lst) {
		List<String> ret = new ArrayList<> ();
		for (int i = 1; i < lst.size(); i++) {
			List<String> l1 = lst.get(i).getListOfColumns();
			List<String> l2 = lst.get(i-1).getListOfColumns();
			l1.retainAll(l2);
			if(ret.isEmpty()) {
				ret.addAll(l1);
			} else {
				ret.retainAll(l1);
			}
        }
		return ret;
	}
	
	
	
	
	private static TableFormatOutput 
	getUnionColumns(final List<TableFormatMap> lst, TableFormatOutput ret) {
		int colPosition = 0;
		int countTable = 0; 
		for(TableFormatMap t: lst) {
			countTable++;
			for (Map.Entry<String, String> entry : t.getMetadata().entrySet()) {
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			    TableFormatMetadataOutput m= new TableFormatMetadataOutput(entry.getKey(), entry.getValue(), colPosition);
			    Map<String, TableFormatMetadataOutput> metadataMap = ret.getMetadata().stream() .collect(Collectors.toMap(meta -> meta.getColName(), meta -> meta));
   			    //Set<String> metadataSet = metadata.stream().map(item->item.getColName()).collect(Collectors.toSet();
			    if(!metadataMap.containsKey(entry.getKey())) {
			    	ret.getMetadata().add(m);
				    ResultMetadata v =  t.getExtendedMetadata().get(entry.getKey());
				    TableFormatExtMetadataOutput mExtended= new TableFormatExtMetadataOutput(entry.getKey(), v, colPosition );
				    ret.getExtendedMetadata().add(mExtended);
				    colPosition++;
				    if(countTable > 1) {
				    	// we might have a glue, or a semi-glue tables
				    }
			    }
			}
		}
		
		return ret;
	}
	
	
	
	/**
	 * 
	 * @param lst
	 * @return
	 */
	private static TableFormatOutput aggregateColumns(final List<TableFormatMap> lst) {
		TableFormatOutput ret = new TableFormatOutput();
		ret = getUnionColumns(lst, ret);
		/*Get some measures about tables involved*/
		List<Integer> colsCnt = lst.stream().map(TableFormatMap::getColCount).distinct().toList();
		List<Long> rowsCnt = lst.stream().map(TableFormatMap::getRowCount).distinct().toList();
		boolean isIdenticalColumns = checkIfPerfectIdenticalColumns(lst); 
		boolean isUnidenticalColumns = checkIfCompletelyUnidenticalColumns(lst); 
		List<String> intersectColumns = getIntersectedColumns(lst);

		if(intersectColumns.size() == ret.getMetadata().size() 
				&& isIdenticalColumns 
				&& colsCnt.size() == 1) {
			/*
			A perfect union, Columns same in all tables, no matter the number of rows in each table.
			 */
			ret.setType(0);  
		} else if(!isIdenticalColumns 
				&& isUnidenticalColumns 
				&& rowsCnt.size() == 1
				&& colsCnt.size() == lst.size()) {
			/*
			A perfect shard/Glued set of tables, same number of rows at least one common unique column
			 */
			ret.setType(1);  
		} else {
			/*
			Mix of union and shard or a diagonal table.
			The result is a table with columns as SUM of all columns and rows as SUM of all rows.
			 */
			ret.setType(2); 
		}
		
		/*reinforce order by position*/
		Collections.sort(ret.getMetadata());
		Collections.sort(ret.getExtendedMetadata());
		
		return ret;
	}
	
	
	
	/**
	 * Aggregate/unionize all rows and all columns.
	 * @param lst
	 * @return
	 */
	private static TableFormatOutput assembleTables(final List<TableFormatMap> lst) {
		TableFormatOutput ret =  aggregateColumns(lst);
				
		//TableFormatRowOutput emptyRow = buildEmptyRow(metadata);
		for(TableFormatMap t: lst) {
			for(Map<String, Object> m:  t.getRows()) {
				TableFormatRowOutput r = buildEmptyRow(ret.getMetadata());;
				for (Map.Entry<String, Object> rowEntry : m.entrySet()) {
					String colName = rowEntry.getKey();
					Object colValue = rowEntry.getValue();
					System.out.println(colName + "/" + colValue);
					TableFormatCellOutput element = r.getRow().stream()
								.filter(tableFormatCellOutput -> colName.compareToIgnoreCase(tableFormatCellOutput.getColName()) == 0)
								.findAny()
								.orElse(null);
					int elementPosition = r.getRow().indexOf(element);
					
					r.getRow().set(elementPosition, new TableFormatCellOutput(colName, r.getRow().get(elementPosition).getColPosition(), colValue)) ;
				}
				r.order();
				ret.getRows().add(r);
			}
		}
		//rows.forEach(r-> Collections.sort(r.getRow()));
		ret.setColCount(ret.getMetadata().size());
		ret.setRowCount(ret.rows.size());
		return ret;
	}
	
	
	
}
