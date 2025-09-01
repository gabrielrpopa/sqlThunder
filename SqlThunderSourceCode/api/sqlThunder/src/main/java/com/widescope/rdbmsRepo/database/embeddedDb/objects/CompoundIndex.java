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

package com.widescope.rdbmsRepo.database.embeddedDb.objects;

import com.google.gson.Gson;

import java.util.List;

public class CompoundIndex {

    private String isIndexUnique;
    public String getIsIndexUnique() { return isIndexUnique; }
    public void setIsIndexUnique(final String isIndexUnique) { this.isIndexUnique = isIndexUnique; }


    private List<String> listOfColumns;
    public List<String> getListOfColumns() { return listOfColumns; }
    public void setListOfColumns(final List<String> listOfColumns) { this.listOfColumns = listOfColumns; }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
