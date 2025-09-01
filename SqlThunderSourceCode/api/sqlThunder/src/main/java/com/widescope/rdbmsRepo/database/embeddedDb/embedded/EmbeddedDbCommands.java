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

package com.widescope.rdbmsRepo.database.embeddedDb.embedded;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class EmbeddedDbCommands {

    private List<String> dbCommands;

    public EmbeddedDbCommands()	{
        this.setCommands(Arrays.asList(new String[] {
                "getalltables",
                "getallindexes",
                "gettablecolumns",
                "getcount",
                "getcolumntypes"
        }));
    }

    public List<String> getCommands() { return dbCommands; }
    public void setCommands(List<String> dbCommands) { this.dbCommands = dbCommands; }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
