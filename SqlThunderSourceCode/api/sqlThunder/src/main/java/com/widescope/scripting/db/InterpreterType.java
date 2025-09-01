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


package com.widescope.scripting.db;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;

public class InterpreterType implements RestInterface
{
	private int interpreterId;
	private String interpreterName;  /* com.widescope.scripting.interpreter.InterpreterName */
	private String interpreterVersion;
	private String interpreterPath;
	private String command;
	private String fileExtensions;
	
	public InterpreterType(	final int interpreterId,
							final String interpreterName,
							String interpreterVersion,
							String interpreterPath,
							String command,
							String fileExtensions)	{
		this.setInterpreterId(interpreterId);
		this.setInterpreterName(interpreterName);
		this.setInterpreterVersion(interpreterVersion);
		this.setInterpreterPath(interpreterPath);
		this.setCommand(command);
		this.setFileExtensions(fileExtensions);
	}
	
	
	public InterpreterType() { 
		this.setInterpreterId(-1);
		this.setInterpreterName("");
		this.setInterpreterVersion("");
		this.setInterpreterPath("");
		this.setCommand("");
		this.setFileExtensions("");
	}
	
	public int getInterpreterId() { return interpreterId; }
	public void setInterpreterId(int interpreterId) { this.interpreterId = interpreterId; }
	
	public String getInterpreterName() { return interpreterName; }
	public void setInterpreterName(String interpreterName) { this.interpreterName = interpreterName; }
	
	public String getInterpreterVersion() { return interpreterVersion; }
 	public void setInterpreterVersion(String interpreterVersion) { this.interpreterVersion = interpreterVersion; }
	
	public String getInterpreterPath() { return interpreterPath; }
	public void setInterpreterPath(String interpreterPath) { this.interpreterPath = interpreterPath; }
	
	public String getCommand() { return command; }
	public void setCommand(String command) { this.command = command; }
	
	public String getFileExtensions() { return fileExtensions; }
	public void setFileExtensions(String fileExtensions) { this.fileExtensions = fileExtensions; }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
