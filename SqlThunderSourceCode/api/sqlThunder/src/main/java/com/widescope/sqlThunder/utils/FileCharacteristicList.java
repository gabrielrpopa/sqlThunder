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


package com.widescope.sqlThunder.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class FileCharacteristicList implements RestInterface {

	private List<FileCharacteristic> fileCharacteristicList;

	public FileCharacteristicList(final List<FileCharacteristic> l) {
		this.fileCharacteristicList = l;
	}
	public FileCharacteristicList() {
		this.fileCharacteristicList = new ArrayList<>();
	}

    public List<FileCharacteristic> getFileCharacteristicList() {
        return fileCharacteristicList;
    }

    public void setFileCharacteristicList(List<FileCharacteristic> fileCharacteristicList) {
        this.fileCharacteristicList = fileCharacteristicList;
    }



	@Override
	public String toString() {
		try	{
			Gson gson = new Gson();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}

	public String toStringPretty() {
		try	{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		}
		catch(Exception ex) {
			return null;
		}
	}



	public static FileCharacteristicList toFileCharacteristicList(String str) {
		Gson gson = new Gson();
		try	{
			return gson.fromJson(str, FileCharacteristicList.class);
		}
		catch(JsonSyntaxException ex) {
			return null;
		}
	}

}
