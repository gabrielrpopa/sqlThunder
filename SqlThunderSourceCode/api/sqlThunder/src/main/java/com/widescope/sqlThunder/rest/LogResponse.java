/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.sqlThunder.rest;



public class LogResponse implements RestInterface {
	private long timestamp;
	private String job;
	private String artifactName;
	
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(final long timestamp) { this.timestamp = timestamp; }
	public String getJob() { return job; }
	public void setJob(final String job) { this.job = job; }
	public String getArtifactName() { return artifactName; }
	public void setArtifactName(final String artifactName) { this.artifactName = artifactName; }
	
	public LogResponse(	final long timestamp, 
						final String job, 
						final String artifactName)	{
		this.job = job;
		this.artifactName = artifactName;
	}

	
}
