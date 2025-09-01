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

package com.widescope.rdbmsRepo.database.elasticsearch.objects;

import com.google.gson.Gson;

public class ClusterVersion {
	private String number;
	private String buildFlavor;
	private String buildType;
	private String buildHash;
	private String buildDate;
	private String buildSnapshot;
	private String luceneVersion;
	private String minimumWireCompatibilityVersion;
	private String minimumIndexCompatibilityVersion;
	public String getNumber() {	return number; }
	public void setNumber(String number) { this.number = number; }
	public String getBuildFlavor() { return buildFlavor; }
	public void setBuildFlavor(String buildFlavor) { this.buildFlavor = buildFlavor; }
	public String getBuildType() { return buildType; }
	public void setBuildType(String buildType) { this.buildType = buildType; }
	public String getBuildHash() { return buildHash; }
	public void setBuildHash(String buildHash) { this.buildHash = buildHash; }
	public String getBuildDate() { return buildDate; }
	public void setBuildDate(String buildDate) { this.buildDate = buildDate; }
	public String getBuildSnapshot() { return buildSnapshot; }
	public void setBuildSnapshot(String buildSnapshot) { this.buildSnapshot = buildSnapshot; }
	public String getLuceneVersion() { return luceneVersion; }
	public void setLuceneVersion(String luceneVersion) { this.luceneVersion = luceneVersion; }
	public String getMinimumWireCompatibilityVersion() { return minimumWireCompatibilityVersion; }
	public void setMinimumWireCompatibilityVersion(String minimumWireCompatibilityVersion) { this.minimumWireCompatibilityVersion = minimumWireCompatibilityVersion; }
	public String getMinimumIndexCompatibilityVersion() { return minimumIndexCompatibilityVersion; }
	public void setMinimumIndexCompatibilityVersion(String minimumIndexCompatibilityVersion) { this.minimumIndexCompatibilityVersion = minimumIndexCompatibilityVersion; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
