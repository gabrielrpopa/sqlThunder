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


public class DmlBatchAffectedParam {
	private long dynamic_sql_id;
	public long getDynamicSqlId() {	return dynamic_sql_id; }
	public void setDynamicSqlId(final long dynamic_sql_id) { this.dynamic_sql_id = dynamic_sql_id; }
	
	private int grand_total_records_affected;
	public int getGrandTotalRecordsAffected() {	return grand_total_records_affected; }
	public void setGrandTotalRecordsAffected(final int grandTotalRecordsAffected) { this.grand_total_records_affected = grandTotalRecordsAffected; }
	
	private List<ParamObj> params_not_commited;
	public List<ParamObj> getParamsNotCommited() {	return params_not_commited; }
	public void setParamsNotCommited(final List<ParamObj> paramObjNotCommited) { this.params_not_commited = paramObjNotCommited; }
	
	public void addParamsNotCommited(final List<ParamObj> paramObjNotCommited) { this.params_not_commited.addAll(paramObjNotCommited); }
	public void addParamNotCommited(ParamObj paramObjNotCommited) { this.params_not_commited.add(paramObjNotCommited); }
	
	public DmlBatchAffectedParam() {
		this.dynamic_sql_id = 0;
		this.grand_total_records_affected = 0;
		this.params_not_commited = new ArrayList<ParamObj>();
	}


}
