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


package com.widescope.rdbmsRepo.database.SqlRepository.Objects;

import java.util.ArrayList;
import java.util.List;




public class DmlBulkAffectedParam {
	private long dynamic_sql_id;
	public long getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final long dynamicSqlId) { this.dynamic_sql_id = dynamicSqlId; }
	
	private int grand_total_records_affected;
	public int getGrandTotalRecordsAffected() {	return grand_total_records_affected; }
	public void setGrandTotalRecordsAffected(final int grandTotalRecordsAffected) { this.grand_total_records_affected = grandTotalRecordsAffected; }
	
	private List<Integer> individual_records_affected;
	public List<Integer> getIndividualRecordsAffected() {	return individual_records_affected; }
	public void setIndividualRecordsAffected(final List<Integer> individualRecordsAffected) { this.individual_records_affected = individualRecordsAffected; }
	
	public void addIndividualRecordAffected(int individualRecordsAffected) { this.individual_records_affected.add(Integer.valueOf(individualRecordsAffected) ); }
	
	public DmlBulkAffectedParam() {
		this.dynamic_sql_id = 0;
		this.grand_total_records_affected = 0;
		this.individual_records_affected = new ArrayList<Integer>();
	}
	
	
	public DmlBulkAffectedParam(final long dynamic_sql_id, 
								final int grand_total_records_affected, 
								final  List<Integer> individual_records_affected) throws Exception	{
		this.dynamic_sql_id = dynamic_sql_id;
		this.grand_total_records_affected = grand_total_records_affected;
		this.individual_records_affected = individual_records_affected;
	}
	
	
	

}
