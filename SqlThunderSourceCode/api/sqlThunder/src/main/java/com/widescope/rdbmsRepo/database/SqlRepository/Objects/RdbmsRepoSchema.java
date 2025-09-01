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


public class RdbmsRepoSchema
{
	
	private int schema_id;
	public int getSchemaId() {	return schema_id; }
	public void setSchemaId(final int schema_id) { this.schema_id = schema_id; }
	
	private String schema_name;
	public String getSchemaName() {	return schema_name; }
	public void setSchemaName(final String schema_name) { this.schema_name = schema_name; }
	
	private String schema_service;
	public String getSchemaService() {	return schema_service; }
	public void setSchemaService(final String schema_service) { this.schema_service = schema_service; }
	
	private String schema_password;
	public String getSchemaPassword() {	return schema_password; }
	public void setSchemaPassword(final String schema_password) { this.schema_password = schema_password; }
	
	private String schema_description;
	public String getSchemaDescription() {	return schema_description; }
	public void setSchemaDescription(final String schema_description) { this.schema_description = schema_description; }
	
	private boolean schema_active;
	public boolean getSchemaActive() {	return schema_active; }
	public void setSchemaActive(final boolean schema_active) { this.schema_active = schema_active; }


}
