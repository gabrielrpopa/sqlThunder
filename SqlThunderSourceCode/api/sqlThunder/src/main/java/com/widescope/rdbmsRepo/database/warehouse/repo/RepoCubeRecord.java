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

package com.widescope.rdbmsRepo.database.warehouse.repo;

import com.google.gson.Gson;
import com.widescope.rest.RestInterface;


public class RepoCubeRecord implements RestInterface{
	private	long cubeId;
	private String uniqueName;
	private String path;
	private	String comment;
	private	long clusterIdSource;
	private	String clusterTypeSource;
	private	String isCompressedSource;
	private	long sqlIdSource;
	private	long cubeIdSource;
	private	long clusterIdDest;
	private	String clusterTypeDest;
	private	String entityDest;
	private	String isCompressedDest;
	
	
	public RepoCubeRecord(	final long cubeId,
							final String uniqueName,
							final String path,
							final String comment,
							final long clusterIdSource,
							final String clusterTypeSource,
							final String isCompressedSource,
							final long sqlIdSource,
							final long cubeIdSource,
							
							final long clusterIdDest,
							final String clusterTypeDest,
							final String entityDest,
							final String isCompressedDest
							
						) {
		this.setCubeId(cubeId);
		this.setUniqueName(uniqueName);
		this.setPath(path);
		this.setComment(comment);
		this.setClusterIdSource(clusterIdSource);
		this.setClusterTypeSource(clusterTypeSource);
		this.setIsCompressedSource(isCompressedSource);
		this.setSqlIdSource(sqlIdSource);
		this.setCubeIdSource(cubeIdSource);
		this.setClusterIdDest(clusterIdDest);
		this.setClusterTypeDest(clusterTypeDest);
		this.setEntityDest(entityDest);
		this.setIsCompressedDest(isCompressedDest);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


	public long getCubeId() { return cubeId; }
	public void setCubeId(long cubeId) { this.cubeId = cubeId; }

	public String getUniqueName() { return uniqueName; }
	public void setUniqueName(String uniqueName) { this.uniqueName = uniqueName; }

	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }


	public long getClusterIdSource() { return clusterIdSource; }
	public void setClusterIdSource(long clusterIdSource) { this.clusterIdSource = clusterIdSource; }

	public String getClusterTypeSource() { return clusterTypeSource; }
	public void setClusterTypeSource(String clusterTypeSource) { this.clusterTypeSource = clusterTypeSource; }

	public String getIsCompressedSource() { return isCompressedSource; }
	public void setIsCompressedSource(String isCompressedSource) { this.isCompressedSource = isCompressedSource; }

	public long getSqlIdSource() { return sqlIdSource; }
	public void setSqlIdSource(long sqlIdSource) { this.sqlIdSource = sqlIdSource; }

	public long getCubeIdSource() { return cubeIdSource; }
	public void setCubeIdSource(long cubeIdSource) { this.cubeIdSource = cubeIdSource; }

	public long getClusterIdDest() { return clusterIdDest; }
	public void setClusterIdDest(long clusterIdDest) { this.clusterIdDest = clusterIdDest; }
	
	public String getClusterTypeDest() { return clusterTypeDest; }
	public void setClusterTypeDest(String clusterTypeDest) { this.clusterTypeDest = clusterTypeDest; }
	
	public String getIsCompressedDest() { return isCompressedDest; }
	public void setIsCompressedDest(String isCompressedDest) { this.isCompressedDest = isCompressedDest; }

	public String getEntityDest() {
		return entityDest;
	}
	public void setEntityDest(String entityDest) {
		this.entityDest = entityDest;
	}

}
